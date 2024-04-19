package adjustabledmods.ui;

import adjustabledmods.ui.plugins.BorderedPanelPlugin;
import adjustabledmods.ui.plugins.ConfirmButtonPlugin;
import adjustabledmods.ui.plugins.SelectButtonPlugin;
import adjustabledmods.ui.tooltips.DModTooltip;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
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
        float rowHeight = 300f;

        // installable d-mods ui
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

        for (HullModSpecAPI dMod : installableDMods) {
            CustomPanelAPI dModPanel = mPanel.createCustomPanel(columnWidth - 4, 50f, new SelectButtonPlugin(this, variant, false));
            TooltipMakerAPI dModNameElement = dModPanel.createUIElement(columnWidth - 4, 40f, false);
            TooltipMakerAPI dModImage = dModNameElement.beginImageWithText(dMod.getSpriteName(), 40f);
            dModImage.addPara(dMod.getDisplayName(), Misc.getTextColor(), 0f);
            dModNameElement.addImageWithText(0f);
            dModNameElement.getPosition().inTL(-5f, 5f);

            TooltipMakerAPI dModButtonElement = dModPanel.createUIElement(columnWidth - 4, 50f, false);
            ButtonAPI dModButton = dModButtonElement.addButton("", dMod, new Color(0,195,255, 190), new Color(0,0,0,255), Alignment.MID, CutStyle.NONE, columnWidth - 4, 50f, 0f);
            dModButton.setHighlightBounceDown(false);
            dModButton.setGlowBrightness(0.4f);
            dModButtonElement.addTooltipTo(new DModTooltip(dMod, variant.getHullSize(), Global.getCombatEngine().createFXDrone(variant)), dModPanel, TooltipMakerAPI.TooltipLocation.RIGHT);

            dModPanel.addUIElement(dModButtonElement).inTL(-10f, 0f);
            dModPanel.addUIElement(dModNameElement);

            installableDModsElement.addCustom(dModPanel, 0f);
            this.installableDModButtons.add(dModButton);
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
            LabelAPI removableDModsText = removableDModsElement.addPara("No D-Mods built-in", 0f);
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
        removableDModsPanel.addUIElement(removableDModsElement).inTL(-5f, 0f);;
        removableDModsContainerElement.addCustom(removableDModsPanel, 0f);

        removableDModsContainerPanel.addUIElement(removableDModsContainerElement);
        mElement.addCustom(removableDModsContainerPanel, 0f).getPosition().inTR(10f, 10f);

//        // Footer buttons
        CustomPanelAPI installableDModsFooterPanel = mPanel.createCustomPanel(columnWidth, 200f, new ConfirmButtonPlugin(this, variant));
        TooltipMakerAPI installableDModsFooterElement = installableDModsFooterPanel.createUIElement(columnWidth, 200f, false);
        installableDModsFooterElement.setButtonFontOrbitron20();
        ButtonAPI installDModButton = installableDModsFooterElement.addButton("Install D-Mod", "install_selected", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), 180f, 25f, 0f);
        installDModButton.setEnabled(this.selectedInstallableDMod != null);
        this.selectedInstallableDModButton = installDModButton;
        ButtonAPI addRandomDModButton = installableDModsFooterElement.addButton("Add Random D-Mod", "add_random", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), 180f, 25f, 0f);
        addRandomDModButton.getPosition().rightOfMid(installDModButton, 0f);
        installableDModsFooterPanel.addUIElement(installableDModsFooterElement);
        mElement.addCustom(installableDModsFooterPanel, 0f).getPosition().belowMid(installableDModsContainerPanel, 0f);

        CustomPanelAPI removableDModsFooterPanel = mPanel.createCustomPanel(columnWidth, 200f, new ConfirmButtonPlugin(this, variant));
        TooltipMakerAPI removableDModsFooterElement = removableDModsFooterPanel.createUIElement(columnWidth, 200f, false);
        removableDModsFooterElement.setButtonFontOrbitron20();
        ButtonAPI removeDModButton = removableDModsFooterElement.addButton("Remove D-Mod", "remove_selected", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), 180f, 25f, 0f);
        removeDModButton.setEnabled(this.selectedRemovableDMod != null);
        this.selectedRemovableDModButton = removeDModButton;
        ButtonAPI removeAllDModsButton = removableDModsFooterElement.addButton("Remove All D-Mods", "remove_all", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), 180f, 25f, 0f);
        removeAllDModsButton.getPosition().rightOfMid(removeDModButton, 0f);
        removableDModsFooterPanel.addUIElement(removableDModsFooterElement);
        mElement.addCustom(removableDModsFooterPanel, 0f).getPosition().belowMid(removableDModsContainerPanel, 0f);
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
}
