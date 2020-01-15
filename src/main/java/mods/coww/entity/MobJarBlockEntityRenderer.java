package mods.coww.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL13;

public class MobJarBlockEntityRenderer extends BlockEntityRenderer<MobJarBlockEntity> {
    private Entity ent;

    public MobJarBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(MobJarBlockEntity mobJar, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockPos pos = mobJar.getPos();
        matrices.push();

        RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, (float) (light & 0xFFFF), (float) ((light >> 16) & 0xFFFF));
        ent = mobJar.getEntity();
        if (ent != null) {
            float g = 0.33F;
            float h = Math.max(ent.getWidth(), ent.getHeight());
            if ((double)h > 1.0D) {
                g /= h;
            }
            matrices.translate(0.5,0.1,0.5);
            matrices.scale(g,g,g);
            MinecraftClient.getInstance().getEntityRenderManager().render(ent, 0,0,0, ent.getYaw(tickDelta), tickDelta, matrices, vertexConsumers, light);
        }
        matrices.pop();
    }
}
