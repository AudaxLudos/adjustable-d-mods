package adjustabledmods.ui.plugins;

import adjustabledmods.ui.DModRefitButton;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import org.lwjgl.util.vector.Vector2f;

public class ConfirmButtonPlugin extends BaseCustomUIPanelPlugin {
    public DModRefitButton refitButton;
    public ShipVariantAPI variant;

    public ConfirmButtonPlugin(DModRefitButton refitButton, ShipVariantAPI variant) {
        this.refitButton = refitButton;
        this.variant = variant;
    }

    @Override
    public void buttonPressed(Object buttonId) {
        if (!(buttonId instanceof String)) return;

        if (buttonId == "install_selected") {
            if (!this.refitButton.selectedInstallableDMods.isEmpty()) {
//                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(this.refitButton.getDModAddOrRemoveCost(variant, false));
                for (int i = 0; i < this.refitButton.selectedInstallableDMods.size(); i++) {
                    HullModSpecAPI dModSpec = this.refitButton.selectedInstallableDMods.get(i);
                    DModManager.setDHull(variant);
                    variant.removeSuppressedMod(dModSpec.getId());
                    variant.addPermaMod(dModSpec.getId(), false);
                }
                refitButton.refreshVariant();
                refitButton.closePanel();
                Global.getSoundPlayer().playSound("ui_survey_xp_gain", 2f, 1f, Global.getSoundPlayer().getListenerPos(), new Vector2f());
            }
        } else if (buttonId == "remove_selected") {
            if (!this.refitButton.selectedRemovableDMods.isEmpty()) {
//                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(this.refitButton.getDModAddOrRemoveCost(variant, true));
                for (int i = 0; i < this.refitButton.selectedRemovableDMods.size(); i++) {
                    HullModSpecAPI spec = this.refitButton.selectedRemovableDMods.get(i);
                    DModManager.setDHull(variant);
                    DModManager.removeDMod(variant, spec.getId());
                }
                refitButton.refreshVariant();
                refitButton.closePanel();
                Global.getSoundPlayer().playSound("ui_survey_xp_gain", 4f, 1f, Global.getSoundPlayer().getListenerPos(), new Vector2f());
            }
        }
    }
}
