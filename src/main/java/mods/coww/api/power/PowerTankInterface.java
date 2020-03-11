package mods.coww.api.power;

import net.minecraft.util.DyeColor;

/**
 * Any TileEntity that implements this is considered a Mana Pool,
 * by which nearby functional flowers will pull mana from it.<br>
 * Mana Distributors will also accept it as valid output.<br><br>
 *
 * <b>Implementation Instructions:</b><br>
 * - Override invalidate() and onChunkUnload(), calling <i>ManaNetworkEvent.removePool(this);</i> on both.<br>
 * - On the first tick of onUpdate(), call <i>ManaNetworkEvent.addPool(this);</i>
 */
public interface PowerTankInterface extends PowerReceiverInterface {

    /**
     * Returns false if the power tank is "accepting" power from other power items,
     * true if it's "sending" power into them.
     */
    boolean isOutputtingPower();

    /**
     * @return The colour of this tank.
     * //todo Add colour capabilities to power tanks.
     */
    DyeColor getColor();

    /**
     * Sets the colour of this tank.
     * @param color The colour to set.
     */
    void setColor(DyeColor color);

}