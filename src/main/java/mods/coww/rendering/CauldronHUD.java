package mods.coww.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import mods.coww.client.CropWonderWorkingClient;
import mods.coww.entity.CropWonderWorkingCauldronBlockEntity;
import mods.coww.entity.CropWonderWorkingCauldronInventory;
import mods.coww.recipes.CauldronRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Optional;

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
                    Block block = state == null ? null : state.getBlock();
                    BlockEntity tile = bpos != null ? client.world.getBlockEntity(bpos) : null;
                    if (tile instanceof CropWonderWorkingCauldronBlockEntity) {
                        Inventory inventory = (CropWonderWorkingCauldronInventory) tile;
                        CropWonderWorkingCauldronBlockEntity cauldron = (CropWonderWorkingCauldronBlockEntity) tile;

                        float angle = -90;
                        int radius = 24;
                        int items = 0;
                        if (inventory != null) {
                            for (int i = 0; i < inventory.getInvSize(); i++) {
                                if (inventory.getInvStack(i).isEmpty()) {
                                    break;
                                }
                                items++;
                            }

                            if (items > 0) {
                                float anglePer = 360F / items;
                                final Optional<CauldronRecipe> match = client.world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, inventory, client.world);
                                if (match.isPresent()) {
                                    RenderSystem.enableAlphaTest();
                                    RenderSystem.color4f(1F, 1F, 1F, 1F);
                                    client.getTextureManager().bindTexture(new Identifier("coww:textures/gui/manahud.png"));
                                    drawTexturedModalRect(xc + radius + 9, yc - 8, 0, 0, 8, 22, 15);
                                    RenderSystem.disableAlphaTest();

                                    ItemStack stack = match.get().getOutput();
                                    ItemStack[] cataList = match.get().getCatalyst().getMatchingStacksClient();
                                    ItemStack catalyst = cataList[client.world.random.nextInt(cataList.length)];

                                    client.getItemRenderer().renderGuiItemIcon(stack, xc + radius + 32, yc - 8);
                                    client.getItemRenderer().renderGuiItemIcon(catalyst, xc + radius + 16, yc + 6);
                                    RenderSystem.pushMatrix();
                                    RenderSystem.translated(0,0,200);
                                    client.textRenderer.drawWithShadow("+", xc + radius + 14, yc + 10, 0xFFFFFF);
                                    RenderSystem.popMatrix();
                                }

                                for (int i = 0; i < items; i++) {
                                    double xPos = xc + Math.cos(angle * Math.PI / 180D) * radius - 8;
                                    double yPos = yc + Math.sin(angle * Math.PI / 180D) * radius - 8;
                                    RenderSystem.translated(xPos, yPos, 0);
                                    client.getItemRenderer().renderGuiItemIcon(inventory.getInvStack(i), 0, 0);
                                    RenderSystem.translated(-xPos, -yPos, 0);
                                    angle += anglePer;
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
                    }
                }
            }
        }
    }

    public static void drawTexturedModalRect(int x, int y, float z, int u, int v, int offsetX, int offsetY) {
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        tessellator.getBuffer().vertex(x, y + offsetY, z).texture(u * f, (v + offsetY) * f).next();
        tessellator.getBuffer().vertex(x + offsetX, y + offsetY, z).texture((u + offsetX) * f, (v + offsetY) * f).next();
        tessellator.getBuffer().vertex(x + offsetX, y, z).texture((u + offsetX) * f, v * f).next();
        tessellator.getBuffer().vertex(x, y, z).texture(u * f, v * f).next();
        tessellator.draw();
    }
}
