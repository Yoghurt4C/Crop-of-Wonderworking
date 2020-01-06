package mods.coww.blocks;

import mods.coww.entity.CropWonderWorkingCauldronBlockEntity;
import mods.coww.entity.CropWonderWorkingCauldronInventory;
import mods.coww.entity.IItemEntity;
import mods.coww.recipes.CauldronRecipe;
import mods.coww.registry.CropWonderWorkingItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Optional;

import static mods.coww.entity.CropWonderWorkingCauldronBlockEntity.matchRecipeInputs;

public class CropWonderWorkingCauldronBlock extends CauldronBlock implements BlockEntityProvider {

    public CropWonderWorkingCauldronBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        final Inventory inventory = (SidedInventory) world.getBlockEntity(pos);
        final CropWonderWorkingCauldronBlockEntity cauldron = (CropWonderWorkingCauldronBlockEntity) world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);
        int level = (int) state.get(LEVEL);
        if (!stack.isEmpty()) {
            if (stack.getItem().equals(Items.WATER_BUCKET) || stack.getItem().equals(CropWonderWorkingItems.BOWL_OF_WATER)) {
                if (level < 3 && !world.isClient) {
                    if (!player.abilities.creativeMode) {
                        player.setStackInHand(hand, new ItemStack(stack.getItem().getRecipeRemainder()));
                    }
                    player.incrementStat(Stats.FILL_CAULDRON);
                    this.setLevel(world, pos, state, 3);
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            } else if (stack.getItem().equals(Items.BUCKET)) {
                if (level == 3 && !world.isClient) {
                    if (!player.abilities.creativeMode) {
                        stack.decrement(1);
                        if (stack.isEmpty()) {
                            player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
                        } else player.inventory.offerOrDrop(world, new ItemStack(Items.WATER_BUCKET));
                    }
                }
                player.incrementStat(Stats.USE_CAULDRON);
                this.setLevel(world, pos, state, 0);
                world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            } else if (stack.getItem().equals(Items.BOWL)) {
                if (level == 3 && !world.isClient) {
                    if (!player.abilities.creativeMode) {
                        stack.decrement(1);
                        if (stack.isEmpty()) {
                            player.setStackInHand(hand, new ItemStack(CropWonderWorkingItems.BOWL_OF_WATER));
                        } else player.inventory.offerOrDrop(world, new ItemStack(CropWonderWorkingItems.BOWL_OF_WATER));
                    }
                }
                player.incrementStat(Stats.USE_CAULDRON);
                this.setLevel(world, pos, state, 0);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            } else for (int i = 0; i < inventory.getInvSize(); i++) {
                if (!inventory.getInvStack(i).isEmpty()) {
                    final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, inventory, world);

                    if (match.isPresent()) {
                        matchRecipeInputs(state, world, pos, player, hand);
                        splash(world,pos);
                        if (inventory.isInvEmpty()) { break; }
                    }
                } else if (inventory.getInvStack(i).isEmpty()) {
                    inventory.setInvStack(i, stack.copy());
                    stack.decrement(1);
                    splash(world,pos);
                    break;
                }
            }
        } else {
            if (!player.isSneaking()) {
                if (inventory.isInvEmpty() && cauldron.getLastRecipeStacks() != null && cauldron.lastRecipeTimer>0 && !cauldron.getLastRecipeStacks().isEmpty()) {
                    cauldron.trySetLastRecipe(player);
                }
            } else {
                for (int j = inventory.getInvSize() - 1; j >= 0; j--) {
                    if (!inventory.isInvEmpty() && player.isSneaking()) {
                        if (!inventory.getInvStack(j).isEmpty()) {
                            player.inventory.offerOrDrop(world, inventory.getInvStack(j));
                            inventory.removeInvStack(j);
                            break;
                        }
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        final CropWonderWorkingCauldronBlockEntity cauldron = (CropWonderWorkingCauldronBlockEntity) world.getBlockEntity(pos);
        final Inventory inventory = (SidedInventory) world.getBlockEntity(pos);
        if (world.isClient) { return; }

        if (entity instanceof ItemEntity) {
            ItemEntity ientity = entity instanceof ItemEntity ? (ItemEntity) entity : null;
            ItemStack stack = ientity.getStack();

            if (!((IItemEntity) ientity).getSpawnedByCauldron()) {
                for (int i = 0; i < inventory.getInvSize(); i++) {
                    if (!inventory.getInvStack(i).isEmpty()) {
                        final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, inventory, world);

                        if (match.isPresent()) {
                            matchRecipeInputs(state, world, pos, entity);
                        }
                    } else if (inventory.getInvStack(i).isEmpty()) {
                        inventory.setInvStack(i, stack.copy());
                        stack.decrement(1);
                        break;
                    }
                }
                if (!((CropWonderWorkingCauldronInventory)inventory).isInvFull()){splash(world,pos);}
                cauldron.sync();
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new CropWonderWorkingCauldronBlockEntity();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) { return new ItemStack(Items.CAULDRON); }

    @Environment(EnvType.CLIENT)
    public void splash (World world, BlockPos pos) {
        if (world.getBlockState(pos).get(LEVEL)>0) {
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.1F, 10F);
        } else world.playSound(null, pos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 0.75F, 5F);
    }
}
