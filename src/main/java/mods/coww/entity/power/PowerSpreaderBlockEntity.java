package mods.coww.entity.power;

import com.google.common.base.Predicates;
import mods.coww.api.callbacks.AddCollectorCallback;
import mods.coww.api.internal.PowerBurstInterface;
import mods.coww.api.lens.LensControlInterface;
import mods.coww.api.lens.LensEffectInterface;
import mods.coww.api.manipulation.WandBindableInterface;
import mods.coww.api.power.*;
import mods.coww.entity.AnvilInventory;
import mods.coww.power.internal.PowerNetworkSerializer;
import mods.coww.registry.cowwBlocks;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RayTraceContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static mods.coww.client.rendering.FluidHandlingRaytrace.raytraceFromPlayer;

public class PowerSpreaderBlockEntity extends BlockEntity implements AnvilInventory, PowerCollectorInterface, WandBindableInterface, ThrottledPacketInterface, PowerSpreaderInterface, DirectionedInterface, Tickable, BlockEntityClientSerializable {

    private static final int MAX_POWER = 1000;
    private static final int ULTRA_MAX_POWER = 6400;
    private static final int TICKS_ALLOWED_WITHOUT_PINGBACK = 20;
    private static final double PINGBACK_EXPIRED_SEARCH_DISTANCE = 0.5;

    private static final String TAG_HAS_IDENTITY = "hasIdentity";
    private static final String TAG_UUID_MOST = "uuidMost";
    private static final String TAG_UUID_LEAST = "uuidLeast";
    private static final String TAG_POWER = "power";
    private static final String TAG_KNOWN_POWER = "knownPower";
    private static final String TAG_REQUEST_UPDATE = "requestUpdate";
    private static final String TAG_ROTATION_X = "rotationX";
    private static final String TAG_ROTATION_Y = "rotationY";
    private static final String TAG_PADDING_COLOR = "paddingColor";
    private static final String TAG_CAN_SHOOT_BURST = "canShootBurst";
    private static final String TAG_PINGBACK_TICKS = "pingbackTicks";
    private static final String TAG_LAST_PINGBACK_X = "lastPingbackX";
    private static final String TAG_LAST_PINGBACK_Y = "lastPingbackY";
    private static final String TAG_LAST_PINGBACK_Z = "lastPingbackZ";

    private static final String TAG_FORCE_CLIENT_BINDING_X = "forceClientBindingX";
    private static final String TAG_FORCE_CLIENT_BINDING_Y = "forceClientBindingY";
    private static final String TAG_FORCE_CLIENT_BINDING_Z = "forceClientBindingZ";

    public static final boolean staticRedstone = false;
    public static final boolean staticDreamwood = false;
    public static final boolean staticUltra = false;

    UUID identity;

    private int power;
    private int knownPower = -1;
    public float rotationX, rotationY;

    @Nullable
    public DyeColor paddingColor = null;

    private boolean requestsClientUpdate = false;
    private boolean hasReceivedInitialPacket = false;

    private PowerReceiverInterface receiver = null;
    private PowerReceiverInterface receiverLastTick = null;

    private boolean redstoneLastTick = true;
    public boolean canShootBurst = true;
    public int lastBurstDeathTick = -1;
    public int burstParticleTick = 0;

    public int pingbackTicks = 0;
    public double lastPingbackX = 0;
    public double lastPingbackY = -1;
    public double lastPingbackZ = 0;

    private List<PowerBurstEntity.PositionProperties> lastTentativeBurst;
    private boolean invalidTentativeBurst = false;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public PowerSpreaderBlockEntity() {
        super(cowwBlocks.POWER_SPREADER_BLOCK_ENTITY);
    }
    
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public boolean isFull() {
        return power >= getMaxPower();
    }

    @Override
    public void receivePower(int power) {
        this.power = Math.min(this.power + power, getMaxPower());
        this.markDirty();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        PowerNetworkEvent.INSTANCE.removeCollector(this);
    }

    //@Override
    public void onChunkUnloaded() {
        //super.onChunkUnloaded();
        //PowerNetworkEvent.INSTANCE.removeCollector(this);
    }

    @Override
    public void tick() {
        boolean inNetwork = PowerNetworkSerializer.INSTANCE.isCollectorIn(this);
        boolean wasInNetwork = inNetwork;
        if(!inNetwork && !isRemoved()) {
            PowerNetworkEvent.INSTANCE.addCollector(this);
        }

        boolean redstone = false;

        for(Direction dir : Direction.values()) {
            BlockEntity beAtOffset = world.getBlockEntity(pos.offset(dir));
            if(world.isChunkLoaded(pos.offset(dir)) && beAtOffset instanceof PowerTankInterface) {
                PowerTankInterface pool = (PowerTankInterface) beAtOffset;
                if(wasInNetwork && (pool != receiver || isRedstone())) {

                    int powerInPool = pool.getCurrentPower();
                    if(powerInPool > 0 && !isFull()) {
                        int powerMissing = getMaxPower() - power;
                        int powerToRemove = Math.min(powerInPool, powerMissing);
                        pool.receivePower(-powerToRemove);
                        receivePower(powerToRemove);
                    }
                }
            }

            int redstoneSide = world.getEmittedRedstonePower(pos.offset(dir), dir);
            if(redstoneSide > 0)
                redstone = true;
        }

        if(needsNewBurstSimulation())
            checkForReceiver();

        if(!canShootBurst)
            if(pingbackTicks <= 0) {
                double x = lastPingbackX;
                double y = lastPingbackY;
                double z = lastPingbackZ;
                Box box = new Box(x, y, z, x, y, z).expand(PINGBACK_EXPIRED_SEARCH_DISTANCE, PINGBACK_EXPIRED_SEARCH_DISTANCE, PINGBACK_EXPIRED_SEARCH_DISTANCE);
                List bursts = world.getEntities(ThrownEntity.class, box, Predicates.instanceOf(PowerBurstInterface.class));
                PowerBurstInterface found = null;
                UUID identity = getIdentifier();
                for(PowerBurstInterface burst : (List<PowerBurstInterface>) bursts)
                    if(burst != null && identity.equals(burst.getShooterUUID())) {
                        found = burst;
                        break;
                    }

                if(found != null)
                    found.ping();
                else setCanShoot(true);
            } else pingbackTicks--;

        boolean shouldShoot = !redstone;

        boolean isredstone = isRedstone();
        if(isredstone)
            shouldShoot = redstone && !redstoneLastTick;

        ItemStack lens = getInvStack(0);
        LensControlInterface control = getLensController(lens);
        if(control != null) {
            if(isredstone) {
                if(shouldShoot)
                    control.onControlledSpreaderPulse(lens, this, redstone);
            } else control.onControlledSpreaderTick(lens, this, redstone);

            shouldShoot &= control.allowBurstShooting(lens, this, redstone);
        }

        if(shouldShoot)
            tryShootBurst();

        if(receiverLastTick != receiver && !world.isClient) {
            requestsClientUpdate = true;
            //todo sync
            sync();
        }

        redstoneLastTick = redstone;
        receiverLastTick = receiver;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        UUID identity = getIdentifier();
        tag.putBoolean(TAG_HAS_IDENTITY, true);
        tag.putLong(TAG_UUID_MOST, identity.getMostSignificantBits());
        tag.putLong(TAG_UUID_LEAST, identity.getLeastSignificantBits());

        tag.putInt(TAG_POWER, power);
        tag.putFloat(TAG_ROTATION_X, rotationX);
        tag.putFloat(TAG_ROTATION_Y, rotationY);
        tag.putBoolean(TAG_REQUEST_UPDATE, requestsClientUpdate);
        tag.putInt(TAG_PADDING_COLOR, paddingColor == null ? -1 : paddingColor.getId());
        tag.putBoolean(TAG_CAN_SHOOT_BURST, canShootBurst);

        tag.putInt(TAG_PINGBACK_TICKS, pingbackTicks);
        tag.putDouble(TAG_LAST_PINGBACK_X, lastPingbackX);
        tag.putDouble(TAG_LAST_PINGBACK_Y, lastPingbackY);
        tag.putDouble(TAG_LAST_PINGBACK_Z, lastPingbackZ);

        tag.putInt(TAG_FORCE_CLIENT_BINDING_X, receiver == null ? 0 : ((BlockEntity) receiver).getPos().getX());
        tag.putInt(TAG_FORCE_CLIENT_BINDING_Y, receiver == null ? -1 : ((BlockEntity) receiver).getPos().getY());
        tag.putInt(TAG_FORCE_CLIENT_BINDING_Z, receiver == null ? 0 : ((BlockEntity) receiver).getPos().getZ());
        requestsClientUpdate = false;
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        if(tag.getBoolean(TAG_HAS_IDENTITY)) {
            long most = tag.getLong(TAG_UUID_MOST);
            long least = tag.getLong(TAG_UUID_LEAST);
            UUID identity = getIdentifierUnsafe();
            if(identity == null || most != identity.getMostSignificantBits() || least != identity.getLeastSignificantBits())
                this.identity = new UUID(most, least);
        } else getIdentifier();

        power = tag.getInt(TAG_POWER);
        rotationX = tag.getFloat(TAG_ROTATION_X);
        rotationY = tag.getFloat(TAG_ROTATION_Y);
        requestsClientUpdate = tag.getBoolean(TAG_REQUEST_UPDATE);

        if(tag.contains(TAG_KNOWN_POWER))
            knownPower = tag.getInt(TAG_KNOWN_POWER);
        if(tag.contains(TAG_PADDING_COLOR))
            paddingColor = tag.getInt(TAG_PADDING_COLOR) == -1 ? null : DyeColor.byId(tag.getInt(TAG_PADDING_COLOR));
        if(tag.contains(TAG_CAN_SHOOT_BURST))
            canShootBurst = tag.getBoolean(TAG_CAN_SHOOT_BURST);

        pingbackTicks = tag.getInt(TAG_PINGBACK_TICKS);
        lastPingbackX = tag.getDouble(TAG_LAST_PINGBACK_X);
        lastPingbackY = tag.getDouble(TAG_LAST_PINGBACK_Y);
        lastPingbackZ = tag.getDouble(TAG_LAST_PINGBACK_Z);

        if(requestsClientUpdate && world != null) {
            int x = tag.getInt(TAG_FORCE_CLIENT_BINDING_X);
            int y = tag.getInt(TAG_FORCE_CLIENT_BINDING_Y);
            int z = tag.getInt(TAG_FORCE_CLIENT_BINDING_Z);
            if(y != -1) {
                BlockEntity tile = world.getBlockEntity(new BlockPos(x, y, z));
                if(tile instanceof PowerReceiverInterface)
                    receiver = (PowerReceiverInterface) tile;
                else receiver = null;
            } else receiver = null;
        }

        if(world != null && world.isClient)
            hasReceivedInitialPacket = true;
    }

    @Override
    public boolean canReceivePowerFromBursts() {
        return true;
    }

    @Override
    public int getCurrentPower() {
        return power;
    }

    public void onWanded(PlayerEntity player, ItemStack wand) {
        if(player == null || world == null)
            return;

        if(!player.isSneaking()) {
            if(!world.isClient) {
                CompoundTag wandedTag = new CompoundTag();
                toTag(wandedTag);
                wandedTag.putInt(TAG_KNOWN_POWER, power);
                if(player instanceof ServerPlayerEntity)
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(new BlockEntityUpdateS2CPacket(pos, -999, wandedTag));
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, SoundCategory.PLAYERS, 0.1F, 1);
        } else {
            BlockHitResult pos = raytraceFromPlayer(world, player, RayTraceContext.FluidHandling.ANY,5);
            if(pos != null && !world.isClient) {
                double x = pos.getPos().x - getPos().getX() - 0.5;
                double y = pos.getPos().y - getPos().getY() - 0.5;
                double z = pos.getPos().z - getPos().getZ() - 0.5;

                if(pos.getSide() != Direction.DOWN && pos.getSide() != Direction.UP) {
                    Vec3d clickVector = new Vec3d(x, 0, z);
                    Vec3d relative = new Vec3d(-0.5, 0, 0);
                    double angle = Math.acos(clickVector.dotProduct(relative) / (relative.length() * clickVector.length())) * 180D / Math.PI;

                    rotationX = (float) angle + 180F;
                    if(clickVector.z < 0)
                        rotationX = 360 - rotationX;
                }

                double angle = y * 180;
                rotationY = -(float) angle;

                checkForReceiver();
                requestsClientUpdate = true;
                sync();
            }
        }
    }

    private boolean needsNewBurstSimulation() {
        if(world.isClient && !hasReceivedInitialPacket)
            return false;

        if(lastTentativeBurst == null)
            return true;

        for(PowerBurstEntity.PositionProperties props : lastTentativeBurst)
            if(!props.contentsEqual(world)) {
                invalidTentativeBurst = props.invalid;
                return !invalidTentativeBurst;
            }

        return false;
    }

    private void tryShootBurst() {
        if((receiver != null || isRedstone()) && !invalidTentativeBurst) {
            if(canShootBurst && (isRedstone() || receiver.canReceivePowerFromBursts() && !receiver.isFull())) {
                PowerBurstEntity burst = getBurst(false);
                if(burst != null) {
                    if(!world.isClient) {
                        power -= burst.getStartingPower();
                        burst.setShooterUUID(getIdentifier());
                        world.spawnEntity(burst);
                        burst.ping();
                        //if(!ConfigHandler.COMMON.silentSpreaders.get())
                            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.05F * (paddingColor != null ? 0.2F : 1F), 0.7F + 0.3F * (float) Math.random());
                    }
                }
            }
        }
    }

    public boolean isRedstone() {
        //updateContainingBlockInfo();
        //return world == null ? staticRedstone : getBlockState().getBlock() == ModBlocks.redstoneSpreader;
        return false;
    }

    public boolean isDreamwood() {
        //updateContainingBlockInfo();
        //return world == null ? staticDreamwood : getBlockState().getBlock() == ModBlocks.elvenSpreader;
        return false;
    }

    public boolean isULTRA_SPREADER() {
        //updateContainingBlockInfo();
        //return world == null ? staticUltra : getBlockState().getBlock() == ModBlocks.gaiaSpreader;
        return false;
    }

    public void checkForReceiver() {
        ItemStack stack = getInvStack(0);
        LensControlInterface control = getLensController(stack);
        if(control != null && !control.allowBurstShooting(stack, this, false))
            return;

        PowerBurstEntity fakeBurst = getBurst(true);
        fakeBurst.setScanBeam();
        BlockEntity receiver = fakeBurst.getCollidedTile(true);

        if(receiver instanceof PowerReceiverInterface
                && receiver.hasWorld()
                && receiver.getWorld().isChunkLoaded(receiver.getPos()))
            this.receiver = (PowerReceiverInterface) receiver;
        else this.receiver = null;
        lastTentativeBurst = fakeBurst.propsList;
    }

    @Override
    public PowerBurstInterface runBurstSimulation() {
        PowerBurstEntity fakeBurst = getBurst(true);
        fakeBurst.setScanBeam();
        fakeBurst.getCollidedTile(true);
        return fakeBurst;
    }

    private PowerBurstEntity getBurst(boolean fake) {
        boolean dreamwood = isDreamwood();
        boolean ultra = isULTRA_SPREADER();
        int maxPower = ultra ? 640 : dreamwood ? 240 : 160;
        int color = isRedstone() ? 0xFF2020 : dreamwood ? 0xFF45C4 : 0x20FF20;
        int ticksBeforePowerLoss = ultra ? 120 : dreamwood ? 80 : 60;
        float powerLossPerTick = ultra ? 20F : 4F;
        float motionModifier = ultra ? 2F : dreamwood ? 1.25F : 1F;
        float gravity = 0F;
        BurstProperties props = new BurstProperties(maxPower, ticksBeforePowerLoss, powerLossPerTick, gravity, motionModifier, color);

        ItemStack lens = getInvStack(0);
        if(!lens.isEmpty() && lens.getItem() instanceof LensEffectInterface)
            ((LensEffectInterface) lens.getItem()).apply(lens, props);

        if(getCurrentPower() >= props.maxPower || fake) {
            PowerBurstEntity burst = new PowerBurstEntity(this, fake);
            burst.setSourceLens(lens);

            burst.setColor(props.color);
            burst.setPower(props.maxPower);
            burst.setStartingPower(props.maxPower);
            burst.setMinPowerLoss(props.ticksBeforePowerLoss);
            burst.setPowerLossPerTick(props.powerLossPerTick);
            burst.setGravity(props.gravity);
            burst.setVelocity(burst.getVelocity().multiply(props.motionModifier));

            return burst;
        }
        return null;
    }

    public LensControlInterface getLensController(ItemStack stack) {
        if(!stack.isEmpty() && stack.getItem() instanceof LensControlInterface) {
            LensControlInterface control = (LensControlInterface) stack.getItem();
            if(control.isControlLens(stack))
                return control;
        }

        return null;
    }

    /*
    @Environment(EnvType.CLIENT)
    public void renderHUD(MinecraftClient mc) {
        String name = new ItemStack(getCachedState().getBlock()).getName().getString();
        int color = isRedstone() ? 0xFF0000 : isDreamwood() ? 0xFF00AE :  0x00FF00;
        HUDHandler.drawSimplePowerHUD(color, knownPower, getMaxPower(), name);

        ItemStack lens = itemHandler.getStackInSlot(0);
        if(!lens.isEmpty()) {
            GlStatePowerger.enableBlend();
            GlStatePowerger.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            String lensName = lens.getDisplayName().getString();
            int width = 16 + mc.fontRenderer.getStringWidth(lensName) / 2;
            int x = mc.mainWindow.getScaledWidth() / 2 - width;
            int y = mc.mainWindow.getScaledHeight() / 2 + 50;

            mc.fontRenderer.drawStringWithShadow(lensName, x + 20, y + 5, color);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getItemRenderer().renderItemAndEffectIntoGUI(lens, x, y);
            RenderHelper.disableStandardItemLighting();
            GlStatePowerger.disableLighting();
            GlStatePowerger.disableBlend();
        }

        if(receiver != null) {
            BlockEntity receiverTile = (BlockEntity) receiver;
            ItemStack recieverStack = new ItemStack(world.getBlockState(receiverTile.getPos()).getBlock());
            GlStatePowerger.enableBlend();
            GlStatePowerger.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if(!recieverStack.isEmpty()) {
                String stackName = recieverStack.getDisplayName().getString();
                int width = 16 + mc.fontRenderer.getStringWidth(stackName) / 2;
                int x = mc.mainWindow.getScaledWidth() / 2 - width;
                int y = mc.mainWindow.getScaledHeight() / 2 + 30;

                mc.fontRenderer.drawStringWithShadow(stackName, x + 20, y + 5, color);
                RenderHelper.enableGUIStandardItemLighting();
                mc.getItemRenderer().renderItemAndEffectIntoGUI(recieverStack, x, y);
                RenderHelper.disableStandardItemLighting();
            }

            GlStatePowerger.disableLighting();
            GlStatePowerger.disableBlend();
        }

        GlStatePowerger.color4f(1F, 1F, 1F, 1F);
    }

     */

    @Override
    public void onClientDisplayTick() {
        if(world != null) {
            PowerBurstEntity burst = getBurst(true);
            burst.getCollidedTile(false);
        }
    }

    @Override
    public float getPowerYieldMultiplier(PowerBurstInterface burst) {
        return 1F;
    }

    @Override
    public void markDirty() {
        checkForReceiver();
        sync();
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return false;
    }

    @Override
    public BlockPos getBinding() {
        if(receiver == null)
            return null;

        BlockEntity tile = (BlockEntity) receiver;
        return tile.getPos();
    }

    @Override
    public int getMaxPower() {
        return isULTRA_SPREADER() ? ULTRA_MAX_POWER : MAX_POWER;
    }

    @Override
    public boolean canSelect(PlayerEntity player, ItemStack wand, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public boolean bindTo(PlayerEntity player, ItemStack wand, BlockPos pos, Direction side) {
        Vec3d thisVec = Vec3d.fromPolar(this.getRotationX(),this.getRotationY());
        Vec3d blockVec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        VoxelShape shape = player.world.getBlockState(pos).getOutlineShape(player.world, pos);
        Box box = shape.isEmpty() ? new Box(pos) : shape.getBoundingBox().offset(pos);

        if(!box.contains(blockVec))
            blockVec = new Vec3d(box.x1 + (box.x2 - box.x1) / 2, box.y1 + (box.y2 - box.y1) / 2, box.z1 + (box.z2 - box.z1) / 2);

        Vec3d diffVec =  blockVec.subtract(thisVec);
        Vec3d diffVec2D = new Vec3d(diffVec.x, diffVec.z, 0);
        Vec3d rotVec = new Vec3d(0, 1, 0);
        double angle = Math.acos(rotVec.normalize().dotProduct(diffVec2D.normalize()))/Math.PI * 180.0;

        if(blockVec.x < thisVec.x)
            angle = -angle;

        rotationX = (float) angle + 90;

        rotVec = new Vec3d(diffVec.x, 0, diffVec.z);
        angle = Math.acos(diffVec.normalize().dotProduct(rotVec.normalize())) * 180F / Math.PI;
        if(blockVec.y < thisVec.y)
            angle = -angle;
        rotationY = (float) angle;

        checkForReceiver();
        return true;
    }

    @Override
    public void markDispatchable() {}

    @Override
    public float getRotationX() {
        return rotationX;
    }

    @Override
    public float getRotationY() {
        return rotationY;
    }

    @Override
    public void setRotationX(float rot) {
        rotationX = rot;
    }

    @Override
    public void setRotationY(float rot) {
        rotationY = rot;
    }

    @Override
    public void applyRotation(BlockRotation rotationIn) {
        switch (rotationIn)
        {
            case CLOCKWISE_90:
                rotationX += 270F;
                break;
            case CLOCKWISE_180:
                rotationX += 180F;
                break;
            case COUNTERCLOCKWISE_90:
                rotationX += 90F;
                break;
            default: break;
        }

        if(rotationX >= 360F)
            rotationX -= 360F;
    }

    @Override
    public void applyMirror(BlockMirror mirrorIn) {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                rotationX = 360F - rotationX;
                break;
            case FRONT_BACK:
                rotationX = 180F - rotationX;
                break;
            default: break;
        }

        if(rotationX < 0F)
            rotationX += 360F;
    }

    @Override
    public void commitRedirection() {
        checkForReceiver();
    }

    @Override
    public void setCanShoot(boolean canShoot) {
        canShootBurst = canShoot;
    }

    @Override
    public int getBurstParticleTick() {
        return burstParticleTick;
    }

    @Override
    public void setBurstParticleTick(int i) {
        burstParticleTick = i;
    }

    @Override
    public int getLastBurstDeathTick() {
        return lastBurstDeathTick;
    }

    @Override
    public void setLastBurstDeathTick(int i) {
        lastBurstDeathTick = i;
    }

    @Override
    public void pingback(PowerBurstInterface burst, UUID expectedIdentity) {
        if(getIdentifier().equals(expectedIdentity)) {
            pingbackTicks = TICKS_ALLOWED_WITHOUT_PINGBACK;
            Entity e = (Entity) burst;
            lastPingbackX = e.getX();
            lastPingbackY = e.getY();
            lastPingbackZ = e.getZ();
            setCanShoot(false);
        }
    }

    @Override
    public UUID getIdentifier() {
        if(identity == null)
            identity = UUID.randomUUID();
        return identity;
    }

    private UUID getIdentifierUnsafe() {
        return identity;
    }

    @Override
    public void clear() {

    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    @Override
    public void sync() { }
}
