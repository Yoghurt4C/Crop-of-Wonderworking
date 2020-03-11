package mods.coww.api.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.entity.BlockEntity;

public interface RemoveCollectorCallback {
    Event<RemoveCollectorCallback> EVENT = EventFactory.createArrayBacked(RemoveCollectorCallback.class, listeners ->
            be -> {
                for(RemoveCollectorCallback listener : listeners)
                    listener.removeCollector(be);
            });

    void removeCollector(BlockEntity be);
}
