package adjustabledmods.ui.plugins;

import adjustabledmods.ui.RefitButtonUI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import org.lwjgl.util.vector.Vector2f;

public class ConfirmButtonPlugin extends BaseCustomUIPanelPlugin {
    public RefitButtonUI refitButton;
    public ShipVariantAPI variant;

    public ConfirmButtonPlugin(RefitButtonUI refitButton, ShipVariantAPI variant) {
        this.refitButton = refitButton;
        this.variant = variant;
    }

    @Override
    public void buttonPressed(Object buttonId) {
        if (!(buttonId instanceof String)) return;

        if (buttonId == "install_selected") {
            if (this.refitButton.selectedInstallableDMod != null) {
                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(this.refitButton.getDModAddOrRemoveCost(variant, false));
                HullModSpecAPI spec = this.refitButton.selectedInstallableDMod;
                DModManager.setDHull(variant);
                variant.removeSuppressedMod(spec.getId());
                variant.addPermaMod(spec.getId(), false);

                this.refitButton.closePanel();
                Global.getSoundPlayer().playSound("ui_survey_xp_gain", 2f, 1f, Global.getSoundPlayer().getListenerPos(), new Vector2f());
                Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.REFIT);
            }
        } else if (buttonId == "remove_selected") {
            if (this.refitButton.selectedRemovableDMod != null) {
                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(this.refitButton.getDModAddOrRemoveCost(variant, true));
                HullModSpecAPI spec = this.refitButton.selectedRemovableDMod;
                DModManager.setDHull(variant);
                DModManager.removeDMod(variant, spec.getId());

                this.refitButton.closePanel();
                Global.getSoundPlayer().playSound("ui_survey_xp_gain", 4f, 1f, Global.getSoundPlayer().getListenerPos(), new Vector2f());
                Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.REFIT);
            }
        }
    }
}
