package adjustabledmods.ui.plugins;

import adjustabledmods.ui.DModRefitButton;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;

public class SelectButtonPlugin extends BaseCustomUIPanelPlugin {
    public DModRefitButton refitButton;
    public ShipVariantAPI variant;
    public boolean isInstalled;

    public SelectButtonPlugin(DModRefitButton refitButton, ShipVariantAPI variant, boolean isInstalled) {
        this.refitButton = refitButton;
        this.variant = variant;
        this.isInstalled = isInstalled;
    }

    @Override
    public void buttonPressed(Object buttonId) {
        if (!(buttonId instanceof HullModSpecAPI)) return;

        if (!isInstalled) {
            if (!refitButton.selectedInstallableDMods.contains((HullModSpecAPI) buttonId)) {
                refitButton.selectedInstallableDMods.add((HullModSpecAPI) buttonId);
            } else {
                refitButton.selectedInstallableDMods.remove((HullModSpecAPI) buttonId);
            }
        } else {
            if (!refitButton.selectedRemovableDMods.contains((HullModSpecAPI) buttonId)) {
                refitButton.selectedRemovableDMods.add((HullModSpecAPI) buttonId);
            } else {
                refitButton.selectedRemovableDMods.remove((HullModSpecAPI) buttonId);
            }
        }

        LabelAPI costDModText = this.refitButton.costToInstallDModText;
        List<ButtonAPI> dModButtons = this.refitButton.installableDModButtons;
        List<HullModSpecAPI> selectedDMods = this.refitButton.selectedInstallableDMods;
        ButtonAPI selectedHullModButton = this.refitButton.selectedInstallableDModButton;
        if (isInstalled) {
            costDModText = this.refitButton.costToRemoveDModText;
            dModButtons = this.refitButton.removableDModButtons;
            selectedDMods = this.refitButton.selectedRemovableDMods;
            selectedHullModButton = this.refitButton.selectedRemovableDModButton;
        }

        costDModText.setText(Misc.getDGSCredits(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled)));

        for (ButtonAPI button : dModButtons) {
            if (!isInstalled) {
                int dModsLeft = DModManager.MAX_DMODS_FROM_COMBAT - DModManager.getNumDMods(variant);
                button.setEnabled(selectedDMods.size() < dModsLeft || selectedDMods.contains((HullModSpecAPI) button.getCustomData()));
            }

            System.out.println("test");

            if (selectedDMods.contains((HullModSpecAPI) button.getCustomData())) {
                button.highlight();
            } else {
                button.unhighlight();
            }
        }

        if (selectedHullModButton != null)
            selectedHullModButton.setEnabled((!this.refitButton.selectedInstallableDMods.isEmpty() || !this.refitButton.selectedRemovableDMods.isEmpty())
                            && (DModManager.getNumDMods(variant) < DModManager.MAX_DMODS_FROM_COMBAT || isInstalled)
                            && Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= this.refitButton.getDModAddOrRemoveCost(variant, isInstalled));
    }
}
