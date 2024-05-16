package adjustabledmods.ui;

import adjustabledmods.Utils;
import adjustabledmods.ui.plugins.BorderedPanelPlugin;
import adjustabledmods.ui.plugins.ConfirmButtonPlugin;
import adjustabledmods.ui.plugins.SelectButtonPlugin;
import adjustabledmods.ui.tooltips.AddDModTooltip;
import adjustabledmods.ui.tooltips.DModTooltip;
import adjustabledmods.ui.tooltips.RemoveDModTooltip;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import lunalib.lunaRefit.BaseRefitButton;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DModRefitButton extends BaseRefitButton {
    public final float WIDTH = 760f;
    public final float HEIGHT = 425f;
    public List<ButtonAPI> installDModButtons = new ArrayList<>();
    public List<ButtonAPI> removeDModButtons = new ArrayList<>();
    public List<HullModSpecAPI> installSelectedDMods = new ArrayList<>();
    public List<HullModSpecAPI> removeSelectedDMods = new ArrayList<>();
    public ButtonAPI installDModButton;
    public ButtonAPI removeDModButton;
    public LabelAPI installCostText;
    public LabelAPI removeCostText;

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
        if (market == null) {
            tooltip.addPara("Must be docked at a market to perform ship restorations", Misc.getNegativeHighlightColor(), 0f);
            tooltip.addSpacer(10f);
        }
        tooltip.addPara("Options to install or remove d-mods on a ship", 0f);
    }

    @Override
    public boolean hasPanel(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return market != null;
    }

    @Override
    public void initPanel(CustomPanelAPI backgroundPanel, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        this.installDModButtons.clear();
        this.removeDModButtons.clear();

        TooltipMakerAPI mElement = backgroundPanel.createUIElement(WIDTH, HEIGHT, false);
        backgroundPanel.addUIElement(mElement);

        // Installable d-mods ui
        float columnWidth = WIDTH / 2f - 20f;
        float rowHeight = 320f;

        CustomPanelAPI dModsContainerPanel = backgroundPanel.createCustomPanel(WIDTH, rowHeight, null);
        TooltipMakerAPI dModsContainerElement = dModsContainerPanel.createUIElement(WIDTH, rowHeight, false);
        dModsContainerPanel.addUIElement(dModsContainerElement).inMid();
        mElement.addCustom(dModsContainerPanel, 10f);

        CustomPanelAPI installableDModsPanel = backgroundPanel.createCustomPanel(columnWidth, rowHeight, new BorderedPanelPlugin());
        TooltipMakerAPI installableDModsHeader = installableDModsPanel.createUIElement(columnWidth, rowHeight, false);
        installableDModsHeader.addSectionHeading("Installable D-Mods", Alignment.MID, 0f);
        installableDModsPanel.addUIElement(installableDModsHeader);

        TooltipMakerAPI installableDModsElement = installableDModsPanel.createUIElement(columnWidth, rowHeight - 20f, true);
        List<HullModSpecAPI> installableDMods = getInstallableDMods(variant, true);
        if (installableDMods.isEmpty()) {
            installableDModsElement.setParaOrbitronVeryLarge();
            LabelAPI removableDModsText = installableDModsElement.addPara("No d-mods applicable", 0f);
            removableDModsText.getPosition().inTL(columnWidth / 2 - removableDModsText.computeTextWidth(removableDModsText.getText()) / 2, (rowHeight - 25f) / 2 - removableDModsText.computeTextHeight(removableDModsText.getText()) / 2);
        } else {
            for (HullModSpecAPI dMod : installableDMods) {
                CustomPanelAPI dModPanel = addDModButton(backgroundPanel, dMod, variant, !(Utils.isShipAboveDModLimit(variant) || (Utils.isSelectionAboveDModsLimit(installSelectedDMods, variant) && installSelectedDMods.contains(dMod))), false);
                installableDModsElement.addCustom(dModPanel, 0f);
            }
        }
        installableDModsPanel.addUIElement(installableDModsElement);
        dModsContainerElement.addCustom(installableDModsPanel, 0f);

        // Removable d-mods ui
        CustomPanelAPI removableDModsPanel = backgroundPanel.createCustomPanel(columnWidth, rowHeight, new BorderedPanelPlugin());
        TooltipMakerAPI removableDModsHeader = removableDModsPanel.createUIElement(columnWidth, rowHeight, false);
        removableDModsHeader.addSectionHeading("Removable D-Mods", Alignment.MID, 0f);
        removableDModsPanel.addUIElement(removableDModsHeader);

        TooltipMakerAPI removableDModsElement = removableDModsPanel.createUIElement(columnWidth, rowHeight - 20f, true);
        List<HullModSpecAPI> removableDMods = getInstalledDMods(variant);
        if (removableDMods.isEmpty()) {
            removableDModsElement.setParaOrbitronVeryLarge();
            LabelAPI removableDModsText = removableDModsElement.addPara("No d-mods found", 0f);
            removableDModsText.getPosition().inTL(columnWidth / 2 - removableDModsText.computeTextWidth(removableDModsText.getText()) / 2, (rowHeight - 25f) / 2 - removableDModsText.computeTextHeight(removableDModsText.getText()) / 2);
        } else {
            for (HullModSpecAPI dMod : removableDMods) {
                CustomPanelAPI dModPanel = addDModButton(backgroundPanel, dMod, variant, true, true);
                removableDModsElement.addCustom(dModPanel, 0f);
            }
        }
        removableDModsPanel.addUIElement(removableDModsElement);
        dModsContainerElement.addCustom(removableDModsPanel, 0f).getPosition().rightOfTop(installableDModsPanel, 20f);

        // Footer elements
        float footerWidth = WIDTH / 3f;

        CustomPanelAPI installableDModsFooterPanel = backgroundPanel.createCustomPanel(footerWidth, 200f, new ConfirmButtonPlugin(this, variant));
        TooltipMakerAPI installableDModsFooterElement = installableDModsFooterPanel.createUIElement(footerWidth, 200f, false);
        this.installCostText = addCustomLabelledValue(installableDModsFooterElement, "Cost to install", Misc.getDGSCredits(0));
        installableDModsFooterElement.addSpacer(10f);
        this.installDModButton = addCustomFooterButton(installableDModsFooterElement, "Add", "install_selected", !this.installSelectedDMods.isEmpty(), new AddDModTooltip(this, variant));
        installableDModsFooterPanel.addUIElement(installableDModsFooterElement);
        mElement.addCustom(installableDModsFooterPanel, 10f);

        CustomPanelAPI dModsFooterTextPanel = backgroundPanel.createCustomPanel(footerWidth, 200f, null);
        TooltipMakerAPI dModsFooterTextElement = dModsFooterTextPanel.createUIElement(footerWidth, 200f, false);
        addCustomLabelledValue(dModsFooterTextElement, "Max d-mods allowed", DModManager.getNumDMods(variant) + " / " + DModManager.MAX_DMODS_FROM_COMBAT);
        dModsFooterTextElement.addSpacer(15f);
        dModsFooterTextElement.setParaSmallInsignia();
        dModsFooterTextElement.addPara("Credits: %s", 0f, Misc.getTextColor(), Misc.getHighlightColor(), Misc.getDGSCredits(Global.getSector().getPlayerFleet().getCargo().getCredits().get())).setAlignment(Alignment.MID);
        dModsFooterTextPanel.addUIElement(dModsFooterTextElement);
        mElement.addCustom(dModsFooterTextPanel, 0f).getPosition().rightOfMid(installableDModsFooterPanel, 0f);

        CustomPanelAPI removableDModsFooterPanel = backgroundPanel.createCustomPanel(footerWidth, 200f, new ConfirmButtonPlugin(this, variant));
        TooltipMakerAPI removableDModsFooterElement = removableDModsFooterPanel.createUIElement(footerWidth, 200f, false);
        this.removeCostText = addCustomLabelledValue(removableDModsFooterElement, "Cost to remove", Misc.getDGSCredits(0));
        removableDModsFooterElement.addSpacer(10f);
        this.removeDModButton = addCustomFooterButton(removableDModsFooterElement, "Remove", "remove_selected", !this.removeSelectedDMods.isEmpty(), new RemoveDModTooltip(this, variant));
        removableDModsFooterPanel.addUIElement(removableDModsFooterElement);
        mElement.addCustom(removableDModsFooterPanel, 0f).getPosition().rightOfMid(dModsFooterTextPanel, 0f);
    }

    @Override
    public void onPanelClose(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        this.installSelectedDMods.clear();
        this.removeSelectedDMods.clear();
    }

    public List<HullModSpecAPI> getInstallableDMods(ShipVariantAPI variant, boolean canAddDestroyedMods) {
        List<HullModSpecAPI> installableDMods = DModManager.getModsWithTags(Tags.HULLMOD_DAMAGE);
        if (canAddDestroyedMods) {
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_DESTROYED_ALWAYS));
        }

        DModManager.removeUnsuitedMods(variant, installableDMods);

        if (DModManager.getNumDMods(variant, Tags.HULLMOD_DAMAGE_STRUCT) > 0) {
            installableDMods = DModManager.getModsWithoutTags(installableDMods, Tags.HULLMOD_DAMAGE_STRUCT);
        }
        if (variant.getHullSpec().getFighterBays() > 0) {
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_FIGHTER_BAY_DAMAGE));
        }
        if (variant.getHullSpec().isPhase()) {
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_DAMAGE_PHASE));
        }
        if (variant.isCarrier()) {
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_CARRIER_ALWAYS));
        }

        DModManager.removeModsAlreadyInVariant(variant, installableDMods);

        return installableDMods;
    }

    public List<HullModSpecAPI> getInstalledDMods(ShipVariantAPI variant) {
        List<HullModSpecAPI> installedDMods = new ArrayList<>();
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
                installedDMods.add(DModManager.getMod(hullmod));
            }
        }

        Collections.sort(installedDMods, new Comparator<HullModSpecAPI>() {
            public int compare(HullModSpecAPI o1, HullModSpecAPI o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        return installedDMods;
    }

    public LabelAPI addCustomLabelledValue(TooltipMakerAPI tooltip, String label, String value) {
        tooltip.setParaFontOrbitron();
        tooltip.addPara(label, Misc.getBasePlayerColor(), 0f).setAlignment(Alignment.MID);
        tooltip.addSpacer(5f);
        tooltip.setParaInsigniaLarge();
        LabelAPI text = tooltip.addPara(value, Misc.getHighlightColor(), 0f);
        text.setAlignment(Alignment.MID);
        return text;
    }

    public CustomPanelAPI addDModButton(CustomPanelAPI panel, HullModSpecAPI dMod, ShipVariantAPI variant, Boolean isEnabled, Boolean isInstalled) {
        float columnWidth = WIDTH / 2f - 20f;

        CustomPanelAPI dModPanel = panel.createCustomPanel(columnWidth, 44f, new SelectButtonPlugin(this, variant, isInstalled));
        TooltipMakerAPI dModButtonElement = dModPanel.createUIElement(columnWidth, 44f, false);
        ButtonAPI dModButton = dModButtonElement.addButton("", dMod, new Color(0, 195, 255, 190), new Color(0, 0, 0, 255), Alignment.MID, CutStyle.NONE, columnWidth, 44f, 0f);
        Utils.setButtonEnabledOrHighlighted(dModButton, isEnabled, !isEnabled);
        dModButtonElement.addTooltipTo(new DModTooltip(dMod, variant.getHullSize(), Global.getCombatEngine().createFXDrone(variant)), dModButton, TooltipMakerAPI.TooltipLocation.RIGHT);
        dModButtonElement.getPosition().setXAlignOffset(-10f);
        dModPanel.addUIElement(dModButtonElement);

        TooltipMakerAPI dModNameElement = dModPanel.createUIElement(columnWidth, 40f, false);
        TooltipMakerAPI dModImage = dModNameElement.beginImageWithText(dMod.getSpriteName(), 40f);
        dModImage.addPara(dMod.getDisplayName(), Misc.getTextColor(), 0f);
        dModNameElement.addImageWithText(0f);
        dModNameElement.getPosition().setXAlignOffset(-8f).setYAlignOffset(2f);
        dModPanel.addUIElement(dModNameElement);

        if (!isInstalled) {
            this.installDModButtons.add(dModButton);
        } else {
            this.removeDModButtons.add(dModButton);
        }
        return dModPanel;
    }

    public ButtonAPI addCustomFooterButton(TooltipMakerAPI tooltip, String label, Object data, Boolean isEnabled, TooltipMakerAPI.TooltipCreator tipBox) {
        tooltip.setButtonFontOrbitron20();
        ButtonAPI button = tooltip.addButton(label, data, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180f, 25f, 0f);
        button.setEnabled(isEnabled);
        tooltip.addTooltipTo(tipBox, button, TooltipMakerAPI.TooltipLocation.RIGHT);
        button.getPosition().inBMid(0);
        return button;
    }

    public float getDModAddOrRemoveCost(ShipVariantAPI variant, boolean isInstalled, float offset) {
        List<HullModSpecAPI> selectedDMods = isInstalled ? this.removeSelectedDMods : this.installSelectedDMods;
        ShipHullSpecAPI hullSpec = variant.getHullSpec().getDParentHull();
        if (hullSpec == null) {
            hullSpec = variant.getHullSpec();
        }

        float maxCost = (float) (hullSpec.getBaseValue() * 1.2f * Math.pow(1.2d, DModManager.MAX_DMODS_FROM_COMBAT));
        float cost = maxCost * ((selectedDMods.size() + offset) / DModManager.MAX_DMODS_FROM_COMBAT);

        float installMult = isInstalled ? 1f : 1.5f;

        if (offset < 0) {
            cost = 0;
        }

        return (float) Math.ceil(cost * installMult);
    }
}
