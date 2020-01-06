package mods.coww.client;

import mods.coww.registry.CropWonderWorkingBlocks;
import mods.coww.rendering.CauldronHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CropWonderWorkingClient implements ClientModInitializer {
    private MinecraftClient client;
    private static CropWonderWorkingClient INSTANCE;
    private CauldronHUD cauldronHUD;

    @Override
    public void onInitializeClient() {
        INSTANCE=this;
        BlockRenderLayerMapImpl.INSTANCE.putBlocks(RenderLayer.getCutout(),
                CropWonderWorkingBlocks.SWEED,
                CropWonderWorkingBlocks.REDLON,
                CropWonderWorkingBlocks.ATTACHED_REDLON,
                CropWonderWorkingBlocks.LAZULLIA,
                CropWonderWorkingBlocks.RAINBOW_CACTI,

                CropWonderWorkingBlocks.COWW_CAULDRON
        );

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            return BiomeColors.getWaterColor(view, pos);
        }, CropWonderWorkingBlocks.COWW_CAULDRON);

        //BlockRenderLayerMapImpl.INSTANCE.putBlocks(RenderLayer.getTranslucent(), );

        HudRenderCallback.EVENT.register(delta -> this.cauldronHUD.renderHUD());
    }

    public void onInit(@NotNull MinecraftClient client){
        this.cauldronHUD =new CauldronHUD(client,this);
    }

    public static CropWonderWorkingClient get(){return INSTANCE;}
}