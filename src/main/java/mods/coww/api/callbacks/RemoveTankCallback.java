package mods.coww.api.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.entity.BlockEntity;

public interface RemoveTankCallback {
    Event<RemoveTankCallback> EVENT = EventFactory.createArrayBacked(RemoveTankCallback.class, listeners ->
            be -> {
                for(RemoveTankCallback listener : listeners)
                    listener.removeTank(be);
            });

    void removeTank(BlockEntity be);
}
