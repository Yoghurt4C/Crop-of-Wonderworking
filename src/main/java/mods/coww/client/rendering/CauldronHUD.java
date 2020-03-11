package mods.coww.client.rendering;

import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import com.mojang.blaze3d.systems.RenderSystem;
import mods.coww.client.CropWonderWorkingClient;
import mods.coww.entity.CauldronBlockEntity;
import mods.coww.entity.GoatDetectorBlockEntity;
import mods.coww.recipes.CauldronRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.FishBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Optional;

import static mods.coww.blocks.GoatDetectorBlock.POWERED;
import static net.minecraft.block.CauldronBlock.LEVEL;

public class CauldronHUD extends DrawableHelper {
    private final MinecraftClient client;
    private final CropWonderWorkingClient mod;
    public CauldronHUD(@NotNull MinecraftClient client, @NotNull CropWonderWorkingClient mod){
        this.client=client;
        this.mod=mod;
    }


    @Environment(EnvType.CLIENT)
    public void renderHUD() {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult pos = client.crosshairTarget;

        int xc = client.getWindow().getScaledWidth() / 2;
        int yc = client.getWindow().getScaledHeight() / 2;

        if (client.world != null) {
            if (pos != null) {
                if (pos.getType() == HitResult.Type.BLOCK) {
                    BlockPos bpos = pos.getType() == HitResult.Type.BLOCK ? ((BlockHitResult) pos).getBlockPos() : null;
                    BlockState state = bpos != null ? client.world.getBlockState(bpos) : null;
                    BlockEntity blockEntity = bpos != null ? client.world.getBlockEntity(bpos) : null;
                    if (blockEntity instanceof CauldronBlockEntity) {
                        CauldronBlockEntity cauldron = (CauldronBlockEntity) blockEntity;
                        String fluidString = (cauldron.fluid.getInvFluid(0).fluidKey.toString().split(" "))[2].replace("}","");
                        ClientPlayerEntity player = client.player;
                        if (client.player != null && cauldron.fluid.getInvFluid(0).fluidKey==FluidKeys.EMPTY
                                ||cauldron.fluid.getInvFluid(0).fluidKey==FluidKeys.WATER && cauldron.getCachedState().get(LEVEL)>3) {
                            ItemStack pstack = client.player.getMainHandStack();
                            if (pstack.getItem() instanceof FishBucketItem) {
                                RenderSystem.pushMatrix();
                                client.getTextureManager().bindTexture(new Identifier("coww:textures/gui/cauldron_hud.png"));
                                RenderSystem.enableAlphaTest();
                                RenderSystem.color4f(1F, 1F, 1F, 1F);
                                drawTexturedModalRect(xc + 33, yc - 8, 0, 44, 0, 22, 15);
                                client.getItemRenderer().renderGuiItemIcon(new ItemStack(Items.CAULDRON),xc + 56, yc - 8);
                                RenderSystem.disableAlphaTest();
                                RenderSystem.popMatrix();
                            }
                        }

                        float angle = -90;
                        int radius = 24;
                        int stackCount = 0;
                        if (cauldron != null) {
                            for (int i = 0; i < cauldron.getInvSize(); i++) {
                                if (cauldron.getInvStack(i).isEmpty()) {
                                    break;
                                }
                                stackCount++;
                            }

                            if (stackCount > 0) {
                                float anglePerStack = 360F / stackCount;
                                final Optional<CauldronRecipe> match = client.world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, cauldron, client.world);
                                if (match.isPresent()) {
                                    RenderSystem.enableAlphaTest();
                                    RenderSystem.color4f(1F, 1F, 1F, 1F);
                                    client.getTextureManager().bindTexture(new Identifier("coww:textures/gui/cauldron_hud.png"));
                                    if(!match.get().getFluid().equals(fluidString))
                                    { drawTexturedModalRect(xc + radius + 9, yc - 8, 0, 22, 0, 22, 15); } else
                                    {drawTexturedModalRect(xc + radius + 9, yc - 8, 0, 0, 0, 22, 15);}
                                    RenderSystem.disableAlphaTest();

                                    ItemStack stack = match.get().getOutput();
                                    ItemStack[] cataList = match.get().getCatalyst().getMatchingStacksClient();
                                    ItemStack catalyst = cataList[client.world.random.nextInt(cataList.length)];
                                    String wrongFluid = I18n.translate("cauldron.coww.fluid_mismatch");
                                    String wrongFluid_ps = I18n.translate("cauldron.coww.fluid_mismatch_ps") + ": " + toTitleCase(match.get().getFluid().split(":")[1]);

                                    client.getItemRenderer().renderGuiItemIcon(stack, xc + radius + 32, yc - 8);
                                    if(!match.get().getFluid().equals(fluidString)) {
                                        RenderSystem.pushMatrix();
                                        RenderSystem.translated(0,0,200);
                                        client.textRenderer.drawWithShadow(wrongFluid, xc + radius + 7, yc + 10, 0xFFFFFF);
                                        client.textRenderer.drawWithShadow(wrongFluid_ps, xc + radius, yc + 20, 0xFFFFFF);

                                    } else {client.getItemRenderer().renderGuiItemIcon(catalyst, xc + radius + 16, yc + 6);
                                    RenderSystem.pushMatrix();
                                    RenderSystem.translated(0,0,200);
                                    client.textRenderer.drawWithShadow("+", xc + radius + 14, yc + 10, 0xFFFFFF);
                                    }
                                    RenderSystem.popMatrix();
                                }

                                for (int i = 0; i < stackCount; i++) {
                                    double xPos = xc + Math.cos(angle * Math.PI / 180D) * radius - 8;
                                    double yPos = yc + Math.sin(angle * Math.PI / 180D) * radius - 8;
                                    RenderSystem.translated(xPos, yPos, 0);
                                    client.getItemRenderer().renderGuiItemIcon(cauldron.getInvStack(i), 0, 0);
                                    RenderSystem.translated(-xPos, -yPos, 0);
                                    angle += anglePerStack;
                                }
                            } else if (state.get(LEVEL) > 0 && cauldron.lastRecipeTimer>0 && cauldron.getLastRecipeResult()!=null) {
                                String s = I18n.translate("cauldron.coww.fill_last_recipe");
                                client.textRenderer.drawWithShadow(s, xc - client.textRenderer.getStringWidth(s) / 2, yc + 10, 0xFFFFFF);
                                s = I18n.translate("cauldron.coww.fill_last_recipe_ps");
                                client.textRenderer.drawWithShadow(s, xc - client.textRenderer.getStringWidth(s) / 2, yc + 20, 0xFFFFFF);
                                s = "("+I18n.translate(cauldron.getLastRecipeResult().getTranslationKey())+")";
                                RenderSystem.pushMatrix();
                                RenderSystem.translated(0,0,200);
                                client.textRenderer.drawWithShadow(s, xc - client.textRenderer.getStringWidth(s) / 2, yc + 40, 0xFFFFFF);
                                RenderSystem.translated(xc-8.5D,yc+30D,-150);
                                client.getItemRenderer().renderGuiItemIcon(cauldron.getLastRecipeResult(),0,0);
                                RenderSystem.popMatrix();
                            }
                        }
                    } else if (blockEntity instanceof GoatDetectorBlockEntity){
                        GoatDetectorBlockEntity goatDetector = (GoatDetectorBlockEntity) blockEntity;
                        String goatFormatted = goatDetector.goat.split(":")[1].replace("\"","").replace("}","");
                        int baseOffset = xc+20;
                        String s = I18n.translate("goat_detector.coww.hud_mode")
                                +": \""+goatFormatted+"\"";
                        client.textRenderer.drawWithShadow(s, baseOffset, yc-11,0xFFFFFF);
                        if (goatDetector.getCachedState().get(POWERED)) {
                            String s2 = I18n.translate("goat_detector.coww.hud_active") + "!";
                            int offset = (baseOffset + (client.textRenderer.getStringWidth(s) / 2)) - (client.textRenderer.getStringWidth(s2) / 2);
                            client.getItemRenderer().renderGuiItemIcon(new ItemStack(Items.REDSTONE_TORCH),offset-12,yc-6);
                            client.textRenderer.drawWithShadow(s2, offset, yc, 0xFFFFFF);
                        }
                        ItemStack stack = client.player!=null ? client.player.getMainHandStack() : null;
                        if (stack.getItem().equals(Items.NAME_TAG) && stack.hasTag()){
                            String stackGoatFormatted = stack.getSubTag("display").get("Name").asString().split(":")[1].replace("\"","").replace("}","");
                            String s3 = I18n.translate("goat_detector.coww.hud_taggable");
                            String s3ps = I18n.translate("goat_detector.coww.hud_taggable_ps")+": \""
                                    + stackGoatFormatted + "\"";
                            int offset = xc-client.textRenderer.getStringWidth(s3)/2;
                            client.getItemRenderer().renderGuiItemIcon(stack,xc-16-client.textRenderer.getStringWidth(s3)/2,yc+35);
                            client.textRenderer.drawWithShadow(s3, offset+1, yc+35,0xFFFFFF);
                            client.textRenderer.drawWithShadow(s3ps,offset+1,yc+45,0xFFFFFF);
                        }
                    }
                }
            }
        }
    }

    public static void drawTexturedModalRect(int x, int y, float z, int u, int v, int uMax, int vMax) {
        float fu = 0.01515F;
        float fv = 0.0667F;
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        tessellator.getBuffer().vertex(x, y + vMax, z).texture(u * fu, (v + vMax) * fv).next();
        tessellator.getBuffer().vertex(x + uMax, y + vMax, z).texture((u + uMax) * fu, (v + vMax) * fv).next();
        tessellator.getBuffer().vertex(x + uMax, y, z).texture((u + uMax) * fu, v * fv).next();
        tessellator.getBuffer().vertex(x, y, z).texture(u * fu, v * fv).next();
        tessellator.draw();
    }

    public static String toTitleCase(String givenString) {
        String[] splitString = givenString.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : splitString) {
            stringBuilder.append(Character.toUpperCase(string.charAt(0)))
                    .append(string.substring(1)).append(" ");
        }
        return stringBuilder.toString().trim();
    }
}
