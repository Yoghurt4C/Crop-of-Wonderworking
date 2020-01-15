package mods.coww.mixins;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock {
    @Shadow
    @Final
    public static IntProperty AGE;

    @Shadow
    public IntProperty getAgeProperty() {
        return AGE;
    }

    @Shadow
    public int getMaxAge() { return 0; }

    protected CropBlockMixin(Settings settings) {
        super(settings);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(getAgeProperty()).equals(getMaxAge())){
            world.setBlockState(pos,state.getBlock().getDefaultState());
            dropStacks(state,world,pos);
            return ActionResult.SUCCESS;
        } return super.onUse(state,world,pos,player,hand,hit);
    }
}
