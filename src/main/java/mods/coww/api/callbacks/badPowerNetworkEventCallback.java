/*
package mods.coww.api.callbacks;

import mods.coww.api.power.PowerNetworkEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.entity.BlockEntity;

public interface PowerNetworkEventCallback {
        Event<PowerNetworkEventCallback> EVENT = EventFactory.createArrayBacked(PowerNetworkEventCallback.class, listeners ->
                new PowerNetworkEventCallback() {
                    @Override
                    public void addCollector(BlockEntity be) {
                        for (PowerNetworkEventCallback listener:listeners){ listener.addCollector(be); }
                    }

                    @Override
                    public void removeCollector(BlockEntity be) {
                        for (PowerNetworkEventCallback listener:listeners){ listener.removeCollector(be); }
                    }

                    @Override
                    public void addTank(BlockEntity be) {
                        for (PowerNetworkEventCallback listener:listeners){ listener.addTank(be); }
                    }

                    @Override
                    public void removeTank(BlockEntity be) {
                        for (PowerNetworkEventCallback listener:listeners){ listener.removeTank(be); }
                    }
                }
        );

        void addCollector(BlockEntity be);
        void removeCollector(BlockEntity be);
        void addTank(BlockEntity be);
        void removeTank(BlockEntity be);
}
 */