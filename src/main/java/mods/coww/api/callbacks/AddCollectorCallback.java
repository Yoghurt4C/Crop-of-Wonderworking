package mods.coww.api.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.entity.BlockEntity;

public interface AddCollectorCallback {
    Event<AddCollectorCallback> EVENT = EventFactory.createArrayBacked(AddCollectorCallback.class, listeners ->
            be -> {
                for(AddCollectorCallback listener : listeners)
                    listener.addCollector(be);
            });

    void addCollector(BlockEntity be);
}
