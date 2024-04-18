package adjustabledmods.ui;

import adjustabledmods.ui.plugins.ConfirmPlugin;
import adjustabledmods.ui.plugins.SelectInstallablePlugin;
import adjustabledmods.ui.plugins.SelectRemovablePlugin;
import adjustabledmods.ui.tooltips.DModTooltip;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import lunalib.lunaRefit.BaseRefitButton;

import java.util.*;

public class RefitButtonUI extends BaseRefitButton {
    public final float WIDTH = 800f;
    public final float HEIGHT = 400f;
    public List<ButtonAPI> installableDModButtons = new ArrayList<>();
    public List<ButtonAPI> removableDModButtons = new ArrayList<>();
    public HullModSpecAPI selectedInstallableDMod = null;
    public HullModSpecAPI selectedRemovableDMod = null;
    public ButtonAPI selectedInstallableDModButton;
    public ButtonAPI selectedRemovableDModButton;

    @Override
    public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
        return "Adjust D-Mods";
    }

    @Override
    public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
        return "graphics/icons/skills/derelict_contingent.png";
    }

    @Override
    public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
        return 9999;
    }

    @Override
    public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return true;
    }

    @Override
    public float getPanelWidth(FleetMemberAPI member, ShipVariantAPI variant) {
        return WIDTH;
    }

    @Override
    public float getPanelHeight(FleetMemberAPI member, ShipVariantAPI variant) {
        return HEIGHT;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        tooltip.addPara("Options to install or remove d-mods on a ship", 0f);
    }

    @Override
    public boolean hasPanel(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return true;
    }

    @Override
    public void initPanel(CustomPanelAPI backgroundPanel, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        this.installableDModButtons.clear();
        this.removableDModButtons.clear();

        CustomPanelAPI mPanel = backgroundPanel.createCustomPanel(WIDTH, HEIGHT, null);
        backgroundPanel.addComponent(mPanel);

        TooltipMakerAPI mElement = mPanel.createUIElement(WIDTH, HEIGHT, false);
        mPanel.addUIElement(mElement);

        float columnWidth = WIDTH / 2f - 20f;

        // installable d-mods ui
        CustomPanelAPI installableDModsHeaderPanel = mPanel.createCustomPanel(columnWidth, 25f, null);
        TooltipMakerAPI installableDModsHeaderElement = installableDModsHeaderPanel.createUIElement(columnWidth, 25f, false);
        installableDModsHeaderElement.addSectionHeading("Installable D-Mods", Alignment.MID, 0f);
        installableDModsHeaderPanel.addUIElement(installableDModsHeaderElement);
        mElement.addCustom(installableDModsHeaderPanel, 0f).getPosition().inTL(10f, 10f);

        CustomPanelAPI installableDModsPanel = mPanel.createCustomPanel(columnWidth, 305f, null);
        TooltipMakerAPI installableDModsElement = installableDModsPanel.createUIElement(columnWidth, 305f, true);

        List<HullModSpecAPI> installableDMods = DModManager.getModsWithTags(Tags.HULLMOD_DAMAGE);
        installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_DESTROYED_ALWAYS));
        if (DModManager.getNumDMods(variant, Tags.HULLMOD_DAMAGE_STRUCT) > 0)
            installableDMods = DModManager.getModsWithoutTags(installableDMods, Tags.HULLMOD_DAMAGE_STRUCT);
        if (variant.getHullSpec().getFighterBays() < 0)
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_FIGHTER_BAY_DAMAGE));
        if (variant.getHullSpec().isPhase())
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_DAMAGE_PHASE));
        if (variant.isCarrier())
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_CARRIER_ALWAYS));
        for (Iterator<HullModSpecAPI> itr = installableDMods.iterator(); itr.hasNext(); ) {
            HullModSpecAPI spec = itr.next();
            if (variant.getHullMods().contains(spec.getId())) {
                itr.remove();
            }
        }
        Collections.sort(installableDMods, new Comparator<HullModSpecAPI>() {
            public int compare(HullModSpecAPI o1, HullModSpecAPI o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        for (HullModSpecAPI dMod : installableDMods) {
            CustomPanelAPI dModPanel = mPanel.createCustomPanel(columnWidth, 50f, new SelectInstallablePlugin(this, variant));
            TooltipMakerAPI dModNameElement = dModPanel.createUIElement(columnWidth, 40f, false);
            TooltipMakerAPI dModImage = dModNameElement.beginImageWithText(dMod.getSpriteName(), 40f);
            dModImage.addPara(dMod.getDisplayName(), Misc.getTextColor(), 0f);
            dModNameElement.addImageWithText(0f);
            dModNameElement.getPosition().inTL(-5f, 5f);

            TooltipMakerAPI dModButtonElement = dModPanel.createUIElement(columnWidth, 50f, false);
            ButtonAPI dModCheckBox = dModButtonElement.addAreaCheckbox("", dMod, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), columnWidth, 50f, 0f);
            dModButtonElement.addTooltipTo(new DModTooltip(dMod, variant.getHullSize(), Global.getCombatEngine().createFXDrone(variant)), dModPanel, TooltipMakerAPI.TooltipLocation.RIGHT);

            dModPanel.addUIElement(dModButtonElement).inTL(-10f, 0f);
            dModPanel.addUIElement(dModNameElement);

            installableDModsElement.addCustom(dModPanel, 0f);
            this.installableDModButtons.add(dModCheckBox);
        }
        installableDModsPanel.addUIElement(installableDModsElement);
        mElement.addCustom(installableDModsPanel, 0f).getPosition().belowMid(installableDModsHeaderPanel, 0f);

        // removable d-mods ui
        CustomPanelAPI removableDModsHeaderPanel = mPanel.createCustomPanel(columnWidth, 25f, null);
        TooltipMakerAPI removableDModsHeaderElement = removableDModsHeaderPanel.createUIElement(columnWidth, 25f, false);
        removableDModsHeaderElement.addSectionHeading("Removable D-Mods", Alignment.MID, 0f);
        removableDModsHeaderPanel.addUIElement(removableDModsHeaderElement);
        mElement.addCustom(removableDModsHeaderPanel, 0f).getPosition().inTR(10f, 10f);

        CustomPanelAPI removableDModsPanel = mPanel.createCustomPanel(columnWidth, 305f, null);
        TooltipMakerAPI removableDModsElement = removableDModsPanel.createUIElement(columnWidth, 305f, true);

        List<HullModSpecAPI> removableDMods = new ArrayList<>();
        for (String hullmod : variant.getHullMods()) {
            if (DModManager.getMod(hullmod) == null) {
                continue;
            }
            if (DModManager.getMod(hullmod).hasTag(Tags.HULLMOD_DAMAGE) ||
                    DModManager.getMod(hullmod).hasTag(Tags.HULLMOD_DESTROYED_ALWAYS) ||
                    DModManager.getMod(hullmod).hasTag(Tags.HULLMOD_DAMAGE_STRUCT) ||
                    DModManager.getMod(hullmod).hasTag(Tags.HULLMOD_FIGHTER_BAY_DAMAGE) ||
                    DModManager.getMod(hullmod).hasTag(Tags.HULLMOD_DAMAGE_PHASE) ||
                    DModManager.getMod(hullmod).hasTag(Tags.HULLMOD_CARRIER_ALWAYS)) {
                removableDMods.add(DModManager.getMod(hullmod));
            }
        }
        Collections.sort(removableDMods, new Comparator<HullModSpecAPI>() {
            public int compare(HullModSpecAPI o1, HullModSpecAPI o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        for (HullModSpecAPI dMod : removableDMods) {
            CustomPanelAPI dModPanel = mPanel.createCustomPanel(columnWidth, 50f, new SelectRemovablePlugin(this, variant));
            TooltipMakerAPI dModNameElement = dModPanel.createUIElement(columnWidth, 40f, false);
            TooltipMakerAPI dModImage = dModNameElement.beginImageWithText(dMod.getSpriteName(), 40f);
            dModImage.addPara(dMod.getDisplayName(), Misc.getTextColor(), 0f);
            dModNameElement.addImageWithText(0f);
            dModNameElement.getPosition().inTL(-5f, 5f);

            TooltipMakerAPI dModButtonElement = dModPanel.createUIElement(columnWidth, 50f, false);
            ButtonAPI dModCheckBox = dModButtonElement.addAreaCheckbox("", dMod, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), columnWidth, 50f, 0f);
            dModButtonElement.addTooltipTo(new DModTooltip(dMod, variant.getHullSize(), Global.getCombatEngine().createFXDrone(variant)), dModPanel, TooltipMakerAPI.TooltipLocation.RIGHT);

            dModPanel.addUIElement(dModButtonElement).inTL(-10f, 0f);
            dModPanel.addUIElement(dModNameElement);

            removableDModsElement.addCustom(dModPanel, 0f);
            this.removableDModButtons.add(dModCheckBox);
        }
        removableDModsPanel.addUIElement(removableDModsElement);
        mElement.addCustom(removableDModsPanel, 0f).getPosition().belowMid(removableDModsHeaderPanel, 0f);

        // Footer buttons
        CustomPanelAPI footerPanel = mPanel.createCustomPanel(WIDTH, 200f, new ConfirmPlugin(this, variant));
        TooltipMakerAPI footerElement = footerPanel.createUIElement(WIDTH, 200f, false);
        footerElement.setButtonFontOrbitron20();
        ButtonAPI installDModButton = footerElement.addButton("Install D-Mod", "install", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), 180f, 25f, 0f);
        installDModButton.setEnabled(this.selectedInstallableDMod != null);
        this.selectedInstallableDModButton = installDModButton;
        installDModButton.getPosition().inLMid(0f);
        ButtonAPI removeDModButton = footerElement.addButton("Remove D-Mod", "remove", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), 180f, 25f, 0f);
        removeDModButton.setEnabled(this.selectedRemovableDMod != null);
        this.selectedRemovableDModButton = removeDModButton;
        removeDModButton.getPosition().inRMid(0f);
        footerPanel.addUIElement(footerElement);
        mElement.addCustom(footerPanel, 0f).getPosition().belowLeft(installableDModsPanel, 0f);
    }

    @Override
    public void onPanelClose(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        this.selectedInstallableDMod = null;
        this.selectedRemovableDMod = null;
    }

    @Override
    public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return super.shouldShow(member, variant, market);
    }
}
