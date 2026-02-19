package com.binaris.wizardry.core.networking;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.EBLogger;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.client.util.GlyphClientHandler;
import com.binaris.wizardry.api.content.entity.living.ISpellCaster;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.NoneSpell;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.client.ParticleSpawner;
import com.binaris.wizardry.content.data.SpellGlyphData;
import com.binaris.wizardry.content.item.ScrollItem;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.core.config.ConfigOption;
import com.binaris.wizardry.core.config.EBConfigManager;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.networking.s2c.*;
import com.binaris.wizardry.core.platform.Services;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;

/**
 * Handles messages received on the client side, we may only call client-side methods from here because we don't want to
 * accidentally reference client-only code on the server side.
 */
public final class ClientMessageHandler {
    public static void spellCast(SpellCastS2C m) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        Entity e = level.getEntity(m.getCasterID());
        if (!(e instanceof Player caster)) return;
        m.getSpell().cast(new PlayerCastContext(level, caster, m.getHand(), 0, m.getModifiers()));

        SpellCastEvent.Source source = SpellCastEvent.Source.OTHER;
        Item item = caster.getItemInHand(m.getHand()).getItem();

        if (item instanceof WandItem) source = SpellCastEvent.Source.WAND;
        else if (item instanceof ScrollItem) source = SpellCastEvent.Source.SCROLL;

        // No need to check if the spell succeeded, because the packet is only ever sent when it succeeds.
        // The handler for this event now deals with discovery.
        WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(source, m.getSpell(), caster, m.getModifiers()));
    }

    public static void npcSpellCast(NPCSpellCastS2C m) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        Entity caster = level.getEntity(m.getCasterID());
        Entity target = m.getTargetID() == -1 ? null : level.getEntity(m.getTargetID());

        // Safety check, the npc cannot be a non-living entity and the target must be a living entity
        if (!(caster instanceof LivingEntity livingCaster) || !(target instanceof LivingEntity livingTarget)) return;

        m.getSpell().cast(new EntityCastContext(level, livingCaster, m.getHand(), 0, livingTarget, m.getModifiers()));
        WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, m.getSpell(), livingCaster, m.getModifiers()));

        if (caster instanceof ISpellCaster spellCaster) {
            if (!m.getSpell().isInstantCast() || m.getSpell() instanceof NoneSpell) {
                spellCaster.setContinuousSpell(m.getSpell());
                spellCaster.setSpellCounter(m.getSpell() instanceof NoneSpell ? 0 : 1);
            } else {
                spellCaster.setSpellCounter(0);
            }
        }
    }

    public static void testParticle(TestParticlePacketS2C m) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        Player player = minecraft.player;
        if (level == null || player == null) return;

        level.addParticle(ParticleTypes.EXPLOSION_EMITTER,
                m.getPos().getX() + 0.5, m.getPos().getY() + 0.5, m.getPos().getZ() + 0.5,
                0, 0, 0);
        player.sendSystemMessage(Component.literal("Test particle at " + m.getPos() + " with color " + m.getColor()));
    }

    public static void spellGlyph(SpellGlyphPacketS2C m) {
        if (GlyphClientHandler.INSTANCE.getGlyphData() == null) {
            GlyphClientHandler.INSTANCE.setGlyphData(new SpellGlyphData());
        }

        ClientUtils.handleGlyphDataPacket(m);
    }

    public static void spellPropertiesSync(SpellPropertiesSyncS2C m) {
        for (Map.Entry<ResourceLocation, SpellProperties> entry : m.getPropertiesMap().entrySet()) {
            Optional<Spell> spell = Optional.ofNullable(Services.REGISTRY_UTIL.getSpell(entry.getKey()));
            if (spell.isEmpty()) {
                EBLogger.warn("Received spell properties for unknown spell: {}", entry.getKey());
                continue;
            }
            spell.get().setProperties(entry.getValue());
        }
    }

    public static void particleBuilder(ParticleBuilderS2C m) {
        // Use ParticleSpawner to avoid loading client classes in ParticleData
        ParticleSpawner.spawnClientParticle(m.getData());
    }

    public static void configSync(ConfigSyncS2C m) {
        // Find the config manager for this mod
        EBConfigManager manager = WizardryMainMod.getConfigManagers().stream()
                .filter(mgr -> mgr.getModId().equals(m.getModId()))
                .findFirst()
                .orElse(null);

        if (manager == null) {
            EBLogger.warn("Received config sync for unknown mod: {}", m.getModId());
            return;
        }

        // Apply the synced config data
        for (ConfigOption<?> option : manager.getConfigProvider().getOptions()) {
            if (m.getConfigData().containsKey(option.getKey())) {
                loadOption(option, m.getConfigData().get(option.getKey()));
            }
        }

        EBLogger.info("Synced config for mod: {}", m.getModId());
    }

    private static <T> void loadOption(ConfigOption<T> option, JsonElement element) {
        option.getCodec()
                .parse(com.mojang.serialization.JsonOps.INSTANCE, element)
                .result()
                .ifPresent(option::set);
    }
}
