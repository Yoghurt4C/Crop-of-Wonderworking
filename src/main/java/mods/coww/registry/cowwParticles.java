package mods.coww.registry;

import mods.coww.CropWonderWorking;
import mods.coww.particles.HexagonParticle.HexagonParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class cowwParticles {
    public static final ParticleType<HexagonParticleEffect> HEXAGON_PARTICLE_EFFECT = register("hexagon_fx", FabricParticleTypes.complex(HexagonParticleEffect.PARAMETERS_FACTORY));

    public static void init(){
    }

    private static <T extends ParticleEffect> ParticleType<T> register(String name, ParticleType<T> particleType) {
        return Registry.register(Registry.PARTICLE_TYPE, CropWonderWorking.cowwIdentifier(name), particleType);
    }
}
