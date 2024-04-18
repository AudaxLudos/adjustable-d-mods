package adjustabledmods.ui.plugins;

import adjustabledmods.ui.RefitButtonUI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import org.lwjgl.util.vector.Vector2f;

public class ConfirmPlugin extends BaseCustomUIPanelPlugin {
    public RefitButtonUI refitButton;
    public ShipVariantAPI variant;

    public ConfirmPlugin(RefitButtonUI refitButton, ShipVariantAPI variant) {
        this.refitButton = refitButton;
        this.variant = variant;
    }

    @Override
    public void buttonPressed(Object buttonId) {
        if (!(buttonId instanceof String)) return;

        if (buttonId == "install") {
            if (this.refitButton.selectedInstallableDMod != null) {
                HullModSpecAPI spec = this.refitButton.selectedInstallableDMod;
                DModManager.setDHull(variant);
                variant.removeSuppressedMod(spec.getId());
                variant.addPermaMod(spec.getId(), false);

                this.refitButton.closePanel();
                Global.getSoundPlayer().playSound("ui_survey_xp_gain", 2f, 2f, Global.getSoundPlayer().getListenerPos(), new Vector2f());
                Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.REFIT);
            }
        } else if (buttonId == "remove") {
            if (this.refitButton.selectedRemovableDMod != null) {
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
