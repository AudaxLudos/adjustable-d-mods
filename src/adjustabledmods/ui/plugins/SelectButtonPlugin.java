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
        if (!(buttonId instanceof HullModSpecAPI)) {
            return;
        }

        LabelAPI costText = !isInstalled ? this.refitButton.installCostText : this.refitButton.removeCostText;
        List<ButtonAPI> dModButtons = !isInstalled ? this.refitButton.installDModButtons : this.refitButton.removeDModButtons;
        List<HullModSpecAPI> selectedDMods = !isInstalled ? this.refitButton.installSelectedDMods : this.refitButton.removeSelectedDMods;
        ButtonAPI confirmButton = !isInstalled ? this.refitButton.installDModButton : this.refitButton.removeDModButton;

        if (!isInstalled && !selectedDMods.contains((HullModSpecAPI) buttonId) &&
                (Utils.isShipAboveDModLimit(variant) || Utils.isSelectionAboveDModsLimit(selectedDMods, variant) ||
                        isDModsNotInShip(selectedDMods, variant, (HullModSpecAPI) buttonId))) {
            return;
        }

        if (!selectedDMods.remove((HullModSpecAPI) buttonId)) {
            selectedDMods.add((HullModSpecAPI) buttonId);
        }

        for (ButtonAPI button : dModButtons) {
            HullModSpecAPI buttonCustomData = (HullModSpecAPI) button.getCustomData();
            boolean isSelected = selectedDMods.contains(buttonCustomData);
            boolean isEnabled = !(!isInstalled && !isSelected &&
                    (Utils.isShipAboveDModLimit(variant) || Utils.isSelectionAboveDModsLimit(selectedDMods, variant) ||
                            isDModsNotInShip(selectedDMods, variant, buttonCustomData)));
            Utils.setButtonEnabledOrHighlighted(button, isEnabled, isSelected || !isEnabled);
        }

        costText.setColor(Utils.canPlayerAffordCost(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled, 0f)) ? Misc.getHighlightColor() : Misc.getNegativeHighlightColor());
        costText.setText(Misc.getDGSCredits(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled, 0f)));
        confirmButton.setEnabled((!selectedDMods.isEmpty()) && (!Utils.isShipAboveDModLimit(variant) || isInstalled) && Utils.canPlayerAffordCost(this.refitButton.getDModAddOrRemoveCost(variant, isInstalled, 0f)));
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
