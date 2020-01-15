package mods.coww.blocks;

import mods.coww.entity.MobJarBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MobJarBlock extends Block implements BlockEntityProvider {
    public MobJarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new MobJarBlockEntity();
    }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        world.playLevelEvent(player, 2001, pos, getRawIdFromState(state));
        if (!world.isClient()) {
            MobJarBlockEntity BE =  (MobJarBlockEntity) world.getBlockEntity(pos);
            if (BE != null) {
                if (BE.getMyEntityType() != null) {
                    Entity myEntity = Registry.ENTITY_TYPE.get(BE.getMyEntityType()).create(world);
                    assert myEntity != null;
                    myEntity.fromTag(BE.getMyEntityData());
                    myEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
                    world.spawnEntity(myEntity);
                }
            }
        }
    }
}
