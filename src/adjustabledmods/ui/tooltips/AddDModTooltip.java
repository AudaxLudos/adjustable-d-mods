package adjustabledmods.ui.tooltips;

import adjustabledmods.ui.DModRefitButton;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class AddDModTooltip extends BaseTooltipCreator {
    public DModRefitButton refitButton;
    public ShipVariantAPI variant;

    public AddDModTooltip(DModRefitButton refitButton, ShipVariantAPI variant) {
        this.refitButton = refitButton;
        this.variant = variant;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 380f;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        if (this.refitButton.installSelectedDMods.isEmpty() && DModManager.getNumDMods(variant) < DModManager.MAX_DMODS_FROM_COMBAT) {
            tooltip.addPara("Select an installable d-mod", Misc.getHighlightColor(), 0f);
            tooltip.addSpacer(10f);
        }
        if (!this.refitButton.installSelectedDMods.isEmpty() && Global.getSector().getPlayerFleet().getCargo().getCredits().get() <= this.refitButton.getDModAddOrRemoveCost(variant, false, 0f)) {
            tooltip.addPara("Not enough credits", Misc.getNegativeHighlightColor(), 0f);
            tooltip.addSpacer(10f);
        }
        if (DModManager.getNumDMods(variant) >= DModManager.MAX_DMODS_FROM_COMBAT) {
            tooltip.addPara("Maximum amount of d-mods reached", Misc.getNegativeHighlightColor(), 0f);
            tooltip.addSpacer(10f);
        }
        tooltip.addPara("Installing d-mods costs credits and will be more expensive the more d-mods the ship has.", 0f);
    }
}
