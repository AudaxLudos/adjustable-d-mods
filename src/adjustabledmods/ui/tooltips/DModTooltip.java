package adjustabledmods.ui.tooltips;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.Objects;

public class DModTooltip extends BaseTooltipCreator {
    public HullModSpecAPI spec;
    public ShipAPI.HullSize hullSize;
    public ShipAPI ship;

    public DModTooltip(HullModSpecAPI spec, ShipAPI.HullSize hullSize, ShipAPI ship) {
        this.spec = spec;
        this.hullSize = hullSize;
        this.ship = ship;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 380f;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addPara(this.spec.getDisplayName(), Misc.getBasePlayerColor(), 0f);
        tooltip.addSpacer(10f);
        if (!Objects.equals(this.spec.getManufacturer(), "Common")) {
            tooltip.addPara(
                    "Design Type: %s",
                    0f,
                    Misc.getGrayColor(),
                    Misc.getDesignTypeColor(this.spec.getManufacturer()),
                    this.spec.getManufacturer()
            );
            tooltip.addSpacer(10f);
        }

        tooltip.addPara(
                this.spec.getDescriptionFormat(),
                0f,
                Misc.getTextColor(),
                Misc.getHighlightColor(),
                this.spec.getEffect().getDescriptionParam(0, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(1, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(2, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(3, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(4, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(5, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(6, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(7, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(8, this.hullSize, this.ship),
                this.spec.getEffect().getDescriptionParam(9, this.hullSize, this.ship)
        );

        this.spec.getEffect().addPostDescriptionSection(
                tooltip,
                this.hullSize,
                null,
                getTooltipWidth(tooltip),
                false
        );
    }
}
