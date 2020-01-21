package mods.coww.registry;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;

import static mods.coww.client.rendering.FluidHandlingRaytrace.raytraceFromPlayer;

public class CropWonderWorkingEvents {
    public static void init(){
        UseBlockCallback.EVENT.register(((player, world, hand, blockHitResult) -> {
            ItemStack equipped=player.getStackInHand(hand);
            if(!equipped.isEmpty() && equipped.getItem() == Items.BOWL) {
                BlockHitResult rtr = raytraceFromPlayer(world, player, RayTraceContext.FluidHandling.SOURCE_ONLY,4.5F);
                if (rtr.getType() == BlockHitResult.Type.BLOCK) {
                    BlockPos pos = rtr.getBlockPos();
                    if (world.getBlockState(pos).getMaterial() == Material.WATER) {
                        if (!world.isClient) {
                            equipped.decrement(1);
                            if (equipped.isEmpty()) {
                                player.setStackInHand(hand, new ItemStack(CropWonderWorkingItems.BOWL_OF_WATER));
                                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            } else player.inventory.offerOrDrop(world,new ItemStack(CropWonderWorkingItems.BOWL_OF_WATER));
                        } return ActionResult.SUCCESS; }}}
            return ActionResult.PASS;
        }));

        UseBlockCallback.EVENT.register(((player, world, hand, blockHitResult) -> {
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof CropBlock && !world.isClient) {
                CropBlock block = ((CropBlock)state.getBlock());
                if (state.get(block.getAgeProperty()).equals(block.getMaxAge())) {
                    world.setBlockState(pos, state.getBlock().getDefaultState());
                    net.minecraft.loot.context.LootContext.Builder builder = (new net.minecraft.loot.context.LootContext.Builder((ServerWorld) world)).setRandom(world.random).put(LootContextParameters.POSITION, pos).put(LootContextParameters.TOOL, ItemStack.EMPTY).putNullable(LootContextParameters.BLOCK_ENTITY, null);
                    for (ItemStack stack : state.getDroppedStacks(builder)) {
                        if (stack.getItem().toString().contains("seed")) {
                            stack.decrement(1);
                        }
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        }));
    }
}
