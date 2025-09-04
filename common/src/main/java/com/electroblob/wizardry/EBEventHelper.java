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
import com.electroblob.wizardry.core.event.IWizardryEvent;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBAdvancementTriggers;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.client.EBKeyBinding;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
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
    }

    private static void onLivingTickEvent(WizardryEventBus bus) {
        bus.register(EBLivingTick.class, PlayerWizardData::onUpdate);
        bus.register(EBLivingTick.class, MagicMobEffect::onLivingTick);
        bus.register(EBLivingTick.class, SpellGUIDisplay::onLivingTickEvent);
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
    }

    private static void onClientTick(WizardryEventBus bus) {
        bus.register(EBClientTickEvent.class, SoundLoop::onClientTick);
        bus.register(EBClientTickEvent.class, EBKeyBinding::onClientTick);
    }

    private static void onSpellPreCast(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Pre.class, WizardArmorItem::onSpellPreCast);
        bus.register(SpellCastEvent.Pre.class, ForfeitRegistry::onSpellCastPreEvent);
        bus.register(SpellCastEvent.Pre.class, (e) -> artifactLoad(EBItems.RING_BATTLEMAGE.get(), e.getCaster(), e, EBArtifactsEffects::battlemageRingPreCast));
    }

    private static void onSpellPostCast(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Post.class, ForfeitRegistry::onSpellCastPostEvent);
    }

    private static void onSpellTickCast(WizardryEventBus bus) {
        bus.register(SpellCastEvent.Tick.class, WizardArmorItem::onSpellTickCast);
    }

    private static <T extends IWizardryEvent> void artifactLoad(Item item, LivingEntity entity, T event, BiConsumer<T, ItemStack> consumer){
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
