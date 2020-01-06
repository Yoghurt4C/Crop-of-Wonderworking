package mods.coww.registry;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;

import static mods.coww.rendering.ItemRaytraceUtil.raytraceFromPlayer;

public class CropWonderWorkingEvents {
    public static void init(){
        UseBlockCallback.EVENT.register(((player, world, hand, blockHitResult) -> {
            ItemStack equipped=player.getStackInHand(hand);
            if(!equipped.isEmpty() && equipped.getItem() == Items.BOWL) {
                System.out.println("got past first if");
                BlockHitResult rtr = raytraceFromPlayer(world, player, RayTraceContext.FluidHandling.SOURCE_ONLY,4.5F);
                if (rtr.getType() == BlockHitResult.Type.BLOCK) {
                    System.out.println("got past 2nd if");
                    BlockPos pos = rtr.getBlockPos();
                    if (world.getBlockState(pos).getMaterial() == Material.WATER) {
                        System.out.println("got past 3rd if");
                        if (!world.isClient) {
                            equipped.decrement(1);
                            System.out.println("got to decrement");
                            if (equipped.isEmpty()) {
                                player.setStackInHand(hand, new ItemStack(CropWonderWorkingItems.BOWL_OF_WATER));
                                world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            } else player.inventory.offerOrDrop(world,new ItemStack(CropWonderWorkingItems.BOWL_OF_WATER));
                        } return ActionResult.SUCCESS; }}}
            return ActionResult.PASS;
        }));
    }
}
