package adjustabledmods.ui.plugins;

import adjustabledmods.Utils;
import adjustabledmods.ui.DModRefitButton;
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

        LabelAPI costDModText = !isInstalled ? this.refitButton.costToInstallDModText : this.refitButton.costToRemoveDModText;
        List<ButtonAPI> dModButtons = !isInstalled ? this.refitButton.installableDModButtons : this.refitButton.removableDModButtons;
        List<HullModSpecAPI> selectedDMods = !isInstalled ? this.refitButton.selectedInstallableDMods : this.refitButton.selectedRemovableDMods;
        ButtonAPI selectedHullModButton = !isInstalled ? this.refitButton.selectedInstallableDModButton : this.refitButton.selectedRemovableDModButton;

        if (!isInstalled && !selectedDMods.contains((HullModSpecAPI) buttonId) &&
                (Utils.isShipAboveDModLimit(variant) || Utils.isSelectionAboveDModsLimit(selectedDMods, variant) ||
                        isDModsNotInShip(selectedDMods, variant, (HullModSpecAPI) buttonId))) {
            return;
        }

        if (!selectedDMods.remove((HullModSpecAPI) buttonId)) {
            selectedDMods.add((HullModSpecAPI) buttonId);
        }

        costDModText.setColor(Utils.canPlayerAffordCost(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled, 0f)) ? Misc.getHighlightColor() : Misc.getNegativeHighlightColor());
        costDModText.setText(Misc.getDGSCredits(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled, 0f)));

        for (ButtonAPI button : dModButtons) {
            boolean isEnabled = true;
            boolean isHighlighted = selectedDMods.contains((HullModSpecAPI) button.getCustomData());

            if (!isInstalled && !selectedDMods.contains((HullModSpecAPI) button.getCustomData()) &&
                    (Utils.isShipAboveDModLimit(variant) || Utils.isSelectionAboveDModsLimit(selectedDMods, variant) ||
                            isDModsNotInShip(selectedDMods, variant, (HullModSpecAPI) button.getCustomData()))) {
                isEnabled = false;
                isHighlighted = true;
            }

            Utils.setButtonEnabledOrHighlighted(button, isEnabled, isHighlighted);
        }

        if (selectedHullModButton != null)
            selectedHullModButton.setEnabled((!selectedDMods.isEmpty()) && (!Utils.isShipAboveDModLimit(variant) || isInstalled)
                    && Utils.canPlayerAffordCost(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled, 0f)));
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
