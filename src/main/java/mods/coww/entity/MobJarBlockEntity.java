package mods.coww.entity;

import mods.coww.registry.CropWonderWorkingBlocks;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.registry.Registry;

public class MobJarBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {

    private Entity entity;
    private Identifier myEntityType;
    private CompoundTag entityData;
    private int tickCount = 0;

    public MobJarBlockEntity() { super(CropWonderWorkingBlocks.MOB_JAR_BLOCK_ENTITY); }

    public CompoundTag toTag(CompoundTag tag) {
        if (myEntityType != null) {
            super.toTag(tag);
            CompoundTag ent = new CompoundTag();
            tag.put("entityData", entityData);
            tag.putString("entity_id", myEntityType.toString());
        }
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.myEntityType = new Identifier(tag.getString("entity_id"));
        this.entityData = tag.getCompound("entityData");

    }


    public Entity getEntity() {
        if (entity == null) {
            if (myEntityType != null) {
                entity = Registry.ENTITY_TYPE.get(myEntityType).create(world);
                if(!world.isClient()){
                    world.getServer().getWorld(entity.dimension).spawnEntity(entity);
                }
                if(entity != null) {
                    entity.fromTag(entityData);
                    initializeTasks(entity);
                }
            }
        }
        return entity;
    }

    public void initializeTasks(Entity entity) {

    }

    public Identifier getMyEntityType(){
        return myEntityType;
    }
    public CompoundTag getMyEntityData(){ return entityData;}

    @Override
    public void fromClientTag(CompoundTag tag) {
        if (tag.contains("entity_id")) {
            if (tag.contains("entityData")) {
                entityData = tag.getCompound("entityData");
                myEntityType = new Identifier(tag.getString("entity_id"));
            }
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        if (myEntityType != null) {
            tag.putString("entity_id", myEntityType.toString());
            tag.put("entityData", entityData);
        }
        return tag;
    }

    public void tick() {
        if (entity != null) {
            //entity.getMoveControl().tick();
            entity.tick();
            if(!world.isClient){
                world.getServer().getWorld(entity.dimension).tickEntity(entity);
            }
        }
    }
}