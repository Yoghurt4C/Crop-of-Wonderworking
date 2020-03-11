package mods.coww.api.power;

/**
 * Any blockentity that implements this can receive power from power bursts.
 */
public interface PowerReceiverInterface extends PowerBlockInterface {

    /**
     * Is this power receiver full? Being full means no bursts will be sent.
     */
    boolean isFull();

    /**
     * Called when this receiver receives power.
     */
    void receivePower(int power);

    /**
     * Can this blockentity receive power from bursts? Generally set to false for
     * implementations of PowerCollectorInterface.
     */
    boolean canReceivePowerFromBursts();

}
