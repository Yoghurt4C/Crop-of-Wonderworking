package mods.coww.client.models;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import mods.coww.CropWonderWorking;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static mods.coww.CropWonderWorking.cowwIdentifier;

public class ToddModel implements UnbakedModel {
    public static final ToddModel INSTANCE = new ToddModel();
    private static final SpriteIdentifier TODD = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, cowwIdentifier("item/todd"));

    @Override
    public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> spriteMap, ModelBakeSettings bakeSettings, Identifier identifier){
        final Identifier toddJsonId = cowwIdentifier("item/todd");
        final BakedModel toddJsonModel = modelLoader.getOrLoadModel(toddJsonId).bake(modelLoader,spriteMap, bakeSettings, toddJsonId);
        return new BakedToddModel(spriteMap.apply(TODD), toddJsonModel);
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> unresolvedTextureReferences){
        return ImmutableSet.of(TODD);
    }

    @Override
    public Collection<Identifier> getModelDependencies(){
        return ImmutableSet.of(cowwIdentifier("item/todd"));
    }

    private static final class BakedToddModel implements BakedModel, FabricBakedModel{
        private final Sprite todd;
        private final BakedModel ToddJSONModel;
        protected final Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        protected final RenderMaterial layer = renderer.materialFinder().blendMode(0, BlendMode.CUTOUT).find();

        private BakedToddModel(Sprite todd, BakedModel toddJSONModel){
            this.todd = todd;
            this.ToddJSONModel=toddJSONModel;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, Random random){
            return Collections.emptyList();
        }

        @Override
        public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
            emitQuads(context.getEmitter());
        }

        public void emitQuads(QuadEmitter qe) {
            for (Direction face:Direction.values()){
                emitTodd(qe,face);
            }
        }

        public void emitTodd(QuadEmitter qe, Direction face){
            qe.material(layer)
                    .square(face, 0f,0f,1f,1f,0f)
                    .spriteColor(0,-1,-1,-1,-1)
                    .spriteBake(0,todd,MutableQuadView.BAKE_LOCK_UV)
                    .emit();
        }

        @Override
        public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
            context.getEmitter().material(layer)
                    .square(Direction.SOUTH, 0f,0f,1f,1f,1f)
                    .spriteColor(0,-1,-1,-1,-1)
                    .spriteBake(0,todd,MutableQuadView.BAKE_LOCK_UV)
                    .emit();
        }

        //todo actually use this whenever it's possible to
        protected Mesh createMesh() {
            final MeshBuilder mb = renderer.meshBuilder();
            Random random = new Random();
            random.setSeed(42);
            emitModel(null,ToddJSONModel,mb.getEmitter());
            return mb.build();
        }

        void emitModel (BlockState blockState, BakedModel model, QuadEmitter qe) {
            Random random = new Random();

            for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
                Direction cullFace = ModelHelper.faceFromIndex(i);
                random.setSeed(42);
                List<BakedQuad> quads = model.getQuads(blockState, cullFace, random);

                if (quads.isEmpty()) {
                    continue;
                }

                for (final BakedQuad q : quads) {
                    qe.fromVanilla(q.getVertexData(), 0, false);
                    qe.cullFace(cullFace);
                    qe.nominalFace(q.getFace());
                    qe.colorIndex(q.getColorIndex());
                    qe.emit();
                }
            }
        }

        @Override
        public boolean useAmbientOcclusion(){
            return false;
        }

        @Override
        public boolean hasDepthInGui(){
            return false;
        }

        @Override
        public boolean isBuiltin(){
            return false;
        }

        @Override
        public Sprite getSprite(){
            return todd;
        }

        @Override
        public ModelItemPropertyOverrideList getItemPropertyOverrides(){
            return ModelItemPropertyOverrideList.EMPTY;
        }

        @Override
        public boolean isVanillaAdapter(){
            return false;
        }

        @Override
        public ModelTransformation getTransformation(){
            return ModelTransformation.NONE;
        }
    }

    public static enum ToddVariantProvider implements ModelVariantProvider{
        INSTANCE;

        @Override
        public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException{
            return modelId.getNamespace().equals(CropWonderWorking.modid) ? ToddModel.INSTANCE : null;
        }
    }
}
