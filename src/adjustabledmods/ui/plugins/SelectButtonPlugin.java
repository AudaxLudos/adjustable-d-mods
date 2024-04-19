package adjustabledmods.ui.plugins;

import adjustabledmods.ui.RefitButtonUI;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.ButtonAPI;

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
        if (buttonId instanceof HullModSpecAPI) {
            if (!isInstalled)
                this.refitButton.selectedInstallableDMod = (HullModSpecAPI) buttonId;
            else
                this.refitButton.selectedRemovableDMod = (HullModSpecAPI) buttonId;
        }

        boolean anyChecked = false;
        if (!isInstalled) {
            for (ButtonAPI button : this.refitButton.installableDModButtons) {
                if (button.getCustomData() == buttonId && !button.isHighlighted()) {
                    button.highlight();
                    anyChecked = true;
                    continue;
                }
                if (button.isHighlighted()) {
                    button.unhighlight();
                }
            }

            System.out.println(anyChecked);

            if (!anyChecked) {
                this.refitButton.selectedInstallableDMod = null;
            }

            if (this.refitButton.selectedInstallableDModButton != null)
                this.refitButton.selectedInstallableDModButton.setEnabled(this.refitButton.selectedInstallableDMod != null);
        } else {
            for (ButtonAPI button : this.refitButton.removableDModButtons) {
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
                this.refitButton.selectedRemovableDMod = null;
            }

            if (this.refitButton.selectedRemovableDModButton != null)
                this.refitButton.selectedRemovableDModButton.setEnabled(this.refitButton.selectedRemovableDMod != null);
        }
    }
}
