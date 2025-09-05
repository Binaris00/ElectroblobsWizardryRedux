package com.electroblob.wizardry;

import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import com.electroblob.wizardry.api.content.enchantment.Imbuement;
import com.electroblob.wizardry.api.content.event.*;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.client.SpellGUIDisplay;
import com.electroblob.wizardry.client.sound.SoundLoop;
import com.electroblob.wizardry.content.ForfeitRegistry;
import com.electroblob.wizardry.content.data.SpellGlyphData;
import com.electroblob.wizardry.content.effect.FireSkinMobEffect;
import com.electroblob.wizardry.content.effect.StaticAuraMobEffect;
import com.electroblob.wizardry.content.effect.WardMobEffect;
import com.electroblob.wizardry.content.entity.construct.BubbleConstruct;
import com.electroblob.wizardry.content.item.WizardArmorItem;
import com.electroblob.wizardry.content.spell.lightning.Charge;
import com.electroblob.wizardry.content.spell.necromancy.CurseOfSoulbinding;
import com.electroblob.wizardry.core.EBArtifactsEffects;
import com.electroblob.wizardry.core.event.IWizardryEvent;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBAdvancementTriggers;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.client.EBKeyBinding;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Simple class to save all the event helper methods
 * This is internal use, you're not supposed to use this for any reason
 */
public final class EBEventHelper {
    private EBEventHelper() {
    }

    public static void register() {
        WizardryEventBus bus = WizardryEventBus.getInstance();
        onLivingHurtEvent(bus);
        onLivingTickEvent(bus);
        onClientTick(bus);
        onSpellPreCast(bus);
        onSpellPostCast(bus);
        onServerLevelLoad(bus);
        onSpellTickCast(bus);
        onPlayerJoin(bus);
        onLivingDeathEvent(bus);
        onItemTossEvent(bus);
        onEntityJoinLevel(bus);
        onSpellDiscovery(bus);
    }

    private static void onLivingHurtEvent(WizardryEventBus bus) {
        bus.register(EBLivingHurtEvent.class, Charge::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, StaticAuraMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, FireSkinMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, CurseOfSoulbinding::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, WardMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, BubbleConstruct::onLivingHurt);

        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_FIRE_MELEE.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::fireMeleeRing));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_ICE_MELEE.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::iceMeleeRing));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_NECROMANCY_MELEE.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::necromancyMeleeRing));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_EARTH_MELEE.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::earthMeleeRing));

        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_SHATTERING.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::shatteringRing));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_SOULBINDING.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::soulbindingRing));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_LEECHING.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::leechingRing));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_POISON.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::poisonRing));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_EXTRACTION.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::extractionRing));

        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.RING_LIGHTNING_MELEE.get(), e.getSource().getDirectEntity(), e, EBArtifactsEffects::lightningMeleeRing));

        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_WARDING.get(), e.getDamagedEntity(), e, EBArtifactsEffects::wardingAmulet));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_FIRE_PROTECTION.get(), e.getDamagedEntity(), e, EBArtifactsEffects::fireProtectionAmulet));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_ICE_PROTECTION.get(), e.getDamagedEntity(), e, EBArtifactsEffects::iceProtectionAmulet));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_CHANNELING.get(), e.getDamagedEntity(), e, EBArtifactsEffects::channelingAmulet));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_FIRE_CLOAKING.get(), e.getDamagedEntity(), e, EBArtifactsEffects::fireCloakingAmulet));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_POTENTIAL.get(), e.getDamagedEntity(), e, EBArtifactsEffects::potentialAmulet));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_LICH.get(), e.getDamagedEntity(), e, EBArtifactsEffects::lichAmulet));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_BANISHING.get(), e.getDamagedEntity(), e, EBArtifactsEffects::banishingAmulet));
        bus.register(EBLivingHurtEvent.class, (e) -> artifactLoad(EBItems.AMULET_TRANSIENCE.get(), e.getDamagedEntity(), e, EBArtifactsEffects::transienceAmulet));
    }

    private static void onLivingTickEvent(WizardryEventBus bus) {
        bus.register(EBLivingTick.class, PlayerWizardData::onUpdate);
        bus.register(EBLivingTick.class, MagicMobEffect::onLivingTick);
        bus.register(EBLivingTick.class, SpellGUIDisplay::onLivingTickEvent);
        bus.register(EBLivingTick.class, (e) -> artifactLoad(EBItems.RING_CONDENSING.get(), e.getEntity(), e, EBArtifactsEffects::condensingRingTick));
        bus.register(EBLivingTick.class, (e) -> artifactLoad(EBItems.CHARM_FEEDING.get(), e.getEntity(), e, EBArtifactsEffects::feedingCharm));
    }

    private static void onSpellDiscovery(WizardryEventBus bus) {
        bus.register(EBDiscoverSpellEvent.class, (event -> {
            if(!event.getPlayer().level().isClientSide)
                EBAdvancementTriggers.DISCOVER_SPELL.trigger((ServerPlayer) event.getPlayer(), event.getSpell(), event.getSource());
        }));
    }

    private static void onPlayerJoin(WizardryEventBus bus) {
        bus.register(EBPlayerJoinServerEvent.class, (event -> SpellGlyphData.get((ServerLevel) event.getPlayer().level()).sync((ServerPlayer) event.getPlayer())));
    }

    private static void onServerLevelLoad(WizardryEventBus bus) {
        bus.register(EBServerLevelLoadEvent.class, SpellGlyphData::onServerLevelLoad);
    }

    private static void onEntityJoinLevel(WizardryEventBus bus) {
        bus.register(EBEntityJoinLevelEvent.class, Imbuement::onEntityJoinLevel);
    }

    private static void onItemTossEvent(WizardryEventBus bus) {
        bus.register(EBItemTossEvent.class, Imbuement::onItemTossEvent);
    }

    private static void onLivingDeathEvent(WizardryEventBus bus) {
        bus.register(EBLivingDeathEvent.class, Imbuement::onLivingDeath);
        bus.register(EBLivingDeathEvent.class, (e) -> artifactLoad(EBItems.RING_COMBUSTION.get(), e.getEntity(), e, EBArtifactsEffects::combustionRingDeath));
        bus.register(EBLivingDeathEvent.class, (e) -> artifactLoad(EBItems.RING_ARCANE_FROST.get(), e.getEntity(), e, EBArtifactsEffects::arcaneFrost));
    }

    private static void onClientTick(WizardryEventBus bus) {
        bus.register(EBClientTickEvent.class, SoundLoop::onClientTick);
        bus.register(EBClientTickEvent.class, EBKeyBinding::onClientTick);
    }

    private static void onSpellPreCast(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Pre.class, WizardArmorItem::onSpellPreCast);
        bus.register(SpellCastEvent.Pre.class, ForfeitRegistry::onSpellCastPreEvent);
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_BLOCKWRANGLER.get(), e.getCaster(), e, EBArtifactsEffects::blockwrangler));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_BATTLEMAGE.get(), e.getCaster(), e, EBArtifactsEffects::battlemageRingPreCast));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_FIRE_BIOME.get(), e.getCaster(), e, EBArtifactsEffects::fireBiomesRingPreCast));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_ICE_BIOME.get(), e.getCaster(), e, EBArtifactsEffects::iceBiomesRingPreCast));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_EARTH_BIOME.get(), e.getCaster(), e, EBArtifactsEffects::earthBiomesRingPreCast));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_CONJURER.get(), e.getCaster(), e, EBArtifactsEffects::conjurerRing));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_FULL_MOON.get(), e.getCaster(), e, EBArtifactsEffects::fullMoon));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_STORM.get(), e.getCaster(), e, EBArtifactsEffects::stormRing));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.CHARM_HUNGER_CASTING.get(), e.getCaster(), e, EBArtifactsEffects::hungerCasting));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.CHARM_FLIGHT.get(), e.getCaster(), e, EBArtifactsEffects::flightCharm));
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.CHARM_EXPERIENCE_TOME.get(), e.getCaster(), e, EBArtifactsEffects::experienceTome));
    }

    private static void onSpellPostCast(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Post.class, ForfeitRegistry::onSpellCastPostEvent);
        bus.register(SpellCastEvent.Post.class, (e) -> artifactLoad(EBItems.RING_PALADIN.get(), e.getCaster(), e, EBArtifactsEffects::paladinRing));
    }

    private static void onSpellTickCast(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Tick.class, WizardArmorItem::onSpellTickCast);
    }

    private static <T extends IWizardryEvent> void artifactLoad(Item item, Entity entity, T event, BiConsumer<T, ItemStack> consumer){
        if(!(entity instanceof Player player)) return;

        if(EBAccessoriesIntegration.isAccessoriesLoaded()) {
            ItemStack stack = EBAccessoriesIntegration.getEquipped(player, item);
            if(stack != null) consumer.accept(event, stack);
            return;
        }

        // we use findAny() and not a list because we don't want to allow players
        // to have more than just 1 artifact for type
        Optional<ItemStack> optional = InventoryUtil.getPrioritisedHotBarAndOffhand(player).stream()
                .filter(stack -> stack.getItem().equals(item)).findAny();
        optional.ifPresent(stack -> consumer.accept(event, stack));
    }
}
