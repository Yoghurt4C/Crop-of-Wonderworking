package mods.coww.power;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import mods.coww.registry.cowwBlocks;

public class cowwPowerTank extends SimpleFixedFluidInv {

    public cowwPowerTank(FluidAmount tankCapacity) {
        super(1, tankCapacity);
    }

    @Override
    public boolean isFluidValidForTank(int tank, FluidKey fluid) {
        return fluid.equals(FluidKeys.get(cowwBlocks.THAUMATURGES_HONEY));
    }
}
