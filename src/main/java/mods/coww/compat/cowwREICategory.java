package mods.coww.compat;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.LabelWidget;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.Widget;
import mods.coww.recipes.CauldronRecipe;
import mods.coww.registry.cowwBlocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static mods.coww.client.rendering.CauldronHUD.toTitleCase;

public class cowwREICategory<R extends CauldronRecipe> implements RecipeCategory<cowwREIDisplay<R>> {

    @Override
    public Identifier getIdentifier() { return R.Type.ID; }

    @Override
    public EntryStack getLogo() {
        return EntryStack.create(cowwBlocks.COWW_CAULDRON);
    }

    @Override
    public String getCategoryName() {
        return I18n.translate("cauldron.coww.category");
    }

    @Override
    public List<Widget> setupDisplay(Supplier<cowwREIDisplay<R>> recipeDisplaySupplier, Rectangle bounds) {
        MinecraftClient client = MinecraftClient.getInstance();
        Point point = new Point(bounds.getCenterX()-10,bounds.getCenterY()-42), center = new Point(bounds.getCenterX()-10, bounds.getCenterY()-10);
        List<List<EntryStack>> input = recipeDisplaySupplier.get().getInputEntries();
        String fluidString = recipeDisplaySupplier.get().getFluid().split(":")[1];
        List<Widget> widgets = Lists.newLinkedList(Collections.singletonList(new RecipeBaseWidget(bounds) {
            @Override
            public void render(int mouseX, int mouseY, float delta) {
                RenderSystem.pushMatrix();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableBlend();
                client.getTextureManager().bindTexture(new Identifier("coww:textures/gui/cauldron_recipe.png"));
                blit(center.getX()-23, center.getY()-39, 0, 0, 101,78, 128, 128);
                RenderSystem.disableBlend();
                RenderSystem.disableAlphaTest();
                RenderSystem.translated(0,0,250);
                client.textRenderer.drawWithShadow("+",center.getX() + 33, center.getY() - 29, 0xFFFFFF);
                RenderSystem.translated(0,0,-250);
                RenderSystem.popMatrix();
            }
        }));

        double angleBetweenEach = 360.0 / input.size();
        String fluidInfo = I18n.translate("cauldron.coww.rei_fluid_info")+" "+toTitleCase(fluidString);

        for (List<EntryStack> entryStacks : input) {
            widgets.add(EntryWidget.create(point.x, point.y).entries(entryStacks).noBackground());
            point=rotatePointAbout(point,center,angleBetweenEach);
        }
        EntryStack cauldron = getLogo().copy();
        cauldron.setting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, stack ->{
            List<String> tooltip=new ArrayList<>();
            tooltip.add("§o§7"+I18n.translate("cauldron.coww.show_recipes"));
            return tooltip;
        });
        widgets.add(new LabelWidget(new Point (bounds.getCenterX(),bounds.getCenterY()+42),fluidInfo));
        widgets.add(CauldronEntryWidget.create(center.getX(),center.getY()-1).entry(cauldron).noHighlight().noBackground());
        widgets.add(EntryWidget.create(center.getX()+35,center.getY()-32).entries(recipeDisplaySupplier.get().getCatalysts()).noBackground());
        widgets.add(EntryWidget.create(center.getX()+55, center.getY()-12).entries(recipeDisplaySupplier.get().getOutputEntries()).noBackground());
        return widgets;
    }

    private Point rotatePointAbout(Point in, Point about, double degrees) {
        double rad = degrees * Math.PI / 180.0;
        double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
        double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
        return new Point((int) newX, (int) newY);
    }

    @Override
    public int getMaximumRecipePerPage() {
        return 1;
    }

    public int getDisplayHeight() {
        return 140;
    }
}
