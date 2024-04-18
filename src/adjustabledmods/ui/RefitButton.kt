package adjustabledmods.ui

import adjustabledmods.ui.tooltips.CustomHullmodToolTip
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.DModManager
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaExtensions.addLunaSpriteElement
import lunalib.lunaRefit.BaseRefitButton
import lunalib.lunaUI.elements.LunaSpriteElement

class RefitButton : BaseRefitButton() {
    private var backgroundPanel: CustomPanelAPI? = null
    private var mainPanel: CustomPanelAPI? = null
    private var selectedDMod: HullModSpecAPI? = null

    override fun getButtonName(member: FleetMemberAPI?, variant: ShipVariantAPI?): String {
        return "Adjust D-Mods"
    }

    override fun getIconName(member: FleetMemberAPI?, variant: ShipVariantAPI?): String {
        return "graphics/icons/skills/technology.png"
    }

    override fun getOrder(member: FleetMemberAPI?, variant: ShipVariantAPI?): Int {
        return 9999
    }

    override fun hasTooltip(member: FleetMemberAPI?, variant: ShipVariantAPI?, market: MarketAPI?): Boolean {
        return true
    }

    override fun getPanelWidth(member: FleetMemberAPI?, variant: ShipVariantAPI?): Float {
        return 800f
    }

    override fun getPanelHeight(member: FleetMemberAPI?, variant: ShipVariantAPI?): Float {
        return 400f
    }

    override fun addTooltip(
        tooltip: TooltipMakerAPI,
        member: FleetMemberAPI?,
        variant: ShipVariantAPI?,
        market: MarketAPI?
    ) {
        tooltip.addPara("Options to install or remove d-mods on a ship", 0f)
    }

    override fun hasPanel(member: FleetMemberAPI?, variant: ShipVariantAPI?, market: MarketAPI?): Boolean {
        return true
    }

    override fun initPanel(
        backgroundPanel: CustomPanelAPI?,
        member: FleetMemberAPI?,
        variant: ShipVariantAPI?,
        market: MarketAPI?
    ) {
        this.backgroundPanel = backgroundPanel
        refreshPanel(member, variant)
    }

    private fun refreshPanel(member: FleetMemberAPI?, variant: ShipVariantAPI?) {
        if (backgroundPanel == null) return
        if (mainPanel != null) backgroundPanel!!.removeComponent(mainPanel)

        val width = getPanelWidth(member!!, variant!!)
        val height = getPanelHeight(member, variant)

        mainPanel = backgroundPanel!!.createCustomPanel(width, height, null)
        backgroundPanel!!.addComponent(mainPanel)
        mainPanel!!.position.inTL(0f, 0f)

        val mainElement = mainPanel!!.createUIElement(width, height, false)
        mainPanel!!.addUIElement(mainElement).inTL(-5f, 0f);

        val headerPanel = mainPanel!!.createCustomPanel(305f, 25f, null)
        val headerElement = headerPanel.createUIElement(305f, 25f, false)
        headerElement.addSectionHeading("Installable D-Mods", Alignment.MID, 0f)
        headerPanel.addUIElement(headerElement)
        mainElement.addCustom(headerPanel, 0f)

        val dModsPanel = mainPanel!!.createCustomPanel(305f, 305f, null)
        val dModsElement = dModsPanel.createUIElement(305f, 305f, true)

        var dMods = DModManager.getModsWithTags(Tags.HULLMOD_DAMAGE)
        dMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_DESTROYED_ALWAYS))
        if (DModManager.getNumDMods(variant, Tags.HULLMOD_DAMAGE_STRUCT) > 0)
            dMods = DModManager.getModsWithoutTags(dMods, Tags.HULLMOD_DAMAGE_STRUCT);
        if (variant.hullSpec.fighterBays < 0)
            dMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_FIGHTER_BAY_DAMAGE))
        if (variant.hullSpec.isPhase)
            dMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_DAMAGE_PHASE))
        if (variant.isCarrier)
            dMods.addAll(DModManager.getModsWithTags(Tags.HULLMOD_CARRIER_ALWAYS))
        dMods = dMods.sortedBy { it.displayName }

        for (dMod in dMods) {
            dModsElement.addLunaElement(305f, 40f).apply {
                renderBorder = false
                renderBackground = true
                enableTransparency = true
                backgroundAlpha = 0.3f

                val sprite = innerElement.addLunaSpriteElement(
                    dMod.spriteName,
                    LunaSpriteElement.ScalingTypes.STRETCH_SPRITE,
                    36f,
                    36f
                ).apply {
                    renderBorder = false
                    enableTransparency = true

                    borderAlpha = 0.8f
                    getSprite().alphaMult = 0.8f
                    position.inTL(2f, 2f)

                    onClick {
                        playClickSound()

                        selectedDMod = if (selectedDMod == dMod) null else dMod
                        refreshPanel(member, variant)
                    }
                }

                val textColor =
                    if (selectedDMod != null && selectedDMod == dMod) Misc.getNegativeHighlightColor() else Misc.getTextColor()
                innerElement.addPara(dMod.displayName, 0f, textColor, textColor).apply {
                    position.rightOfMid(sprite.elementPanel, 10f)
                }

                onHoverEnter {
                    playSound("ui_button_mouseover", 1f, 1f)
                    sprite.getSprite().alphaMult = 1f
                    backgroundAlpha = 1f
                }

                onHoverExit {
                    sprite.getSprite().alphaMult = 0.8f
                    backgroundAlpha = 0.3f
                }

                onClick {
                    playClickSound()

                    selectedDMod = if (selectedDMod == dMod) null else dMod
                    refreshPanel(member, variant)
                }
            }

            // Add tooltip to s-mod
            val ship = Global.getCombatEngine().createFXDrone(variant)
            dModsElement!!.addTooltipToPrevious(CustomHullmodToolTip(dMod, variant.hullSize, ship), TooltipMakerAPI.TooltipLocation.RIGHT)
        }
        dModsPanel.addUIElement(dModsElement)
        mainElement.addCustom(dModsPanel, 0f)
    }

    override fun onPanelClose(member: FleetMemberAPI?, variant: ShipVariantAPI?, market: MarketAPI?) {
        backgroundPanel = null
        mainPanel = null
        selectedDMod = null
    }

    override fun shouldShow(member: FleetMemberAPI?, variant: ShipVariantAPI?, market: MarketAPI?): Boolean {
        return true
    }
}