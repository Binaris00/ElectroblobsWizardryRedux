package com.binaris.wizardry;

import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.api.content.event.*;
import com.binaris.wizardry.api.content.item.ArtifactItem;
import com.binaris.wizardry.api.content.spell.SpellContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.Forfeit;
import com.binaris.wizardry.content.data.SpellGlyphData;
import com.binaris.wizardry.content.effect.ContainmentEffect;
import com.binaris.wizardry.content.effect.FireSkinMobEffect;
import com.binaris.wizardry.content.effect.StaticAuraMobEffect;
import com.binaris.wizardry.content.effect.WardMobEffect;
import com.binaris.wizardry.content.entity.construct.BubbleConstruct;
import com.binaris.wizardry.content.item.WandUpgradeItem;
import com.binaris.wizardry.content.item.WizardArmorItem;
import com.binaris.wizardry.content.spell.healing.FontOfMana;
import com.binaris.wizardry.content.spell.lightning.Charge;
import com.binaris.wizardry.content.spell.necromancy.CurseOfSoulbinding;
import com.binaris.wizardry.content.spell.sorcery.ArcaneLockSpell;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.DataEvents;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.setup.registries.EBAdvancementTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

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
        onSpellPreCast(bus);
        onSpellPostCast(bus);
        onServerLevelLoad(bus);
        onSpellTickCast(bus);
        onPlayerJoin(bus);
        onLivingDeathEvent(bus);
        onItemTossEvent(bus);
        onEntityJoinLevel(bus);
        onSpellDiscovery(bus);
        onPlayerInteractEntity(bus);
        onItemPlaceInContainer(bus);
        onPlayerUseBlock(bus);
        onPlayerBreakBlock(bus);
    }

    private static void onLivingHurtEvent(WizardryEventBus bus) {
        bus.register(EBLivingHurtEvent.class, Charge::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, StaticAuraMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, FireSkinMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, CurseOfSoulbinding::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, WardMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, BubbleConstruct::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, ArtifactItem::onHurtEntity);
        bus.register(EBLivingHurtEvent.class, ArtifactItem::onPlayerHurt);
        bus.register(EBLivingHurtEvent.class, AllyDesignation::onLivingHurt);
    }

    private static void onLivingTickEvent(WizardryEventBus bus) {
        bus.register(EBLivingTick.class, MagicMobEffect::onLivingTick);
        bus.register(EBLivingTick.class, ArtifactItem::onTick);
        bus.register(EBLivingTick.class, DataEvents::onMinionTick);
        bus.register(EBLivingTick.class, DataEvents::onPlayerTick);
        bus.register(EBLivingTick.class, ContainmentEffect::onLivingUpdateEvent);
    }

    private static void onSpellDiscovery(WizardryEventBus bus) {
        bus.register(EBDiscoverSpellEvent.class, (event -> {
            if (!event.getPlayer().level().isClientSide)
                EBAdvancementTriggers.DISCOVER_SPELL.trigger((ServerPlayer) event.getPlayer(), event.getSpell(), event.getSource());
        }));
    }

    private static void onPlayerJoin(WizardryEventBus bus) {
        bus.register(EBPlayerJoinServerEvent.class, (event -> SpellGlyphData.get((ServerLevel) event.getPlayer().level()).sync((ServerPlayer) event.getPlayer())));
        bus.register(EBPlayerJoinServerEvent.class, (SpellProperties::onPlayerJoin));
        bus.register(EBPlayerJoinServerEvent.class, com.binaris.wizardry.core.config.EBConfigManager::onPlayerJoin);
    }

    private static void onServerLevelLoad(WizardryEventBus bus) {
        bus.register(EBServerLevelLoadEvent.class, SpellGlyphData::onServerLevelLoad);
    }

    private static void onEntityJoinLevel(WizardryEventBus bus) {
        bus.register(EBEntityJoinLevelEvent.class, DataEvents::onMinionJoinLevel);
    }

    private static void onItemTossEvent(WizardryEventBus bus) {
        bus.register(EBItemTossEvent.class, DataEvents::onConjureToss);
    }

    private static void onLivingDeathEvent(WizardryEventBus bus) {
        bus.register(EBLivingDeathEvent.class, ArtifactItem::onKillEntity);
        bus.register(EBLivingDeathEvent.class, DataEvents::onConjureEntityDeath);
        bus.register(EBLivingDeathEvent.class, WandUpgradeItem::onPlayerKillMob);
    }

    private static void onItemPlaceInContainer(WizardryEventBus bus) {
        bus.register(EBItemPlaceInContainerEvent.class, DataEvents::onConjureItemPlaceInContainer);
    }

    private static void onSpellPreCast(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Pre.class, WizardArmorItem::onSpellPreCast);
        bus.register(SpellCastEvent.Pre.class, Forfeit::onSpellCastPreEvent);
        bus.register(SpellCastEvent.Pre.class, ArtifactItem::onSpellPreCast);
        bus.register(SpellCastEvent.Pre.class, EBEventHelper::castContextCheck);
        bus.register(SpellCastEvent.Pre.class, FontOfMana::onSpellCastPreEvent);
    }

    private static void onSpellPostCast(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Post.class, Forfeit::onSpellCastPostEvent);
        bus.register(SpellCastEvent.Post.class, ArtifactItem::onSpellPostCast);
    }

    private static void onSpellTickCast(WizardryEventBus bus) {
    }

    private static void onPlayerInteractEntity(WizardryEventBus bus) {
        bus.register(EBPlayerInteractEntityEvent.class, DataEvents::onPlayerInteractMinion);
    }

    private static void onPlayerUseBlock(WizardryEventBus bus) {
        bus.register(EBPlayerUseBlockEvent.class, ArcaneLockSpell::onPlayerUseBlock);
    }

    private static void onPlayerBreakBlock(WizardryEventBus bus) {
        bus.register(EBPlayerBreakBlockEvent.class, ArcaneLockSpell::onPlayerBreakBlock);
    }

    private static void castContextCheck(SpellCastEvent.Pre event) {
        boolean enabled = switch (event.getSource()) {
            case WAND -> event.getSpell().isEnabled(SpellContext.WANDS);
            case SCROLL -> event.getSpell().isEnabled(SpellContext.SCROLL);
            case COMMAND -> event.getSpell().isEnabled(SpellContext.COMMANDS);
            case NPC -> event.getSpell().isEnabled(SpellContext.NPCS);
            case DISPENSER -> event.getSpell().isEnabled(SpellContext.DISPENSERS);
            default -> true;
        };

        // If a spell is disabled in the config, it will not work.
        if (!enabled) {
            if (event.getCaster() != null && !event.getCaster().level().isClientSide)
                event.getCaster().sendSystemMessage(Component.translatable("spell.disabled", event.getSpell().getDescriptionFormatted()));
            event.setCanceled(true);
        }
    }
}
