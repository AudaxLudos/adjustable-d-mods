package adjustabledmods;

import com.fs.starfarer.api.Global;
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
        button.setButtonPressedSound(isEnabled ? "ui_button_pressed" : "ui_button_disabled_pressed");
        button.setGlowBrightness(isEnabled ? 0.56f : 1.2f);
        button.setHighlightBrightness(0.6f);
        button.setQuickMode(isEnabled);

        if (isHighlighted) {
            button.highlight();
        } else {
            button.unhighlight();
        }
    }

    public static boolean isSelectionAboveDModsLimit(List<HullModSpecAPI> hullmods, ShipVariantAPI variant) {
        return hullmods.size() >= DModManager.MAX_DMODS_FROM_COMBAT - DModManager.getNumDMods(variant);
    }

    public static boolean canPlayerAffordCost(float cost) {
        return Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= cost;
    }
}
