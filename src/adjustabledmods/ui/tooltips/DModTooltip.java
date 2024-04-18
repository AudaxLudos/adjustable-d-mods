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
        tooltip.addPara(spec.getDisplayName(), Misc.getBasePlayerColor(), 0f);
        tooltip.addSpacer(10f);
        if (!Objects.equals(spec.getManufacturer(), "Common")) {
            tooltip.addPara(
                    "Design Type: %s",
                    0f,
                    Misc.getGrayColor(),
                    Misc.getDesignTypeColor(spec.getManufacturer()),
                    spec.getManufacturer()
            );
            tooltip.addSpacer(10f);
        }

        tooltip.addPara(
                spec.getDescriptionFormat(),
                0f,
                Misc.getTextColor(),
                Misc.getHighlightColor(),
                spec.getEffect().getDescriptionParam(0, hullSize, ship),
                spec.getEffect().getDescriptionParam(1, hullSize, ship),
                spec.getEffect().getDescriptionParam(2, hullSize, ship),
                spec.getEffect().getDescriptionParam(3, hullSize, ship),
                spec.getEffect().getDescriptionParam(4, hullSize, ship),
                spec.getEffect().getDescriptionParam(5, hullSize, ship),
                spec.getEffect().getDescriptionParam(6, hullSize, ship),
                spec.getEffect().getDescriptionParam(7, hullSize, ship),
                spec.getEffect().getDescriptionParam(8, hullSize, ship),
                spec.getEffect().getDescriptionParam(9, hullSize, ship)
        );

        spec.getEffect().addPostDescriptionSection(
                tooltip,
                hullSize,
                null,
                getTooltipWidth(tooltip),
                false
        );
    }
}
