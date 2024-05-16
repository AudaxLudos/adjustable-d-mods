package adjustabledmods;

import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.ButtonAPI;

import java.util.List;

public class Utils {
    public static Boolean isShipAboveDModLimit(ShipVariantAPI variant) {
        return DModManager.getNumDMods(variant) >= DModManager.MAX_DMODS_FROM_COMBAT;
    }

    public static void setButtonEnabledOrHighlighted(ButtonAPI button, Boolean isEnabled, Boolean isHighlighted) {
        if (isEnabled) {
            button.setButtonPressedSound("ui_button_pressed");
            button.setGlowBrightness(0.56f);
            button.setHighlightBrightness(0.6f);
            button.setQuickMode(true);
        } else {
            button.setButtonPressedSound("ui_button_disabled_pressed");
            button.setGlowBrightness(1.2f);
            button.setHighlightBrightness(0.6f);
            button.setQuickMode(false);
        }

        if (isHighlighted)
            button.highlight();
        else
            button.unhighlight();
    }

    public static boolean isSelectionAboveDModsLimit(List<HullModSpecAPI> hullmods, ShipVariantAPI variant) {
        return hullmods.size() >= DModManager.MAX_DMODS_FROM_COMBAT - DModManager.getNumDMods(variant);
    }
}
