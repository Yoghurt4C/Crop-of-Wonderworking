package mods.coww.api.power;

/**
 * A blockentity that will only send a few packets rather than one per every entity collision.
 * markDispatchable marks that this BE needs to send a packet. Further handling is to be done
 * in the BE itself.
 */
public interface ThrottledPacketInterface {

    void markDispatchable();

}
