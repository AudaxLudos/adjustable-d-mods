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
        if (!(buttonId instanceof String)) {
            return;
        }

        if (buttonId == "install_selected") {
            if (!this.refitButton.installSelectedDMods.isEmpty()) {
                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(this.refitButton.getDModAddOrRemoveCost(this.variant, false, 0f));
                for (int i = 0; i < this.refitButton.installSelectedDMods.size(); i++) {
                    HullModSpecAPI dModSpec = this.refitButton.installSelectedDMods.get(i);
                    DModManager.setDHull(this.variant);
                    this.variant.removeSuppressedMod(dModSpec.getId());
                    this.variant.addPermaMod(dModSpec.getId(), false);
                }
                this.refitButton.refreshVariant();
                this.refitButton.closePanel();
                Global.getSoundPlayer().playSound("ui_survey_xp_gain", 2f, 1f, Global.getSoundPlayer().getListenerPos(), new Vector2f());
            }
        } else if (buttonId == "remove_selected") {
            if (!this.refitButton.removeSelectedDMods.isEmpty()) {
                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(this.refitButton.getDModAddOrRemoveCost(this.variant, true, 0f));
                for (int i = 0; i < this.refitButton.removeSelectedDMods.size(); i++) {
                    HullModSpecAPI spec = this.refitButton.removeSelectedDMods.get(i);
                    DModManager.setDHull(this.variant);
                    DModManager.removeDMod(this.variant, spec.getId());
                }
                this.refitButton.refreshVariant();
                this.refitButton.closePanel();
                Global.getSoundPlayer().playSound("ui_survey_xp_gain", 4f, 1f, Global.getSoundPlayer().getListenerPos(), new Vector2f());
            }
        }
    }
}
