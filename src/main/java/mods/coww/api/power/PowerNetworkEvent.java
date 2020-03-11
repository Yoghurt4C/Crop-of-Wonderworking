package mods.coww.api.power;

import mods.coww.api.callbacks.*;
import mods.coww.power.internal.PowerNetworkSerializer;
import net.minecraft.block.entity.BlockEntity;

public class PowerNetworkEvent implements AddCollectorCallback, RemoveCollectorCallback, AddTankCallback, RemoveTankCallback {
    public static PowerNetworkEvent INSTANCE;
    public final BlockEntity be;
    public final PowerBlockType type;
    public final Action action;

    public PowerNetworkEvent(BlockEntity be, PowerBlockType type, Action action) {
        this.be = be;
        this.type = type;
        this.action = action;
    }

    public void addCollector(BlockEntity be) { AddCollectorCallback.EVENT.invoker().addCollector(be); }

    public void removeCollector(BlockEntity be) {
        RemoveCollectorCallback.EVENT.invoker().removeCollector(be);
    }

    public void addTank(BlockEntity be) { AddTankCallback.EVENT.invoker().addTank(be); }

    public void removeTank(BlockEntity be) {
        RemoveTankCallback.EVENT.invoker().removeTank(be);
    }

    public enum PowerBlockType {
        TANK, COLLECTOR
    }

    public enum Action {
        REMOVE, ADD
    }
}
