package mods.coww.api.power;

import mods.coww.api.internal.PowerBurstInterface;

import java.util.UUID;

/**
 * This describes an interface of a Mana Sender block that should be able to ping-back-able
 * by a burst to tell it that the burst is still alive.
 */
public interface PingableInterface {

    /**
     * Pings this object back, telling it that the burst passed in is still alive
     * in the world. The UUID parameter should be the UUID with which the burst
     * was created, this is used to let the object handle the check for if it's the
     * correct ID internally. PowerBurstInterface implementations should do this every tick.
     */
    void pingback(PowerBurstInterface burst, UUID expectedIdentity);

    /**
     * @return A unique and persistent identifier for this PingableInterface.
     */
    UUID getIdentifier();

}