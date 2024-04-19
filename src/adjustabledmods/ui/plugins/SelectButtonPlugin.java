package adjustabledmods.ui.plugins;

import adjustabledmods.ui.RefitButtonUI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.Sys;

import java.util.List;

public class SelectButtonPlugin extends BaseCustomUIPanelPlugin {
    public RefitButtonUI refitButton;
    public ShipVariantAPI variant;
    public boolean isInstalled;

    public SelectButtonPlugin(RefitButtonUI refitButton, ShipVariantAPI variant, boolean isInstalled) {
        this.refitButton = refitButton;
        this.variant = variant;
        this.isInstalled = isInstalled;
    }

    @Override
    public void buttonPressed(Object buttonId) {
        if (!(buttonId instanceof HullModSpecAPI)) return;

        if (!isInstalled) {
            this.refitButton.selectedInstallableDMod = (HullModSpecAPI) buttonId;
        } else {
            this.refitButton.selectedRemovableDMod = (HullModSpecAPI) buttonId;
        }

        LabelAPI costDModText = this.refitButton.costToInstallDModText;
        List<ButtonAPI> dModButtons = this.refitButton.installableDModButtons;
        ButtonAPI selectedHullModButton = this.refitButton.selectedInstallableDModButton;
        if (isInstalled) {
            costDModText = this.refitButton.costToRemoveDModText;
            dModButtons = this.refitButton.removableDModButtons;
            selectedHullModButton = this.refitButton.selectedRemovableDModButton;
        }

        costDModText.setText(Misc.getDGSCredits(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled)));

        boolean anyChecked = false;
        for (ButtonAPI button : dModButtons) {
            if (button.getCustomData() == buttonId && !button.isHighlighted()) {
                button.highlight();
                anyChecked = true;
                continue;
            }
            if (button.isHighlighted()) {
                button.unhighlight();
            }
        }

        if (!anyChecked) {
            this.refitButton.selectedInstallableDMod = null;
            this.refitButton.selectedRemovableDMod = null;
            costDModText.setText(Misc.getDGSCredits(0));
        }

        if (selectedHullModButton != null)
            selectedHullModButton.setEnabled(
                    (this.refitButton.selectedInstallableDMod != null || this.refitButton.selectedRemovableDMod != null)
                    && (DModManager.getNumDMods(variant) < DModManager.MAX_DMODS_FROM_COMBAT || isInstalled)
                    && Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= this.refitButton.getDModAddOrRemoveCost(variant, isInstalled));
    }
}
