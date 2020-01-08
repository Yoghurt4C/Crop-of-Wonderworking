package mods.coww.client;

import mods.coww.entity.CropWonderWorkingCauldronBlockEntityRenderer;
import mods.coww.rendering.CauldronHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import org.jetbrains.annotations.NotNull;

import static mods.coww.registry.CropWonderWorkingBlocks.*;


@SuppressWarnings("unused")
public class CropWonderWorkingClient implements ClientModInitializer {
    private MinecraftClient client;
    private static CropWonderWorkingClient INSTANCE;
    private CauldronHUD cauldronHUD;

    @Override
    public void onInitializeClient() {
        INSTANCE=this;

        BlockRenderLayerMapImpl.INSTANCE.putBlocks(RenderLayer.getCutout(),
                SWEED,
                REDLON,
                ATTACHED_REDLON,
                LAZULLIA,
                RAINBOW_CACTI,
                COWW_CAULDRON
        );

        BlockEntityRendererRegistry.INSTANCE.register(COWW_CAULDRON_BLOCKENTITY, CropWonderWorkingCauldronBlockEntityRenderer::new);

        //BlockRenderLayerMapImpl.INSTANCE.putBlocks(RenderLayer.getTranslucent(), );

        HudRenderCallback.EVENT.register(delta -> this.cauldronHUD.renderHUD());
    }

    public void onInit(@NotNull MinecraftClient client){
        this.cauldronHUD =new CauldronHUD(client,this);
    }

    public static CropWonderWorkingClient get(){return INSTANCE;}
}