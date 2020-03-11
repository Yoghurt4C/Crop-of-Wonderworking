package mods.coww.api.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.entity.BlockEntity;

public interface AddTankCallback {
    Event<AddTankCallback> EVENT = EventFactory.createArrayBacked(AddTankCallback.class, listeners ->
            be -> {
                for(AddTankCallback listener : listeners)
                    listener.addTank(be);
            });

    void addTank(BlockEntity be);
}
