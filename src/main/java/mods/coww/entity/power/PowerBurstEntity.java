package mods.coww.entity.power;

import mods.coww.api.internal.PowerBurstInterface;
import mods.coww.api.lens.LensEffectInterface;
import mods.coww.api.power.*;
import mods.coww.registry.cowwEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PowerBurstEntity extends ThrownEntity implements PowerBurstInterface {
    //@ObjectHolder(LibMisc.MOD_ID + ":power_burst")
    private static final String TAG_TICKS_EXISTED = "ticksExisted";
    private static final String TAG_COLOR = "color";
    private static final String TAG_POWER = "power";
    private static final String TAG_STARTING_POWER = "startingPower";
    private static final String TAG_MIN_POWER_LOSS = "minPowerLoss";
    private static final String TAG_TICK_POWER_LOSS = "powerLossTick";
    private static final String TAG_SPREADER_X = "spreaderX";
    private static final String TAG_SPREADER_Y = "spreaderY";
    private static final String TAG_SPREADER_Z = "spreaderZ";
    private static final String TAG_GRAVITY = "gravity";
    private static final String TAG_LENS_STACK = "lensStack";
    private static final String TAG_LAST_MOTION_X = "lastMotionX";
    private static final String TAG_LAST_MOTION_Y = "lastMotionY";
    private static final String TAG_LAST_MOTION_Z = "lastMotionZ";
    private static final String TAG_HAS_SHOOTER = "hasShooter";
    private static final String TAG_SHOOTER_UUID_MOST = "shooterUUIDMost";
    private static final String TAG_SHOOTER_UUID_LEAST = "shooterUUIDLeast";

    private static final TrackedData<Integer> COLOR = DataTracker.registerData(PowerBurstEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> POWER = DataTracker.registerData(PowerBurstEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> START_POWER = DataTracker.registerData(PowerBurstEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MIN_POWER_LOSS = DataTracker.registerData(PowerBurstEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> POWER_LOSS_PER_TICK = DataTracker.registerData(PowerBurstEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> GRAVITY = DataTracker.registerData(PowerBurstEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<BlockPos> SOURCE_COORDS = DataTracker.registerData(PowerBurstEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<ItemStack> SOURCE_LENS = DataTracker.registerData(PowerBurstEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    float accumulatedPowerLoss = 0;
    boolean fake = false;
    final Set<BlockPos> alreadyCollidedAt = new HashSet<>();
    boolean fullPowerLastTick = true;
    UUID shooterIdentity = null;
    int _ticksExisted = 0;
    boolean scanBeam = false;
    public final List<PositionProperties> propsList = new ArrayList<>();

    public PowerBurstEntity(EntityType<? extends ThrownEntity> type, World world) { super(type, world); }

    public PowerBurstEntity(World world) { this(cowwEntities.POWER_BURST, world); }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(COLOR, 0);
        dataTracker.startTracking(POWER, 0);
        dataTracker.startTracking(START_POWER, 0);
        dataTracker.startTracking(MIN_POWER_LOSS, 0);
        dataTracker.startTracking(POWER_LOSS_PER_TICK, 0F);
        dataTracker.startTracking(GRAVITY, 0F);
        dataTracker.startTracking(SOURCE_COORDS, BlockPos.ORIGIN);
        dataTracker.startTracking(SOURCE_LENS, ItemStack.EMPTY);
    }

    public PowerBurstEntity(PowerSpreaderInterface spreader, boolean fake) {
        this(((BlockEntity)spreader).getWorld());

        BlockEntity tile = (BlockEntity) spreader;

        this.fake = fake;

        setBurstSourceCoords(tile.getPos());
        setPositionAndAngles(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5, 0, 0);
        yaw = -(spreader.getRotationX() + 90F);
        pitch = spreader.getRotationY();

        float f = 0.4F;
        double mx = MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI) * f / 2D;
        double mz = -(MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI) * f) / 2D;
        double my = MathHelper.sin(pitch / 180.0F * (float) Math.PI) * f / 2D;
        setBurstMotion(mx, my, mz);
    }

    public PowerBurstEntity(PlayerEntity player) {
        super(cowwEntities.POWER_BURST, player, player.world);

        setBurstSourceCoords(new BlockPos(0, -1, 0));
        setRotation(player.yaw + 180, -player.pitch);

        float f = 0.4F;
        double mx = MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI) * f / 2D;
        double mz = -(MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI) * f) / 2D;
        double my = MathHelper.sin(pitch / 180.0F * (float) Math.PI) * f / 2D;
        setBurstMotion(mx, my, mz);
    }

    private void superUpdate() {
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        // Botania - inline supersuperclass.tick()
        {
            if (!this.world.isClient) {
                this.setFlag(6, this.isGlowing());
            }

            this.baseTick();
        }
        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            this.inGround = false;
            this.setVelocity(this.getVelocity().multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
        }

        Box axisalignedbb = this.getBoundingBox().expand(this.getVelocity().getX(), this.getVelocity().getY(), this.getVelocity().getZ()).expand(1.0D);

		/* Botania - no ignoreEntity stuff at all
		for(Entity entity : this.world.getEntitiesInAABBexcluding(this, axisalignedbb, (p_213881_0_) -> {
			return !p_213881_0_.isSpectator() && p_213881_0_.canBeCollidedWith();
		})) {
			if (entity == this.ignoreEntity) {
				++this.ignoreTime;
				break;
			}
			if (this.owner != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
				this.ignoreEntity = entity;
				this.ignoreTime = 3;
				break;
			}
		}
		*/

        HitResult raytraceresult = ProjectileUtil.getCollision(this, axisalignedbb, (entityCollisionPredicate) -> {
            return !entityCollisionPredicate.isSpectator() && entityCollisionPredicate.collides(); // && p_213880_1_ != this.ignoreEntity;
        }, RayTraceContext.ShapeType.OUTLINE, true);
		/*
		if (this.ignoreEntity != null && this.ignoreTime-- <= 0) {
			this.ignoreEntity = null;
		}
		*/

        if (raytraceresult.getType() != HitResult.Type.MISS) {
            if (raytraceresult.getType() == HitResult.Type.BLOCK && this.world.getBlockState(((BlockHitResult)raytraceresult).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
                this.setInNetherPortal(((BlockHitResult)raytraceresult).getBlockPos());
            } else {
                this.onCollision(raytraceresult);
            }
        }

        Vec3d vec3d = this.getVelocity();
        this.trackedX += vec3d.x;
        this.trackedY += vec3d.y;
        this.trackedZ += vec3d.z;
        float f = MathHelper.sqrt(squaredHorizontalLength(vec3d));
        this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));

        for(this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI)); this.pitch - this.prevPitch < -180.0F; this.prevPitch -= 360.0F) {
            ;
        }

        while(this.pitch - this.prevPitch >= 180.0F) {
            this.prevPitch += 360.0F;
        }

        while(this.yaw - this.prevYaw < -180.0F) {
            this.prevYaw -= 360.0F;
        }

        while(this.yaw - this.prevYaw >= 180.0F) {
            this.prevYaw += 360.0F;
        }

        this.pitch = MathHelper.lerp(0.2F, this.prevPitch, this.pitch);
        this.yaw = MathHelper.lerp(0.2F, this.prevYaw, this.yaw);
        float f1;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                float f2 = 0.25F;
                this.world.addParticle(ParticleTypes.BUBBLE, this.trackedX - vec3d.x * 0.25D, this.trackedY - vec3d.y * 0.25D, this.trackedZ - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
            }

            f1 = 0.8F;
        } else {
            f1 = 0.99F;
        }

        // Botania - no drag this.setMotion(vec3d.scale((double)f1));
        if (!this.hasNoGravity()) {
            Vec3d vec3d1 = this.getVelocity();
            this.setVelocity(vec3d1.x, vec3d1.y - (double)this.getGravity(), vec3d1.z);
        }

        this.setPosition(this.trackedX, this.trackedY, this.trackedZ);
    }

    @Override
    public void tick() {
        setTicksExisted(getTicksExisted() + 1);
        superUpdate();

        if(!fake && isAlive() && !scanBeam)
            ping();

        LensEffectInterface lens = getLensInstance();
        if(lens != null)
            lens.updateBurst(this, getSourceLens());

        int power = getPower();
        if(getTicksExisted() >= getMinPowerLoss()) {
            accumulatedPowerLoss += getPowerLossPerTick();
            int loss = (int) accumulatedPowerLoss;
            setPower(power - loss);
            accumulatedPowerLoss -= loss;

            if(getPower() <= 0)
                remove();
        }

        particles();

        setBurstMotion(getVelocity().getX(), getVelocity().getY(), getVelocity().getZ());

        fullPowerLastTick = getPower() == getStartingPower();

        if(scanBeam) {
            PositionProperties props = new PositionProperties((Entity)this);
            if(propsList.isEmpty())
                propsList.add(props);
            else {
                PositionProperties lastProps = propsList.get(propsList.size() - 1);
                if(!props.coordsEqual(lastProps))
                    propsList.add(props);
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        setPosition(x, y, z);
        setRotation(yaw, pitch);
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isInLava() {
        //Avoids expensive getBlockState check in Entity#onEntityUpdate (see super impl)
        return false;
    }

    private BlockEntity collidedTile = null;
    private boolean noParticles = false;

    public BlockEntity getCollidedTile(boolean noParticles) {
        this.noParticles = noParticles;

        int iterations = 0;
        while(isAlive()
                //&& iterations < ConfigHandler.COMMON.spreaderTraceTime.get()
        ) {
            tick();
            iterations++;
        }

        if(fake)
            incrementFakeParticleTick();

        return collidedTile;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt(TAG_TICKS_EXISTED, getTicksExisted());
        tag.putInt(TAG_COLOR, getColor());
        tag.putInt(TAG_POWER, getPower());
        tag.putInt(TAG_STARTING_POWER, getStartingPower());
        tag.putInt(TAG_MIN_POWER_LOSS, getMinPowerLoss());
        tag.putFloat(TAG_TICK_POWER_LOSS, getPowerLossPerTick());
        tag.putFloat(TAG_GRAVITY, getGravity());

        ItemStack stack = getSourceLens();
        CompoundTag lensCmp = new CompoundTag();
        if(!stack.isEmpty())
            lensCmp = stack.toTag(lensCmp);
        tag.put(TAG_LENS_STACK, lensCmp);

        BlockPos coords = getBurstSourceBlockPos();
        tag.putInt(TAG_SPREADER_X, coords.getX());
        tag.putInt(TAG_SPREADER_Y, coords.getY());
        tag.putInt(TAG_SPREADER_Z, coords.getZ());

        tag.putDouble(TAG_LAST_MOTION_X, getVelocity().getX());
        tag.putDouble(TAG_LAST_MOTION_Y, getVelocity().getY());
        tag.putDouble(TAG_LAST_MOTION_Z, getVelocity().getZ());

        UUID identity = getShooterUUID();
        boolean hasShooter = identity != null;
        tag.putBoolean(TAG_HAS_SHOOTER, hasShooter);
        if(hasShooter) {
            tag.putLong(TAG_SHOOTER_UUID_MOST, identity.getMostSignificantBits());
            tag.putLong(TAG_SHOOTER_UUID_LEAST, identity.getLeastSignificantBits());
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        setTicksExisted(tag.getInt(TAG_TICKS_EXISTED));
        setColor(tag.getInt(TAG_COLOR));
        setPower(tag.getInt(TAG_POWER));
        setStartingPower(tag.getInt(TAG_STARTING_POWER));
        setMinPowerLoss(tag.getInt(TAG_MIN_POWER_LOSS));
        setPowerLossPerTick(tag.getFloat(TAG_TICK_POWER_LOSS));
        setGravity(tag.getFloat(TAG_GRAVITY));

        CompoundTag lensCmp = tag.getCompound(TAG_LENS_STACK);
        ItemStack stack = ItemStack.fromTag(lensCmp);
        if(!stack.isEmpty())
            setSourceLens(stack);
        else setSourceLens(ItemStack.EMPTY);

        int x = tag.getInt(TAG_SPREADER_X);
        int y = tag.getInt(TAG_SPREADER_Y);
        int z = tag.getInt(TAG_SPREADER_Z);

        setBurstSourceCoords(new BlockPos(x, y, z));

        double lastMotionX = tag.getDouble(TAG_LAST_MOTION_X);
        double lastMotionY = tag.getDouble(TAG_LAST_MOTION_Y);
        double lastMotionZ = tag.getDouble(TAG_LAST_MOTION_Z);

        setBurstMotion(lastMotionX, lastMotionY, lastMotionZ);

        boolean hasShooter = tag.getBoolean(TAG_HAS_SHOOTER);
        if(hasShooter) {
            long most = tag.getLong(TAG_SHOOTER_UUID_MOST);
            long least = tag.getLong(TAG_SHOOTER_UUID_LEAST);
            UUID identity = getShooterUUID();
            if(identity == null || most != identity.getMostSignificantBits() || least != identity.getLeastSignificantBits())
                shooterIdentity = new UUID(most, least);
        }
    }

    public void particles() {
        if(!isAlive() || !world.isClient)
            return;

        LensEffectInterface lens = getLensInstance();
        if(lens != null && !lens.doParticles(this, getSourceLens()))
            return;

        Color color = new Color(getColor());
        float r = color.getRed() / 255F;
        float g = color.getGreen() / 255F;
        float b = color.getBlue() / 255F;
        float osize = getParticleSize();
        float size = osize;

        if(fake) {
            if(getPower() == getStartingPower())
                size = 2F;
            else if(fullPowerLastTick)
                size = 4F;

            if(!noParticles && shouldDoFakeParticles()) {
                //SparkleParticleData data = SparkleParticleData.fake(0.4F * size, r, g, b, 1);
                //Botania.proxy.addParticleForce(world, data, posX, posY, posZ, 0, 0, 0);
            }
        } else {
            //boolean depth = !Botania.proxy.isClientPlayerWearingMonocle();

            //if(ConfigHandler.CLIENT.subtlePowerSystem.get()) {
            //    WispParticleData data = WispParticleData.wisp(0.1F * size, r, g, b, depth);
            //    world.addParticle(data, posX, posY, posZ, (float) (Math.random() - 0.5F) * 0.02F, (float) (Math.random() - 0.5F) * 0.02F, (float) (Math.random() - 0.5F) * 0.01F);
            //} else {
                float or = r;
                float og = g;
                float ob = b;

                double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b; // Standard relative luminance calculation

                long savedPosX = trackedX;
                long savedPosY = trackedY;
                long savedPosZ = trackedZ;

                Vec3d currentPos = new Vec3d(this.trackedX,this.trackedY,this.trackedZ);
                Vec3d oldPos = new Vec3d(prevX, prevY, prevZ);
                Vec3d diffVec = oldPos.subtract(currentPos);
                Vec3d diffVecNorm = diffVec.normalize();

                double distance = 0.095;

                do {
                    if (luminance < 0.1) {
                        r = or + (float) Math.random() * 0.125F;
                        g = og + (float) Math.random() * 0.125F;
                        b = ob + (float) Math.random() * 0.125F;
                    }
                    size = osize + ((float) Math.random() - 0.5F) * 0.065F + (float) Math.sin(new Random(getUuid().getMostSignificantBits()).nextInt(9001)) * 0.4F;
                    //WispParticleData data = WispParticleData.wisp(0.2F * size, r, g, b, depth);
                    world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, trackedX, trackedY, trackedZ,
                            (float) -getVelocity().getX() * 0.01F,
                            (float) -getVelocity().getY() * 0.01F,
                            (float) -getVelocity().getZ() * 0.01F);

                    trackedX += diffVecNorm.x * distance;
                    trackedY += diffVecNorm.y * distance;
                    trackedZ += diffVecNorm.z * distance;

                    currentPos = new Vec3d(this.trackedX,this.trackedY,this.trackedZ);
                    diffVec = oldPos.subtract(currentPos);
                    //if(getPersistentData().contains(ItemTinyPlanet.TAG_ORBIT))
                        //break;
                } while(Math.abs(Math.sqrt(diffVec.x * diffVec.x + diffVec.y * diffVec.y + diffVec.z * diffVec.z)) > distance);

                //WispParticleData data = WispParticleData.wisp(0.1F * size, or, og, ob, depth);
                world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, trackedX, trackedY, trackedZ, (float) (Math.random() - 0.5F) * 0.06F, (float) (Math.random() - 0.5F) * 0.06F, (float) (Math.random() - 0.5F) * 0.06F);

                trackedX = savedPosX;
                trackedY = savedPosY;
                trackedZ = savedPosZ;
            //}
        }
    }

    public float getParticleSize() {
        return (float) getPower() / (float) getStartingPower();
    }

    @Override
    protected void onCollision(HitResult rtr) {
        BlockPos pos = null;
        boolean dead = false;

        if(rtr.getType() == HitResult.Type.BLOCK) {
            pos = ((BlockHitResult) rtr).getBlockPos();
            BlockEntity tile = world.getBlockEntity(pos);
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if(block instanceof PowerCollisionGhostInterface
                    && ((PowerCollisionGhostInterface) block).isGhost(state, world, pos)
                    && !(block instanceof PowerBurstTriggerInterface)
                    || block instanceof PlantBlock
                    || block instanceof LeavesBlock)
                return;

            BlockPos coords = getBurstSourceBlockPos();
            if(tile != null && !tile.getPos().equals(coords))
                collidedTile = tile;

            if(tile == null || !tile.getPos().equals(coords)) {
                if(!fake && !noParticles && (!world.isClient || tile instanceof ClientPowerSerializerInterface) && tile instanceof PowerReceiverInterface && ((PowerReceiverInterface) tile).canReceivePowerFromBursts())
                    onRecieverImpact((PowerReceiverInterface) tile, tile.getPos());

                if(block instanceof PowerBurstTriggerInterface)
                    ((PowerBurstTriggerInterface) block).onBurstCollision(this, world, pos);

                boolean ghost = block instanceof PowerCollisionGhostInterface;
                dead = !ghost;
                if(ghost)
                    return;
            }
        }

        LensEffectInterface lens = getLensInstance();
        if(lens != null)
            dead = lens.collideBurst(this, rtr, collidedTile instanceof PowerReceiverInterface
                    && ((PowerReceiverInterface) collidedTile).canReceivePowerFromBursts(), dead, getSourceLens());

        if(pos != null && !hasAlreadyCollidedAt(pos))
            alreadyCollidedAt.add(pos);

        if(dead && isAlive()) {
            if(!fake && world.isClient) {
                Color color = new Color(getColor());
                float r = color.getRed() / 255F;
                float g = color.getGreen() / 255F;
                float b = color.getBlue() / 255F;

                int power = getPower();
                int maxPower = getStartingPower();
                float size = (float) power / (float) maxPower;

                //if(!ConfigHandler.CLIENT.subtlePowerSystem.get())
                    for(int i = 0; i < 4; i++) {
                        //WispParticleData data = WispParticleData.wisp(0.15F * size, r, g, b);
                        world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, trackedX, trackedY, trackedZ, (float) (Math.random() - 0.5F) * 0.04F, (float) (Math.random() - 0.5F) * 0.04F, (float) (Math.random() - 0.5F) * 0.04F);
                    }
                //SparkleParticleData data = SparkleParticleData.sparkle((float) 4, r, g, b, 2);
                world.addParticle(ParticleTypes.ENTITY_EFFECT, (float) trackedX, (float) trackedY, (float) trackedZ, 0, 0, 0);
            }

            remove();
        }
    }

    private void onRecieverImpact(PowerReceiverInterface tile, BlockPos pos) {
        //if (getPersistentData().getBoolean(LensWarp.TAG_WARPED))
        //    return;

        LensEffectInterface lens = getLensInstance();
        int power = getPower();

        if(lens != null) {
            ItemStack stack = getSourceLens();
            power = lens.getPowerToTransfer(this, this, stack, tile);
        }

        if(tile instanceof PowerCollectorInterface)
            power *= ((PowerCollectorInterface) tile).getPowerYieldMultiplier(this);

        tile.receivePower(power);

        if(tile instanceof ThrottledPacketInterface)
            ((ThrottledPacketInterface) tile).markDispatchable();
        //else sync();
    }

    @Override
    public void remove() {
        super.remove();

        if(!fake) {
            BlockEntity tile = getShooter();
            if(tile instanceof PowerSpreaderInterface)
                ((PowerSpreaderInterface) tile).setCanShoot(true);
        } else setDeathTicksForFakeParticle();
    }

    private BlockEntity getShooter() {
        return world.getBlockEntity(getBurstSourceBlockPos());
    }

    /*
    @Override
    protected float getGravityVelocity() {
        return getGravity();
    }
     */

    @Override
    public boolean isFake() {
        return fake;
    }

    @Override
    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public void setScanBeam() {
        scanBeam = true;
    }

    @Override
    public int getColor() {
        return dataTracker.get(COLOR);
    }

    @Override
    public void setColor(int color) {
        dataTracker.set(COLOR, color);
    }

    @Override
    public int getPower() {
        return dataTracker.get(POWER);
    }

    @Override
    public void setPower(int power) {
        dataTracker.set(POWER, power);
    }

    @Override
    public int getStartingPower() {
        return dataTracker.get(START_POWER);
    }

    @Override
    public void setStartingPower(int power) {
        dataTracker.set(START_POWER, power);
    }

    @Override
    public int getMinPowerLoss() {
        return dataTracker.get(MIN_POWER_LOSS);
    }

    @Override
    public void setMinPowerLoss(int minPowerLoss) {
        dataTracker.set(MIN_POWER_LOSS, minPowerLoss);
    }

    @Override
    public float getPowerLossPerTick() {
        return dataTracker.get(POWER_LOSS_PER_TICK);
    }

    @Override
    public void setPowerLossPerTick(float power) {
        dataTracker.set(POWER_LOSS_PER_TICK, power);
    }

    @Override
    public float getGravity() {
        return dataTracker.get(GRAVITY);
    }

    @Override
    public void setGravity(float gravity) {
        dataTracker.set(GRAVITY, gravity);
    }

    @Override
    public BlockPos getBurstSourceBlockPos() {
        return dataTracker.get(SOURCE_COORDS);
    }

    @Override
    public void setBurstSourceCoords(BlockPos pos) {
        dataTracker.set(SOURCE_COORDS, pos);
    }

    @Override
    public ItemStack getSourceLens() {
        return dataTracker.get(SOURCE_LENS);
    }

    @Override
    public void setSourceLens(ItemStack lens) {
        dataTracker.set(SOURCE_LENS, lens);
    }

    @Override
    public int getTicksExisted() {
        return _ticksExisted;
    }

    public void setTicksExisted(int ticks) {
        _ticksExisted = ticks;
    }

    private LensEffectInterface getLensInstance() {
        ItemStack lens = getSourceLens();
        if(!lens.isEmpty() && lens.getItem() instanceof LensEffectInterface)
            return (LensEffectInterface) lens.getItem();

        return null;
    }

    @Override
    public void setBurstMotion(double x, double y, double z) {
        this.setVelocity(x, y, z);
    }

    @Override
    public boolean hasAlreadyCollidedAt(BlockPos pos) {
        return alreadyCollidedAt.contains(pos);
    }

    @Override
    public void setCollidedAt(BlockPos pos) {
        if(!hasAlreadyCollidedAt(pos))
            alreadyCollidedAt.add(pos.toImmutable());
    }

    @Override
    public void setShooterUUID(UUID uuid) {
        shooterIdentity = uuid;
    }

    @Override
    public UUID getShooterUUID() {
        return shooterIdentity;
    }

    @Override
    public void ping() {
        BlockEntity tile = getShooter();
        if(tile instanceof PingableInterface)
            ((PingableInterface) tile).pingback(this, getShooterUUID());
    }

    @Nonnull
    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getEntityId());
    }

    protected boolean shouldDoFakeParticles() {
        //if (ConfigHandler.CLIENT.staticWandBeam.get())
        //    return true;

        BlockEntity tile = getShooter();
        return tile instanceof PowerSpreaderInterface
                && (getPower() != getStartingPower() && fullPowerLastTick
                || Math.abs(((PowerSpreaderInterface) tile).getBurstParticleTick() - getTicksExisted()) < 4);
    }

    private void incrementFakeParticleTick() {
        BlockEntity tile = getShooter();
        if(tile instanceof PowerSpreaderInterface) {
            PowerSpreaderInterface spreader = (PowerSpreaderInterface) tile;
            spreader.setBurstParticleTick(spreader.getBurstParticleTick()+2);
            if(spreader.getLastBurstDeathTick() != -1 && spreader.getBurstParticleTick() > spreader.getLastBurstDeathTick())
                spreader.setBurstParticleTick(0);
        }
    }

    private void setDeathTicksForFakeParticle() {
        BlockPos coords = getBurstSourceBlockPos();
        BlockEntity tile = world.getBlockEntity(coords);
        if(tile instanceof PowerSpreaderInterface)
            ((PowerSpreaderInterface) tile).setLastBurstDeathTick(getTicksExisted());
    }

    public static class PositionProperties {

        public final BlockPos coords;
        public final BlockState state;

        public boolean invalid = false;

        public PositionProperties(Entity entity) {
            int x = MathHelper.floor(entity.getX());
            int y = MathHelper.floor(entity.getY());
            int z = MathHelper.floor(entity.getZ());
            coords = new BlockPos(x, y, z);
            state = entity.world.getBlockState(coords);
        }

        public boolean coordsEqual(PositionProperties props) {
            return coords.equals(props.coords);
        }

        public boolean contentsEqual(World world) {
            if(!world.isChunkLoaded(coords)) {
                invalid = true;
                return false;
            }

            return world.getBlockState(coords) == state;
        }

        @Override
        public int hashCode() {
            return Objects.hash(coords, state);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof PositionProperties
                    && ((PositionProperties) o).state == state
                    && ((PositionProperties) o).coords.equals(coords);
        }
    }

}