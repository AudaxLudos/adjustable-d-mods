package adjustabledmods.ui;

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
    public final float WIDTH = 800f;
    public final float HEIGHT = 405f;
    public List<ButtonAPI> installableDModButtons = new ArrayList<>();
    public List<ButtonAPI> removableDModButtons = new ArrayList<>();
    public HullModSpecAPI selectedInstallableDMod = null;
    public HullModSpecAPI selectedRemovableDMod = null;
    public ButtonAPI selectedInstallableDModButton;
    public ButtonAPI selectedRemovableDModButton;
    public LabelAPI costToInstallDModText;
    public LabelAPI costToRemoveDModText;

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
        if (market == null) {
            tooltip.addSpacer(10f);
            tooltip.addPara("Must be docked at a market to perform ship restorations", Misc.getNegativeHighlightColor(), 0f);
        }
    }

    @Override
    public boolean hasPanel(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return market != null;
    }

    @Override
    public void initPanel(CustomPanelAPI backgroundPanel, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        this.installableDModButtons.clear();
        this.removableDModButtons.clear();

        CustomPanelAPI mPanel = backgroundPanel.createCustomPanel(WIDTH, HEIGHT, null);
        TooltipMakerAPI mElement = mPanel.createUIElement(WIDTH, HEIGHT, false);

        float columnWidth = WIDTH / 2f - 20f;
        float rowHeight = 300f;

        // installable d-mods ui
        CustomPanelAPI dModsContainerPanel = mPanel.createCustomPanel(WIDTH, rowHeight, null);
        TooltipMakerAPI dModsContainerElement = dModsContainerPanel.createUIElement(WIDTH, rowHeight, false);

        CustomPanelAPI installableDModsContainerPanel = mPanel.createCustomPanel(columnWidth, rowHeight, new BorderedPanelPlugin());
        TooltipMakerAPI installableDModsContainerElement = installableDModsContainerPanel.createUIElement(columnWidth, rowHeight, false);
        installableDModsContainerElement.addSectionHeading("Installable D-Mods", Alignment.MID, 0f);
        installableDModsContainerElement.addSpacer(3f);

        CustomPanelAPI installableDModsPanel = mPanel.createCustomPanel(columnWidth, rowHeight - 25f, null);
        TooltipMakerAPI installableDModsElement = installableDModsPanel.createUIElement(columnWidth, rowHeight - 25f, true);

        List<HullModSpecAPI> installableDMods = getInstallableDMods(variant, true);
        Collections.sort(installableDMods, new Comparator<HullModSpecAPI>() {
            public int compare(HullModSpecAPI o1, HullModSpecAPI o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        if (installableDMods.isEmpty()) {
            installableDModsElement.setParaOrbitronVeryLarge();
            LabelAPI removableDModsText = installableDModsElement.addPara("No d-mods applicable", 0f);
            removableDModsText.getPosition().inTL(
                    columnWidth / 2 - removableDModsText.computeTextWidth(removableDModsText.getText()) / 2,
                    (rowHeight - 25f) / 2 - removableDModsText.computeTextHeight(removableDModsText.getText()) / 2
            );
        } else {
            for (HullModSpecAPI dMod : installableDMods) {
                CustomPanelAPI dModPanel = mPanel.createCustomPanel(columnWidth - 4, 50f, new SelectButtonPlugin(this, variant, false));
                TooltipMakerAPI dModNameElement = dModPanel.createUIElement(columnWidth - 4, 40f, false);
                TooltipMakerAPI dModImage = dModNameElement.beginImageWithText(dMod.getSpriteName(), 40f);
                dModImage.addPara(dMod.getDisplayName(), Misc.getTextColor(), 0f);
                dModNameElement.addImageWithText(0f);
                dModNameElement.getPosition().inTL(-5f, 5f);

                TooltipMakerAPI dModButtonElement = dModPanel.createUIElement(columnWidth - 4, 50f, false);
                ButtonAPI dModButton = dModButtonElement.addButton("", dMod, new Color(0, 195, 255, 190), new Color(0, 0, 0, 255), Alignment.MID, CutStyle.NONE, columnWidth - 4, 50f, 0f);
                dModButton.setHighlightBounceDown(false);
                dModButton.setGlowBrightness(0.4f);
                dModButtonElement.addTooltipTo(new DModTooltip(dMod, variant.getHullSize(), Global.getCombatEngine().createFXDrone(variant)), dModPanel, TooltipMakerAPI.TooltipLocation.RIGHT);

                dModPanel.addUIElement(dModButtonElement).inTL(-10f, 0f);
                dModPanel.addUIElement(dModNameElement);

                installableDModsElement.addCustom(dModPanel, 0f);
                this.installableDModButtons.add(dModButton);
            }
        }
        installableDModsPanel.addUIElement(installableDModsElement).inTL(-5f, 0f);
        installableDModsContainerElement.addCustom(installableDModsPanel, 0f);

        installableDModsContainerPanel.addUIElement(installableDModsContainerElement);
        mElement.addCustom(installableDModsContainerPanel, 0f).getPosition().inTL(10f, 10f);

        // removable d-mods ui
        CustomPanelAPI removableDModsContainerPanel = mPanel.createCustomPanel(columnWidth, rowHeight, new BorderedPanelPlugin());
        TooltipMakerAPI removableDModsContainerElement = removableDModsContainerPanel.createUIElement(columnWidth, rowHeight, false);
        removableDModsContainerElement.addSectionHeading("Removable D-Mods", Alignment.MID, 0f);
        removableDModsContainerElement.addSpacer(3f);

        CustomPanelAPI removableDModsPanel = mPanel.createCustomPanel(columnWidth, rowHeight - 25f, null);
        TooltipMakerAPI removableDModsElement = removableDModsPanel.createUIElement(columnWidth, rowHeight - 25f, true);

        List<HullModSpecAPI> removableDMods = getInstalledDMods(variant);
        Collections.sort(removableDMods, new Comparator<HullModSpecAPI>() {
            public int compare(HullModSpecAPI o1, HullModSpecAPI o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        if (removableDMods.isEmpty()) {
            removableDModsElement.setParaOrbitronVeryLarge();
            LabelAPI removableDModsText = removableDModsElement.addPara("No d-mods found", 0f);
            removableDModsText.getPosition().inTL(
                    columnWidth / 2 - removableDModsText.computeTextWidth(removableDModsText.getText()) / 2,
                    (rowHeight - 25f) / 2 - removableDModsText.computeTextHeight(removableDModsText.getText()) / 2
            );
        } else {
            for (HullModSpecAPI dMod : removableDMods) {
                CustomPanelAPI dModPanel = mPanel.createCustomPanel(columnWidth - 4, 50f, new SelectButtonPlugin(this, variant, true));
                TooltipMakerAPI dModNameElement = dModPanel.createUIElement(columnWidth - 4, 40f, false);
                TooltipMakerAPI dModImage = dModNameElement.beginImageWithText(dMod.getSpriteName(), 40f);
                dModImage.addPara(dMod.getDisplayName(), Misc.getTextColor(), 0f);
                dModNameElement.addImageWithText(0f);
                dModNameElement.getPosition().inTL(-5f, 5f);

                TooltipMakerAPI dModButtonElement = dModPanel.createUIElement(columnWidth - 4, 50f, false);
                ButtonAPI dModButton = dModButtonElement.addButton("", dMod, new Color(0, 195, 255, 190), new Color(0, 0, 0, 255), Alignment.MID, CutStyle.NONE, columnWidth - 4, 50f, 0f);
                dModButton.setHighlightBounceDown(false);
                dModButton.setGlowBrightness(0.4f);
                dModButtonElement.addTooltipTo(new DModTooltip(dMod, variant.getHullSize(), Global.getCombatEngine().createFXDrone(variant)), dModPanel, TooltipMakerAPI.TooltipLocation.RIGHT);

                dModPanel.addUIElement(dModButtonElement).inTL(-10f, 0f);
                dModPanel.addUIElement(dModNameElement);

                removableDModsElement.addCustom(dModPanel, 0f);
                this.removableDModButtons.add(dModButton);
            }
        }
        removableDModsPanel.addUIElement(removableDModsElement).inTL(-5f, 0f);
        removableDModsContainerElement.addCustom(removableDModsPanel, 0f);

        removableDModsContainerPanel.addUIElement(removableDModsContainerElement);
        dModsContainerElement.addCustom(removableDModsContainerPanel, 0f).getPosition().inTR(10f, 10f);

        dModsContainerPanel.addUIElement(dModsContainerElement);
        mElement.addCustom(dModsContainerPanel, 0f).getPosition().inTR(0f, 0f);

        float test = WIDTH / 3f;

        // Footer buttons
        CustomPanelAPI installableDModsFooterPanel = mPanel.createCustomPanel(test, 200f, new ConfirmButtonPlugin(this, variant));
        TooltipMakerAPI installableDModsFooterElement = installableDModsFooterPanel.createUIElement(test, 200f, false);
        installableDModsFooterElement.setParaFontOrbitron();
        installableDModsFooterElement.addSpacer(10f);
        installableDModsFooterElement.addPara("Cost to add", Misc.getBasePlayerColor(), 0f).setAlignment(Alignment.MID);
        installableDModsFooterElement.addSpacer(5f);
        installableDModsFooterElement.setParaInsigniaLarge();
        LabelAPI costToInstallDModText = installableDModsFooterElement.addPara(Misc.getDGSCredits(0), Misc.getHighlightColor(), 0f);
        costToInstallDModText.setAlignment(Alignment.MID);
        this.costToInstallDModText = costToInstallDModText;
        installableDModsFooterElement.addSpacer(10f);
        installableDModsFooterElement.setButtonFontOrbitron20();
        ButtonAPI installDModButton = installableDModsFooterElement.addButton("Add", "install_selected", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180f, 25f, 0f);
        installDModButton.setEnabled(this.selectedInstallableDMod != null);
        this.selectedInstallableDModButton = installDModButton;
        installableDModsFooterElement.addTooltipTo(new AddDModTooltip(this, variant), installDModButton, TooltipMakerAPI.TooltipLocation.RIGHT);
        installDModButton.getPosition().inBMid(0);
        installableDModsFooterPanel.addUIElement(installableDModsFooterElement);
        mElement.addCustom(installableDModsFooterPanel, 10f);

        CustomPanelAPI dModsFooterTextPanel = mPanel.createCustomPanel(test, 200f, null);
        TooltipMakerAPI dModsFooterTextElement = dModsFooterTextPanel.createUIElement(test, 200f, false);
        dModsFooterTextElement.setParaFontOrbitron();
        dModsFooterTextElement.addSpacer(10f);
        dModsFooterTextElement.addPara("Max d-mods allowed", Misc.getBasePlayerColor(), 0f).setAlignment(Alignment.MID);
        dModsFooterTextElement.addSpacer(5f);
        dModsFooterTextElement.setParaInsigniaLarge();
        dModsFooterTextElement.addPara(DModManager.getNumDMods(variant) + " / " + DModManager.MAX_DMODS_FROM_COMBAT, Misc.getHighlightColor(), 0f).setAlignment(Alignment.MID);
        dModsFooterTextElement.addSpacer(10f);
        dModsFooterTextPanel.addUIElement(dModsFooterTextElement);
        mElement.addCustom(dModsFooterTextPanel, 0f).getPosition().rightOfMid(installableDModsFooterPanel, 0f);

        CustomPanelAPI removableDModsFooterPanel = mPanel.createCustomPanel(test, 200f, new ConfirmButtonPlugin(this, variant));
        TooltipMakerAPI removableDModsFooterElement = removableDModsFooterPanel.createUIElement(test, 200f, false);
        removableDModsFooterElement.setParaFontOrbitron();
        removableDModsFooterElement.addSpacer(10f);
        removableDModsFooterElement.addPara("Cost to remove", Misc.getBasePlayerColor(), 0f).setAlignment(Alignment.MID);
        removableDModsFooterElement.addSpacer(5f);
        removableDModsFooterElement.setParaInsigniaLarge();
        LabelAPI costToRemoveDModText = removableDModsFooterElement.addPara(Misc.getDGSCredits(0), Misc.getHighlightColor(), 0f);
        costToRemoveDModText.setAlignment(Alignment.MID);
        this.costToRemoveDModText = costToRemoveDModText;
        removableDModsFooterElement.addSpacer(10f);
        removableDModsFooterElement.setButtonFontOrbitron20();
        ButtonAPI removeDModButton = removableDModsFooterElement.addButton("Remove", "remove_selected", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, 180f, 25f, 0f);
        removeDModButton.setEnabled(this.selectedRemovableDMod != null);
        this.selectedRemovableDModButton = removeDModButton;
        installableDModsFooterElement.addTooltipTo(new RemoveDModTooltip(this, variant), removeDModButton, TooltipMakerAPI.TooltipLocation.RIGHT);
        removeDModButton.getPosition().inBMid(0);
        removableDModsFooterPanel.addUIElement(removableDModsFooterElement);
        mElement.addCustom(removableDModsFooterPanel, 0f).getPosition().rightOfMid(dModsFooterTextPanel, 0f);

        mPanel.addUIElement(mElement).inTR(0f, 0f);
        backgroundPanel.addComponent(mPanel);
    }

    @Override
    public void onPanelClose(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        this.selectedInstallableDMod = null;
        this.selectedRemovableDMod = null;
    }

    public List<HullModSpecAPI> getInstallableDMods(ShipVariantAPI variant, boolean canAddDestroyedMods) {
        List<HullModSpecAPI> installableDMods = DModManager.getModsWithTags(Tags.HULLMOD_DAMAGE);
        if (canAddDestroyedMods)
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_DESTROYED_ALWAYS));

        DModManager.removeUnsuitedMods(variant, installableDMods);

        if (DModManager.getNumDMods(variant, Tags.HULLMOD_DAMAGE_STRUCT) > 0)
            installableDMods = DModManager.getModsWithoutTags(installableDMods, Tags.HULLMOD_DAMAGE_STRUCT);
        if (variant.getHullSpec().getFighterBays() < 0)
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_FIGHTER_BAY_DAMAGE));
        if (variant.getHullSpec().isPhase())
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_DAMAGE_PHASE));
        if (variant.isCarrier())
            installableDMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_CARRIER_ALWAYS));

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

        return installedDMods;
    }

    public float getDModAddOrRemoveCost(ShipVariantAPI variant, boolean isInstalled) {
        int numDMods = DModManager.getNumDMods(variant);
        ShipHullSpecAPI hullSpec = variant.getHullSpec().getDParentHull();
        if (hullSpec == null)
            hullSpec = variant.getHullSpec();

        float baseMultiplier = 0.4f;
        float dModMultiplier = 1.2f;
        float cost = hullSpec.getBaseValue() * baseMultiplier;
        for (int i = 0; i < numDMods; i++) {
            cost *= dModMultiplier;
        }

        float removalMultiplier = 2f;
        if (isInstalled)
            removalMultiplier = 1.5f;

        return cost * removalMultiplier;
    }
}