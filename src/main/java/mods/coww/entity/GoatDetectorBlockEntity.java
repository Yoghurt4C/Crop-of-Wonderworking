package mods.coww.entity;

import mods.coww.registry.cowwBlocks;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;

import java.util.List;

import static net.minecraft.state.property.Properties.FACING;
import static net.minecraft.state.property.Properties.POWERED;

public class GoatDetectorBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {

    public String goat = "{\"text\":\"Goat\"}";

    public GoatDetectorBlockEntity() {
        super(cowwBlocks.GOAT_DETECTOR_BLOCKENTITY);
    }

    @Override
    public void tick() {
        if (world != null) {
            List<?> livingEntityList = world.getEntities(LivingEntity.class, new Box(pos.offset(this.getCachedState().get(FACING))), null);
            if (!livingEntityList.isEmpty()) {
                if (!this.getCachedState().get(POWERED)) {
                    for (Object obj : livingEntityList) {
                        LivingEntity entity = (LivingEntity) obj;
                        String goatFormatted = goat.split(":")[1].replace("\"","").replace("}","");
                        if (entity.getDisplayName().asFormattedString().toLowerCase().contains(goatFormatted.toLowerCase())
                                || entity.toString().contains(goatFormatted.toLowerCase())) {
                            world.getBlockTickScheduler().schedule(pos, this.getCachedState().getBlock(), 2);
                            break;
                        }
                    }
                }
            } else if (this.getCachedState().get(POWERED)){
                world.getBlockTickScheduler().schedule(pos, this.getCachedState().getBlock(), 2);
            }
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.goat=tag.getCompound("display").getString("Name");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        CompoundTag display = new CompoundTag();
        display.put("Name", StringTag.of(goat));
        tag.put("display",display);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag){this.fromTag(tag);}

    @Override
    public CompoundTag toClientTag(CompoundTag tag){return this.toTag(tag);}
}
