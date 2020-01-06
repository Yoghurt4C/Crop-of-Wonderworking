package mods.coww.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface ItemFluidInterface {
    ItemStack getEmpty();

    ItemStack getFull(Fluid fluid);

    Fluid getFluid(ItemStack itemStack);

    @Accessor("HitResult")
    HitResult rayTrace(World world, PlayerEntity player, RayTraceContext.FluidHandling fluidHandling);

}
