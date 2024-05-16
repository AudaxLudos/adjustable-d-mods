package adjustabledmods.ui.plugins;

import adjustabledmods.Utils;
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

        LabelAPI costDModText = this.refitButton.costToInstallDModText;
        List<ButtonAPI> dModButtons = this.refitButton.installableDModButtons;
        List<HullModSpecAPI> selectedDMods = this.refitButton.selectedInstallableDMods;
        ButtonAPI selectedHullModButton = this.refitButton.selectedInstallableDModButton;

        if (!isInstalled) {
            if (Utils.isShipAboveDModLimit(variant)) return;
            if (Utils.isSelectionAboveDModsLimit(selectedDMods, variant) && !selectedDMods.contains((HullModSpecAPI) buttonId)) return;
            if (isDModsNotInShip(selectedDMods, variant, (HullModSpecAPI) buttonId) && !selectedDMods.contains((HullModSpecAPI) buttonId)) return;
        } else {
            costDModText = this.refitButton.costToRemoveDModText;
            dModButtons = this.refitButton.removableDModButtons;
            selectedDMods = this.refitButton.selectedRemovableDMods;
            selectedHullModButton = this.refitButton.selectedRemovableDModButton;
        }

        if (!selectedDMods.contains((HullModSpecAPI) buttonId)) {
            selectedDMods.add((HullModSpecAPI) buttonId);
        } else {
            selectedDMods.remove((HullModSpecAPI) buttonId);
        }

        costDModText.setText(Misc.getDGSCredits(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled)));

        for (ButtonAPI button : dModButtons) {
            Utils.setButtonEnabledOrHighlighted(button, true, selectedDMods.contains((HullModSpecAPI) button.getCustomData()));

            if (!isInstalled)
                if (Utils.isShipAboveDModLimit(variant)
                        || (Utils.isSelectionAboveDModsLimit(selectedDMods, variant) && !selectedDMods.contains((HullModSpecAPI) button.getCustomData()))
                        || isDModsNotInShip(selectedDMods, variant, (HullModSpecAPI) button.getCustomData()) && !selectedDMods.contains((HullModSpecAPI) button.getCustomData()))
                    Utils.setButtonEnabledOrHighlighted(button, false, true);
        }

        if (selectedHullModButton != null)
            selectedHullModButton.setEnabled((!selectedDMods.isEmpty())
                            && (DModManager.getNumDMods(variant) < DModManager.MAX_DMODS_FROM_COMBAT || isInstalled)
                            && Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= this.refitButton.getDModAddOrRemoveCost(variant, isInstalled));
    }

    public boolean isDModsNotInShip(List<HullModSpecAPI> dMods, ShipVariantAPI variant, HullModSpecAPI dMod) {
        ShipVariantAPI variantClone = variant.clone();
        for (HullModSpecAPI dModSpec : dMods) {
            DModManager.setDHull(variantClone);
            variantClone.removeSuppressedMod(dModSpec.getId());
            variantClone.addPermaMod(dModSpec.getId(), false);
        }

        List<HullModSpecAPI> installableDMods = this.refitButton.getInstallableDMods(variantClone, true);
        return !installableDMods.contains(dMod);
    }
}
