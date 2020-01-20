package mods.coww.blocks;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.mixin.api.IBucketItem;
import mods.coww.entity.CropWonderWorkingCauldronBlockEntity;
import mods.coww.entity.IItemEntity;
import mods.coww.registry.CropWonderWorkingItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CropWonderWorkingCauldronBlock extends CauldronBlock implements BlockEntityProvider {

    public CropWonderWorkingCauldronBlock(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecated")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        final CropWonderWorkingCauldronBlockEntity cauldron = (CropWonderWorkingCauldronBlockEntity) world.getBlockEntity(pos);
        if (cauldron!=null) { cauldron.handleInventory(cauldron,player,hand); }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        final CropWonderWorkingCauldronBlockEntity cauldron = (CropWonderWorkingCauldronBlockEntity) world.getBlockEntity(pos);
        if (cauldron != null) {
            Fluid cauldronFluid = cauldron.fluid.getInvFluid(0).fluidKey.getRawFluid();
            if (world.isClient) {
               return;
            }

            if (cauldronFluid != Fluids.LAVA) {
                if (entity instanceof ItemEntity) {
                    ItemEntity ientity = (ItemEntity) entity;
                    ItemStack stack = ientity.getStack();
                    int level = state.get(LEVEL);
                    if (!((IItemEntity) ientity).getSpawnedByCauldron()) {
                        if (stack.getItem() instanceof IBucketItem) { cauldron.interact(cauldron, stack); }
                        else if (stack.getItem().equals(Items.BOWL)) {
                            if (level == 3) {
                                stack.decrement(1);
                                cauldron.spawnCraftingResult(world, pos, new ItemStack(CropWonderWorkingItems.BOWL_OF_WATER)); }
                            cauldron.fluid.setInvFluid(0, FluidVolumeUtil.EMPTY, Simulation.ACTION);
                            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.1F, 10F);
                            world.setBlockState(pos,cauldron.getCachedState().with(LEVEL,0));
                        } else cauldron.handleInventory(cauldron, stack);
                        cauldron.sync();
                    }
                }
            } else { entity.setOnFireFor(4); }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new CropWonderWorkingCauldronBlockEntity();
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        CropWonderWorkingCauldronBlockEntity cauldron = (CropWonderWorkingCauldronBlockEntity) world.getBlockEntity(pos);
        world.playLevelEvent(player, 2001, pos, getRawIdFromState(state));
        if (cauldron!=null)
        ItemScatterer.spawn(world,pos,cauldron);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) { return new ItemStack(Items.CAULDRON); }
}
