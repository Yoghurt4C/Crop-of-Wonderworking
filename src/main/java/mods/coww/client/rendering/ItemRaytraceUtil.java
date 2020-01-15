package mods.coww.client.rendering;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class ItemRaytraceUtil {
    public static BlockHitResult raytraceFromPlayer(World world, PlayerEntity player, RayTraceContext.FluidHandling fluidHandling, double range) {
        float f = player.pitch;
        float f1 = player.yaw;
        Vec3d vec3d = player.getCameraPosVec(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = range;
        Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return world.rayTrace(new RayTraceContext(vec3d, vec3d1, RayTraceContext.ShapeType.OUTLINE, fluidHandling, player));
    }
}
