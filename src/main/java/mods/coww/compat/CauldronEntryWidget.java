package mods.coww.compat;

import me.shedaniel.math.api.Point;
import me.shedaniel.rei.api.ClientHelper;
import me.shedaniel.rei.api.ConfigObject;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.QueuedTooltip;
import me.shedaniel.rei.impl.ScreenHelper;
import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CauldronEntryWidget extends EntryWidget {

    protected CauldronEntryWidget(int x, int y) {
        super(x, y);
    }

    public static CauldronEntryWidget create(int x, int y) {
        return new CauldronEntryWidget(x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!interactable)
            return false;
        if (containsMouse(mouseX, mouseY)) {
            return ClientHelper.getInstance().executeViewAllRecipesFromCategory(cowwREIPlugin.CAULDRON);
        }
        return false;
    }

    @Override
    protected void queueTooltip(int mouseX, int mouseY, float delta) {
        List<String> baseTooltips = new ArrayList<>();
        baseTooltips.add(I18n.translate("block.coww.cauldron"));
        baseTooltips.add("§o§7"+I18n.translate("cauldron.coww.show_recipes"));
        QueuedTooltip tooltip = QueuedTooltip.create(new Point(mouseX,mouseY),baseTooltips);
        if (tooltip != null) {
            if (interactableFavorites && ConfigObject.getInstance().doDisplayFavoritesTooltip() && !ConfigObject.getInstance().getFavoriteKeyCode().isUnknown()) {
                String name = ConfigObject.getInstance().getFavoriteKeyCode().getLocalizedName();
                if (reverseFavoritesAction())
                    tooltip.getText().addAll(Arrays.asList(I18n.translate("text.rei.remove_favorites_tooltip", name).split("\n")));
                else
                    tooltip.getText().addAll(Arrays.asList(I18n.translate("text.rei.favorites_tooltip", name).split("\n")));
            }
            ScreenHelper.getLastOverlay().addTooltip(tooltip);
        }
    }
}
