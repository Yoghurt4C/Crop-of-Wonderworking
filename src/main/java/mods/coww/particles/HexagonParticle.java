package mods.coww.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.coww.registry.cowwParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public class HexagonParticle extends SpriteBillboardParticle {

    public HexagonParticle(HexagonParticleEffect fx, World world, double x, double y, double z, double vX, double vY, double vZ, SpriteProvider sprites) {
        super(world, x, y, z, vX, vY, vZ);
        this.setSprite(sprites.getSprite(0,1));
        this.setColor(fx.red,fx.green,fx.blue);
        this.setColorAlpha(fx.alpha);
        this.scale = fx.scale;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class FACTORY implements ParticleFactory<HexagonParticleEffect> {
        private final FabricSpriteProvider sprites;

        public FACTORY(FabricSpriteProvider sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(HexagonParticleEffect parameters, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new HexagonParticle(parameters,world,x,y,z,velocityX,velocityY,velocityZ,sprites);
        }
    }
    
    public static class HexagonParticleEffect implements ParticleEffect{
        public static final HexagonParticleEffect RED = new HexagonParticleEffect(1.0F, 0.0F, 0.0F, 1.0F, 1.0F);

        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;
        private final float scale;

        public HexagonParticleEffect(float red, float green, float blue, float alpha, float scale) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            this.scale = MathHelper.clamp(scale, 0.01F, 4.0F);
        }

        public void write(PacketByteBuf buf) {
            buf.writeFloat(this.red);
            buf.writeFloat(this.green);
            buf.writeFloat(this.blue);
            buf.writeFloat(this.scale);
        }

        public String asString() {
            return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getId(this.getType()), this.red, this.green, this.blue, this.alpha, this.scale);
        }

        public ParticleType<HexagonParticleEffect> getType() {
            return cowwParticles.HEXAGON_PARTICLE_EFFECT;
        }

        @Environment(EnvType.CLIENT)
        public float getRed() {
            return this.red;
        }

        @Environment(EnvType.CLIENT)
        public float getGreen() {
            return this.green;
        }

        @Environment(EnvType.CLIENT)
        public float getBlue() {
            return this.blue;
        }

        @Environment(EnvType.CLIENT)
        public float getAlpha() { return this.alpha;}

        @Environment(EnvType.CLIENT)
        public float getScale() {
            return this.scale;
        }

        public static final Factory<HexagonParticleEffect> PARAMETERS_FACTORY = new Factory<HexagonParticleEffect>() {
            public HexagonParticleEffect read(ParticleType<HexagonParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
                stringReader.expect(' ');
                float r = (float)stringReader.readDouble();
                stringReader.expect(' ');
                float g = (float)stringReader.readDouble();
                stringReader.expect(' ');
                float b = (float)stringReader.readDouble();
                stringReader.expect(' ');
                float a = (float)stringReader.readDouble();
                stringReader.expect(' ');
                float s = (float)stringReader.readDouble();
                return new HexagonParticleEffect(r, g, b, a, s);
            }

            public HexagonParticleEffect read(ParticleType<HexagonParticleEffect> particleType, PacketByteBuf packetByteBuf) {
                return new HexagonParticleEffect(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
            }
        };
    }
}
