package adjustabledmods.ui.plugins;

import adjustabledmods.ui.RefitButtonUI;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.ButtonAPI;

public class SelectRemovablePlugin extends BaseCustomUIPanelPlugin {
    public RefitButtonUI refitButton;
    public ShipVariantAPI variant;

    public SelectRemovablePlugin(RefitButtonUI refitButton, ShipVariantAPI variant) {
        this.refitButton = refitButton;
        this.variant = variant;
    }

    @Override
    public void buttonPressed(Object buttonId) {
        if (buttonId instanceof HullModSpecAPI) {
            this.refitButton.selectedRemovableDMod = (HullModSpecAPI) buttonId;
        }

        boolean anyChecked = false;
        for (ButtonAPI button : this.refitButton.removableDModButtons) {
            if (button.isChecked() && button.getCustomData() != buttonId) {
                button.setChecked(false);
            }
            if (button.isChecked()) {
                anyChecked = true;
            }
        }

        if (!anyChecked) {
            this.refitButton.selectedRemovableDMod = null;
        }

        this.refitButton.selectedRemovableDModButton.setEnabled(this.refitButton.selectedRemovableDMod != null);
    }
}
