package mods.coww.items;

import mods.coww.registry.CropWonderWorkingSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoesMadItem extends HoeItem {
    public HoesMadItem(ToolMaterial material, float attackSpeed, Settings settings){
        super(material,attackSpeed,settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        if (context.getSide() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir()) {
            BlockState blockState = (BlockState)TILLED_BLOCKS.get(world.getBlockState(blockPos).getBlock());
            if (blockState != null) {
                PlayerEntity playerEntity = context.getPlayer();
                world.playSound(playerEntity, blockPos, CropWonderWorkingSounds.HOES_MAD, SoundCategory.BLOCKS, 0.3F, 1.0F);
                for (int i=0;i<6;i++){spawnFX(world,blockPos,3);}
                if (!world.isClient) {
                    world.setBlockState(blockPos, blockState, 11);
                    if (playerEntity != null) {
                        context.getStack().damage(1, playerEntity, (p) -> {
                            p.sendToolBreakStatus(context.getHand());
                        });
                    }
                }

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    public void spawnFX(World world, BlockPos pos, int range){
        world.addParticle(ParticleTypes.ANGRY_VILLAGER, pos.getX() + Math.random(), pos.getY() + 0.25 + Math.random(), pos.getZ() + Math.random(), range, (float) Math.random() * 0.1F - 0.05F, range);
    }
}
