package mods.coww.api.power;

import mods.coww.api.internal.PowerBurstInterface;

/**
 * Any TileEntity that implements this is considered a Mana Spreader,
 * by which can fire mana bursts as a spreader.
 */
public interface PowerSpreaderInterface extends PowerBlockInterface, PingableInterface, DirectionedInterface {

    public void setCanShoot(boolean canShoot);

    public int getBurstParticleTick();

    public void setBurstParticleTick(int i);

    public int getLastBurstDeathTick();

    public void setLastBurstDeathTick(int ticksExisted);

    public PowerBurstInterface runBurstSimulation();

}