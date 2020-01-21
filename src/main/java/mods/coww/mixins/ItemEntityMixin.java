package mods.coww.mixins;

import mods.coww.entity.IItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements IItemEntity {

    @Unique
    private boolean ITEM_CAULDRON_SPAWNED;

    protected ItemEntityMixin(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType,world);
    }

    public boolean coww_getSpawnedByCauldron() {return this.ITEM_CAULDRON_SPAWNED;}

    public void coww_setSpawnedByCauldron(boolean bool) {
        this.ITEM_CAULDRON_SPAWNED=bool;
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void onReadCustomDataToTag(CompoundTag tag, CallbackInfo info) {
        ITEM_CAULDRON_SPAWNED = tag.getBoolean("CauldronSpawned");
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void onWriteCustomDataToTag(CompoundTag tag, CallbackInfo info) {
        tag.putBoolean("CauldronSpawned", ITEM_CAULDRON_SPAWNED);
    }
}
