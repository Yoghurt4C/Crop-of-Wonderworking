package mods.coww.items;

import mods.coww.registry.CropWonderWorkingBlocks;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class MobJarBlockItem extends BlockItem {
    public boolean isEmpty = true;

    public MobJarBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static boolean writeTagToBlockEntity(World world, PlayerEntity player, BlockPos pos, ItemStack stack) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            CompoundTag beTag = stack.getSubTag("BlockEntityTag");
            BlockEntity be = world.getBlockEntity(pos);
            if (be != null) {
                if (!world.isClient && be.shouldNotCopyTagFromItem() && (player == null || !player.isCreativeLevelTwoOp())) {
                    return false;
                }
                CompoundTag beWriteTag = be.toTag(new CompoundTag());
                beWriteTag.putInt("x", pos.getX());
                beWriteTag.putInt("y", pos.getY());
                beWriteTag.putInt("z", pos.getZ());
                if (stack.hasTag()) {
                    if (stack.getTag().contains("entity_id")) {
                        beWriteTag.putString("entity_id", stack.getTag().getString("entity_id"));
                        beWriteTag.put("entityData", stack.getTag().getCompound("entityData"));
                    }
                    be.fromTag(beWriteTag);
                }
            }

        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> text, TooltipContext tooltipContext) {
        if (world != null) {
            if (stack.hasTag()) {
                assert stack.getTag() != null;
                text.add(new TranslatableText(("Animal Type: " + stack.getTag().getString("genetics:entitytype"))));
                text.add(new TranslatableText("entity_id:" + stack.getTag().getString("entity_id")));
                //text.add(new TranslatableText("data:" + stack.getTag().getCompound("entityData").toString()));
                isEmpty = false;
            } else {
                text.add(new TranslatableText("Empty Jar"));
                isEmpty = true;
            }
        }
    }

    @Override
    public boolean useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (stack.getItem() == CropWonderWorkingBlocks.MOB_JAR.asItem()) {
            ItemStack newItem = null;
            CompoundTag entityInfo = new CompoundTag();
            CompoundTag entityTag = new CompoundTag();
            MobJarBlockItem theItem = (MobJarBlockItem) stack.getItem();
            if (theItem.isEmpty) {
                newItem = new ItemStack(CropWonderWorkingBlocks.MOB_JAR.asItem());
                entity.toTag(entityTag);
                entityInfo.putString("genetics:entitytype", entity.getName().getString());
                entityInfo.putString("entity_id", Registry.ENTITY_TYPE.getId(entity.getType()).toString());
                entityInfo.put("entityData", entityTag);
                newItem.setTag(entityInfo);
                entity.removed = true;
                stack.setCount(stack.getCount() - 1);
            }
            if (newItem != null) {
                if (stack.isEmpty()) {
                    player.setStackInHand(hand, newItem);
                } else if (!player.inventory.insertStack(newItem)) {
                    player.dropItem(newItem, false);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ActionResult place(ItemPlacementContext placementContext) {
        if (!placementContext.canPlace()) {
            return ActionResult.FAIL;
        } else {
            ItemPlacementContext newPlacementContext = this.getPlacementContext(placementContext);
            if (newPlacementContext == null) {
                return ActionResult.FAIL;
            } else {
                BlockState placementState = this.getPlacementState(newPlacementContext);
                if (placementState == null) {
                    return ActionResult.FAIL;
                } else if (!this.place(newPlacementContext, placementState)) {
                    return ActionResult.FAIL;
                } else {
                    BlockPos pos = newPlacementContext.getBlockPos();
                    World world = newPlacementContext.getWorld();
                    PlayerEntity player = newPlacementContext.getPlayer();
                    ItemStack stack = newPlacementContext.getStack();
                    BlockState newPlacementState = world.getBlockState(pos);
                    Block block = newPlacementState.getBlock();
                    if (block == placementState.getBlock()) {
                        this.postPlacement(pos, world, player, stack, newPlacementState);
                        block.onPlaced(world, pos, newPlacementState, player, stack);

                        if (player instanceof ServerPlayerEntity) {
                            Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
                        }
                    }

                    BlockSoundGroup placementSoundGroup = newPlacementState.getSoundGroup();
                    world.playSound(player, pos, this.getPlaceSound(newPlacementState), SoundCategory.BLOCKS, (placementSoundGroup.getVolume() + 1.0F) / 2.0F, placementSoundGroup.getPitch() * 0.8F);
                    stack.decrement(1);
                    return ActionResult.SUCCESS;
                }
            }
        }
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState placementState) {
        return writeTagToBlockEntity(world, player, pos, stack);
    }
}
