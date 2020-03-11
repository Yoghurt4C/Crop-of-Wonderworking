package mods.coww.client.rendering;

import mods.coww.entity.power.PowerBurstEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.Identifier;

public class PowerBurstEntityRenderer extends EntityRenderer<PowerBurstEntity> {

    public PowerBurstEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) { super(entityRenderDispatcher); }

    @Override
    public Identifier getTexture(PowerBurstEntity entity) {
        return null;
    }
}
