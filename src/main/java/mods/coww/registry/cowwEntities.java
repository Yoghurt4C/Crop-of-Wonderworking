package mods.coww.registry;

import mods.coww.CropWonderWorking;
import mods.coww.entity.power.PowerBurstEntity;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public class cowwEntities {
    public static final EntityType<PowerBurstEntity> POWER_BURST = FabricEntityTypeBuilder.create(EntityCategory.MISC, (EntityType.EntityFactory<PowerBurstEntity>) PowerBurstEntity::new).size(EntityDimensions.fixed(0f,0f)).trackable(64,10,true).build();

    public static void init(){
        register("power_burst", POWER_BURST);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> entity) {
        return Registry.register(Registry.ENTITY_TYPE, CropWonderWorking.cowwIdentifier(name), entity);
    }
}
