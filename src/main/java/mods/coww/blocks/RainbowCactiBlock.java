package mods.coww.blocks;

import mods.coww.registry.cowwBlocks;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Iterator;

public class RainbowCactiBlock extends CactusBlock {
    public RainbowCactiBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Iterator var4 = Direction.Type.HORIZONTAL.iterator();

        Direction direction;
        Material material;
        do {
            if (!var4.hasNext()) {
                Block block = world.getBlockState(pos.down()).getBlock();
                return (block == cowwBlocks.RAINBOW_CACTI || block == Blocks.SAND || block == Blocks.RED_SAND) && !world.getBlockState(pos.up()).getMaterial().isLiquid();
            }
            direction = (Direction)var4.next();
            BlockState blockState = world.getBlockState(pos.offset(direction));
            material = blockState.getMaterial();
        } while(!material.isSolid() && !world.getFluidState(pos.offset(direction)).matches(FluidTags.LAVA));
        return false;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity) {
            if (!TagRegistry.item(new Identifier("c","dye_any")).contains(((ItemEntity)entity).getStack().getItem())){
                if (!((ItemEntity)entity).getStack().getItem().equals(cowwBlocks.RAINBOW_CACTI.asItem())) {
                    entity.damage(DamageSource.CACTUS, 1.0F);
                }
            }
        } else entity.damage(DamageSource.CACTUS, 1.0F);
    }
}
