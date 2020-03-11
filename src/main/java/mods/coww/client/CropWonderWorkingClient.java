package mods.coww.client;

import mods.coww.client.models.ToddModel;
import mods.coww.client.rendering.PowerBurstEntityRenderer;
import mods.coww.entity.CauldronBlockEntityRenderer;
import mods.coww.client.rendering.CauldronHUD;
import mods.coww.registry.cowwEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static mods.coww.registry.cowwBlocks.*;


@SuppressWarnings("unused")
public class CropWonderWorkingClient implements ClientModInitializer {
    private MinecraftClient client;
    private static CropWonderWorkingClient INSTANCE;
    private CauldronHUD cauldronHUD;
    private final static Calendar calendar = Calendar.getInstance();
    private Random random = new Random();

    @Override
    public void onInitializeClient() {
        INSTANCE=this;

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                SWEED,
                REDLON,
                ATTACHED_REDLON,
                LAZULLIA,
                RAINBOW_CACTI,
                BRIAR,
                COWW_CAULDRON,
                WONDERWORKING_ANVIL
        );

        BlockEntityRendererRegistry.INSTANCE.register(COWW_CAULDRON_BLOCKENTITY, CauldronBlockEntityRenderer::new);

        for (Boolean isToddDay : daysOfTheTodd) {
            if (isToddDay && random.nextInt(4) % 4 == 0) { ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> ToddModel.ToddVariantProvider.INSTANCE); break; }
        }

        //BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), );

        HudRenderCallback.EVENT.register(delta -> this.cauldronHUD.renderHUD());

        EntityRendererRegistry.INSTANCE.register(cowwEntities.POWER_BURST, (entityRenderDispatcher, context) -> new PowerBurstEntityRenderer(entityRenderDispatcher));
    }

    public void onInit(@NotNull MinecraftClient client){
        this.cauldronHUD =new CauldronHUD(client,this);
    }

    public static CropWonderWorkingClient get(){return INSTANCE;}

    public static boolean isDayOfTheTodd(int month, int day){
        long start = new GregorianCalendar(calendar.get(Calendar.YEAR), month, day,0,0).getTime().getTime();
        long span = TimeUnit.DAYS.convert(calendar.getTime().getTime() - start, TimeUnit.MILLISECONDS);
        return span >= 0 && span <= 1;
    }

    public static List<Boolean> daysOfTheTodd = new ArrayList<>();{
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.MARCH,25));//arena
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.SEPTEMBER,20));//daggerfall
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.MAY,1));//morrowind
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.MARCH,20));//oblivion
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.OCTOBER,28));//fallout 3 & the elder scrolls v skyrim special edition
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.OCTOBER,19));//new vegas
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.NOVEMBER,11));//the elder scrolls v skyrim
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.NOVEMBER,10));//fallout 4
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.NOVEMBER,14));//fallout 76
        daysOfTheTodd.add(isDayOfTheTodd(Calendar.JUNE,10));//the elder scrolls v skyrim very special edition
    }
}