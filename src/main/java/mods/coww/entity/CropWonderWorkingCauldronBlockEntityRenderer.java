package mods.coww.entity;

import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

import static net.minecraft.block.CauldronBlock.LEVEL;

public class CropWonderWorkingCauldronBlockEntityRenderer extends BlockEntityRenderer<CropWonderWorkingCauldronBlockEntity> {

    public CropWonderWorkingCauldronBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@Nonnull CropWonderWorkingCauldronBlockEntity cauldron, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!cauldron.getWorld().isChunkLoaded(cauldron.getPos()))
            return;
        double bobOffset = Math.sin((cauldron.getWorld().getTime() + tickDelta) / 8.0) / 4.0;
        FluidKey fluid = cauldron.fluid.getInvFluid(0).fluidKey;
        matrices.push();
        RenderSystem.enableRescaleNormal();
        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.enableRescaleNormal();

        float s = 1F / 256F * 10F;
        float v = 1F / 8F;
        float v2 = 1F / 4F;
        float w = -v * 2.5F;
        if (!cauldron.isInvEmpty()) {
            int petals = 0;
            for (int i = 0; i < cauldron.getInvSize(); i++)
                if (!cauldron.getInvStack(i).isEmpty())
                    petals++;
                else break;

            if (petals > 0) {
                final float modifier = 6F;
                final float rotationModifier = 0.25F;
                final float radiusBase = 0.9F;
                final float radiusMod = 0.1F;
                float ticks = (cauldron.getWorld().getTime() + tickDelta) * 0.5F;
                float offsetPerPetal = 360 / petals;

                matrices.push();
                matrices.translate(0.5, (0.375F + (cauldron.getCachedState().get(LEVEL) * 0.1875F))-0.025 + (bobOffset / 16.0), 0.5);
                matrices.scale(v2, v2, v2);
                for (int i = 0; i < petals; i++) {
                    float offset = offsetPerPetal * i;
                    float deg = (int) (ticks / rotationModifier % 360F + offset);
                    float rad = deg * (float) Math.PI / 180F;
                    float radiusX = (float) (radiusBase + radiusMod * Math.sin(ticks / modifier));
                    float radiusZ = (float) (radiusBase + radiusMod * Math.cos(ticks / modifier));
                    float x = (float) (radiusX * Math.cos(rad));
                    float z = (float) (radiusZ * Math.sin(rad));
                    float y = (float) Math.cos((ticks + 50 * i) / 5F) / 10F;

                    matrices.push();
                    matrices.translate(x, y, z);
                    matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(ticks/4F));
                    matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(ticks / 8F));
                    matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(ticks/4F));
                    RenderSystem.color4f(1F, 1F, 1F, 1F);
                    ItemStack stack = cauldron.getInvStack(i);
                    MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
                    int lightAbove = WorldRenderer.getLightmapCoordinates(cauldron.getWorld(), cauldron.getPos().up());
                    MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);
                    matrices.pop();
                }
                matrices.pop();
            }
        }
        if (!fluid.isEmpty()) {
            matrices.push();
            FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid.getRawFluid());
            Sprite sprite = fluid.getRawFluid()==null ? FluidRenderHandlerRegistry.INSTANCE.get(Fluids.WATER).getFluidSprites(cauldron.getWorld(), cauldron.getPos(), Fluids.WATER.getDefaultState())[0]
                    : fluidRenderHandler.getFluidSprites(cauldron.getWorld(), cauldron.getPos(), fluid.getRawFluid().getDefaultState())[0];
            int color = fluid.getRawFluid()==null ? cauldron.fluid.getInvFluid(0).getRenderColor()
                    : fluidRenderHandler.getFluidColor(cauldron.getWorld(), cauldron.getPos(), fluid.getRawFluid().getDefaultState());
            float alpha = fluid.equals(FluidKeys.LAVA) ? 1F : 0.7F;

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableAlphaTest();
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            matrices.translate(0, 0.375F + (cauldron.getCachedState().get(LEVEL) * 0.1875F), 0);

            int red = ((color >> 16) & 0xFF);
            int green = ((color >> 8) & 0xFF);
            int blue = (color & 0xFF);
            VertexConsumer vcon = vertexConsumers.getBuffer(RenderLayer.getTranslucentNoCrumbling());
            vcon.vertex(matrices.peek().getModel(), 0, 0, 1F).color(red, green, blue, (int) (alpha * 255F)).texture(sprite.getMinU(), sprite.getMaxV()).light(light).overlay(overlay).normal(matrices.peek().getNormal(), 0, 1, 0).next();
            vcon.vertex(matrices.peek().getModel(), 1F, 0, 1F).color(red, green, blue, (int) (alpha * 255F)).texture(sprite.getMaxU(), sprite.getMaxV()).light(light).overlay(overlay).normal(matrices.peek().getNormal(), 0, 1, 0).next();
            vcon.vertex(matrices.peek().getModel(), 1F, 0, 0).color(red, green, blue, (int) (alpha * 255F)).texture(sprite.getMaxU(), sprite.getMinV()).light(light).overlay(overlay).normal(matrices.peek().getNormal(), 0, 1, 0).next();
            vcon.vertex(matrices.peek().getModel(), 0, 0, 0).color(red, green, blue, (int) (alpha * 255F)).texture(sprite.getMinU(), sprite.getMinV()).light(light).overlay(overlay).normal(matrices.peek().getNormal(), 0, 1, 0).next();
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            matrices.pop();
        }
        matrices.pop();
    }
}
