package com.binaris.wizardry.client;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.EBLogger;
import com.binaris.wizardry.api.client.util.GlyphClientHandler;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.content.data.SpellGlyphData;
import com.binaris.wizardry.core.OLDEBCONFIG;
import com.binaris.wizardry.core.config.EBConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SpellGUIDisplay {
    public static final int CHARGE_METER_WIDTH = 25;
    public static final int CHARGE_METER_HEIGHT = 9;
    public static final int SPELL_ICON_SIZE = 32;
    public static final float SPELL_NAME_SCALE = 0.5f;
    public static final float SPELL_NAME_OPACITY = 0.3f;
    private static final ResourceLocation CHARGE_METER = WizardryMainMod.location("textures/gui/spell_charge_meter.png");
    private static final Map<String, SpellHUDSkin> SKINS = new LinkedHashMap<>(14);
    private static final int SPELL_SWITCH_TIME = 4;
    private static final Minecraft mc = Minecraft.getInstance();
    private static int switchTimer = 0;

    public SpellGUIDisplay() {
    } // Why would you do this

    // Where the mod saves the default skin
    // TODO more spell guis
    public static void init() {
        addSkin("default", WizardryMainMod.location("gui/spell_hud/default.png"), WizardryMainMod.location("gui/spell_hud/default.json"));
    }

    public static void addSkin(String key, ResourceLocation texture, ResourceLocation metadata) {
        EBLogger.debug("Loading spell HUD skin: " + key);
        SKINS.put(key, new SpellHUDSkin(texture, metadata));
    }

    @Nullable
    public static SpellHUDSkin getSkin(String key) {
        SpellHUDSkin skin = SKINS.get(key);

        if (skin == null) {
            EBLogger.info("The spell HUD skin specified in the config did not match any of the loaded skins; using the default skin as a fallback.");

            skin = SKINS.get("default");

            if (skin == null) {
                EBLogger.warn("The default spell HUD skin is missing! A resource pack must have overridden it" + " with an invalid JSON file (default.json), please try again without any resource packs.");
                return null;
            }
        }

        return skin;
    }

    public static void draw(GuiGraphics guiGraphics, PoseStack stack, float partialTicks) {
        if (!OLDEBCONFIG.showSpellHUD && !OLDEBCONFIG.showChargeMeter) return;

        Player player = mc.player;
        if (player.isSpectator()) return;

        ItemStack wand = player.getMainHandItem();
        boolean mainHand = true;

        if (!(wand.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellHUD(player, wand))) {
            wand = player.getOffhandItem();
            mainHand = false;
            if (!(wand.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellHUD(player, wand)))
                return;
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        renderChargeMeter(stack, player, wand, width, height, partialTicks);
        renderSpellHUD(guiGraphics, stack, player, wand, mainHand, width, height, partialTicks, true);
        renderSpellHUD(guiGraphics, stack, player, wand, mainHand, width, height, partialTicks, false);
    }

    public static void playSpellSwitchAnimation(boolean next) {
        switchTimer = next ? SPELL_SWITCH_TIME : -SPELL_SWITCH_TIME;
    }

    public static void renderSpellHUD(GuiGraphics guiGraphics, PoseStack stack, Player player, ItemStack wand, boolean mainHand, int width, int height, float partialTicks, boolean textLayer) {
        if (!OLDEBCONFIG.showSpellHUD) return;

        if (!(wand.getItem() instanceof ISpellCastingItem))
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");

        boolean flipX = EBConfig.SPELL_HUD_FLIP_X.get();
        boolean flipY = EBConfig.SPELL_HUD_FLIP_Y.get();

        if (OLDEBCONFIG.spellHUDDynamicPositioning) {
            flipX = flipX == ((mainHand ? player.getMainArm() : player.getMainArm().getOpposite()) == HumanoidArm.LEFT);
        }

        SpellHUDSkin skin = getSkin("default");
        if (skin == null) return;

        stack.pushPose();

        int x = flipX ? width : 0;
        int y = flipY ? 0 : height;

        Spell spell = ((ISpellCastingItem) wand.getItem()).getCurrentSpell(wand);
        int cooldown = ((ISpellCastingItem) wand.getItem()).getCurrentCooldown(wand, player.level());
        int maxCooldown = ((ISpellCastingItem) wand.getItem()).getCurrentMaxCooldown(wand);

        if (textLayer) {
            float animationProgress = Math.signum(switchTimer) * ((SPELL_SWITCH_TIME - Math.abs(switchTimer) + partialTicks) / SPELL_SWITCH_TIME);

            Component prevSpellName = getFormattedSpellName(((ISpellCastingItem) wand.getItem()).getPreviousSpell(wand), player, 0);
            Component spellName = getFormattedSpellName(((ISpellCastingItem) wand.getItem()).getCurrentSpell(wand), player, cooldown);
            Component nextSpellName = getFormattedSpellName(((ISpellCastingItem) wand.getItem()).getNextSpell(wand), player, 0);

            Spell prevSpell = ((ISpellCastingItem) wand.getItem()).getPreviousSpell(wand);
            Spell nextSpell = ((ISpellCastingItem) wand.getItem()).getNextSpell(wand);
            Spell currentSpell = ((ISpellCastingItem) wand.getItem()).getCurrentSpell(wand);

            skin.drawText(guiGraphics, x, y, flipX, flipY, prevSpellName, spellName, nextSpellName, animationProgress);
        } else {
            boolean discovered = true;

            if (!player.isCreative()) {
                discovered = Services.OBJECT_DATA.getSpellManagerData(player).hasSpellBeenDiscovered(spell);
            }

            ResourceLocation icon = discovered ? WizardryMainMod.location("textures/spells/%s.png".formatted(spell.getLocation().getPath())) : WizardryMainMod.location("textures/spells/none.png");

            float progress = 1;
            if (!player.isCreative()) {
                progress = maxCooldown == 0 ? 1 : (maxCooldown - (float) cooldown + partialTicks) / maxCooldown;
            }

            skin.drawBackground(stack, x, y, flipX, flipY, icon, progress, player.isCreative(), player.hasEffect(EBMobEffects.ARCANE_JAMMER.get()));
        }

        stack.popPose();
    }

    public static void renderChargeMeter(PoseStack stack, Player player, ItemStack wand, int width, int height, float partialTicks) {
        if (player.isSpectator()) return;
        stack.pushPose();

        if (!OLDEBCONFIG.showChargeMeter) return;
        if (mc.options.renderDebug) return;
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) return;
        if (wand != player.getUseItem()) return;

        if (!(wand.getItem() instanceof ISpellCastingItem))
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");

        Spell spell = ((ISpellCastingItem) wand.getItem()).getCurrentSpell(wand);

        int chargeup = spell.getCharge();

        chargeup = (int) (chargeup * Services.OBJECT_DATA.getWizardData(player).getSpellModifiers().get(SpellModifiers.CHARGEUP));

        if (chargeup <= 0) return;

        if (player.getTicksUsingItem() == 0) return;
        float charge = (player.getTicksUsingItem() + partialTicks) / chargeup;
        if (charge > 1) return;

        RenderSystem.setShaderTexture(0, CHARGE_METER);

        // -1 to make it more centered...
        int x1 = width / 2 - CHARGE_METER_WIDTH / 2 - 1;
        float y = height / 2F - CHARGE_METER_HEIGHT / 2F - 0.5F;
        int w = (int) ((float) CHARGE_METER_WIDTH / 2 * charge);
        int u = CHARGE_METER_WIDTH - w;

        DrawingUtils.drawTexturedRect(x1, y, 0, 0, w, CHARGE_METER_HEIGHT, 32, 32);
        DrawingUtils.drawTexturedRect(x1 + u, y, u, 0, w, CHARGE_METER_HEIGHT, 32, 32);

        stack.popPose();
    }

    private static Component getFormattedSpellName(Spell spell, Player player, int cooldown) {
        boolean discovered = true;

        if (!player.isCreative()) {
            discovered = Services.OBJECT_DATA.getSpellManagerData(player).hasSpellBeenDiscovered(spell);
        }

        Style format = cooldown > 0 ? Style.EMPTY.withColor(ChatFormatting.GRAY) : Style.EMPTY.withColor(spell.getElement().getColor());
        if (!discovered)
            format = Style.EMPTY.withColor(ChatFormatting.BLUE).withFont(new ResourceLocation("minecraft", "alt"));

        if (player.hasEffect(EBMobEffects.ARCANE_JAMMER.get())) format = Style.EMPTY.withObfuscated(true);

        Component name = discovered ? Component.translatable(spell.getDescriptionId()) :
                Component.literal(SpellGlyphData.getGlyphName(spell, GlyphClientHandler.INSTANCE.getGlyphData()));
        ((MutableComponent) name).withStyle(format);
        return name;
    }


    public static void onLivingTickEvent(EBLivingTick event) {
        if (event.getLevel().isClientSide && event.getEntity() == mc.player) {
            if (switchTimer > 0) switchTimer--;
            else if (switchTimer < 0) switchTimer++;
        }
    }
}
