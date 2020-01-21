package mods.coww.registry;

import mods.coww.CropWonderWorking;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class CropWonderWorkingSounds {
    public static SoundEvent HOES_MAD;

    public static void init(){
        HOES_MAD=register("hoes_mad");
    }

    public static SoundEvent register(String name){
        return Registry.register(Registry.SOUND_EVENT, CropWonderWorking.cowwIdentifier(name), new SoundEvent(CropWonderWorking.cowwIdentifier(name)));
    }
}
