package com.electroblob.wizardry;

import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import com.electroblob.wizardry.api.content.enchantment.Imbuement;
import com.electroblob.wizardry.api.content.event.*;
import com.electroblob.wizardry.client.SpellGUIDisplay;
import com.electroblob.wizardry.client.sound.SoundLoop;
import com.electroblob.wizardry.content.effect.FireSkinMobEffect;
import com.electroblob.wizardry.content.effect.StaticAuraMobEffect;
import com.electroblob.wizardry.content.effect.WardMobEffect;
import com.electroblob.wizardry.content.entity.construct.BubbleConstruct;
import com.electroblob.wizardry.content.item.WizardArmorItem;
import com.electroblob.wizardry.content.spell.lightning.Charge;
import com.electroblob.wizardry.content.spell.necromancy.CurseOfSoulbinding;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.setup.registries.client.EBKeyBinding;

/**
 * Simple class to save all the event helper methods
 * This is internal use, you're not supposed to use this
 * */
public final class EBEventHelper {
    private EBEventHelper() {}

    public static void register(){
        WizardryEventBus bus = WizardryEventBus.getInstance();
        onLivingHurtEvent(bus);
        onLivingTickEvent(bus);
        onClientTick(bus);
        onSpellPreCast(bus);
        onSpellTickCast(bus);
        onPlayerJoin(bus);
        onLivingDeathEvent(bus);
        onItemTossEvent(bus);
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

    private static void onPlayerJoin(WizardryEventBus bus){

    }

    private static void onEntityJoinLevel(WizardryEventBus bus){
        bus.register(EBEntityJoinLevelEvent.class, Imbuement::onEntityJoinLevel);
    }

    private static void onItemTossEvent(WizardryEventBus bus){
        bus.register(EBItemTossEvent.class, Imbuement::onItemTossEvent);
    }

    private static void onLivingDeathEvent(WizardryEventBus bus){
        bus.register(EBLivingDeathEvent.class, Imbuement::onLivingDeath);
    }

    private static void onClientTick(WizardryEventBus bus){
        bus.register(EBClientTickEvent.class, SoundLoop::onClientTick);
        bus.register(EBClientTickEvent.class, EBKeyBinding::onClientTick);
    }

    private static void onSpellPreCast(WizardryEventBus bus){
        bus.register(SpellCastEvent.Pre.class, WizardArmorItem::onSpellPreCast);
    }

    private static void onSpellTickCast(WizardryEventBus bus){
        bus.register(SpellCastEvent.Tick.class, WizardArmorItem::onSpellTickCast);
    }
}
