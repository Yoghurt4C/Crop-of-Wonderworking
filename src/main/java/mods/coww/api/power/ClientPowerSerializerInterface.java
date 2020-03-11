package mods.coww.api.power;

/**
 * A blockentity that implements this will get it's receivePower call
 * called on both the client and the server. If this is not implemented,
 * the call will only occur serverside.
 */
public interface ClientPowerSerializerInterface extends PowerReceiverInterface { }
