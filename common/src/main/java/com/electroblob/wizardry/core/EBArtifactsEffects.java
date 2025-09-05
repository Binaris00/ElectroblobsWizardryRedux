package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.content.entity.projectile.IceShardEntity;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConjurationSpell;
import com.electroblob.wizardry.content.spell.healing.*;
import com.electroblob.wizardry.content.spell.necromancy.Banish;
import com.electroblob.wizardry.content.spell.necromancy.CurseOfSoulbinding;
import com.electroblob.wizardry.content.spell.sorcery.ImbueWeapon;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * You aren't supposed to use this, if you want to implement your own artifacts you could copy exactly the way that ebw
 * does it, but you could also use another way to load the logic and even use another different mod for it!!
 * <br><br>
 * We use {@link EBLivingTick} instead of the standard way with Accessories because that could lead to a weird logic
 * with duplicated code for non-accessories-artefacts and accessories-artefacts, so it's better to have all the artefacts
 * logic on the same place, here we could handle both cases without touching the Accessories API
 * <br><br>
 * You see that the methods always share an extent of the {@link com.electroblob.wizardry.core.event.IWizardryEvent IWizardryEvent}
 * and the Item, this helps to access/change things inside the event and also check/change things inside the artifact, this
 * gives the possibility to modify/check data and make features about it (thx Accessories API ^^)
 */
public final class EBArtifactsEffects {
    private EBArtifactsEffects() {
    }

    public static void condensingRingTick(EBLivingTick event, ItemStack stack) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.tickCount % 150 == 0) {
            InventoryUtil.getHotbar(player).stream()
                    .filter(st -> st.getItem() instanceof IManaStoringItem)
                    .forEach(st -> ((IManaStoringItem) st.getItem()).rechargeMana(st, 1));
        }
    }

    public static void feedingCharm(EBLivingTick event, ItemStack stack){
        if(!(event.getEntity() instanceof Player player)) return;
        if(player.tickCount % 100 != 0) return;

        if(player.getFoodData().getFoodLevel() < 20 - Spells.SATIETY.property(ReplenishHunger.HUNGER_POINTS)){
            if(findMatchingWandAndCast(player, Spells.SATIETY)) return;
        }
        if(player.getFoodData().getFoodLevel() < 20 - Spells.REPLENISH_HUNGER.property(ReplenishHunger.HUNGER_POINTS)){
            findMatchingWandAndCast(player, Spells.REPLENISH_HUNGER);
        }
    }

    public static void combustionRingDeath(EBLivingDeathEvent event, ItemStack stack) {
        if (event.getSource().typeHolder().is(EBDamageSources.FIRE.location()))
            event.getEntity().level().explode(event.getEntity(), event.getEntity().xo, event.getEntity().yo, event.getEntity().zo, 1.5f, Level.ExplosionInteraction.NONE);
    }

    // ==================================
    // LIVING HURT
    // ==================================

    public static void shatteringRing(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (player.level().random.nextFloat() < 0.15f && event.getDamagedEntity().getHealth() < 12f
                && event.getDamagedEntity().hasEffect(EBMobEffects.FROST.get()) && !event.getSource().isIndirect()) {

            event.setAmount(12f);

            for (int i = 0; i < 8; i++) {
                double dx = event.getDamagedEntity().level().random.nextDouble() - 0.5;
                double dy = event.getDamagedEntity().level().random.nextDouble() - 0.5;
                double dz = event.getDamagedEntity().level().random.nextDouble() - 0.5;
                IceShardEntity iceshard = new IceShardEntity(event.getDamagedEntity().level());
                iceshard.setPos(event.getDamagedEntity().xo + dx + Math.signum(dx) * event.getDamagedEntity().getBbWidth(),
                        event.getDamagedEntity().yo + event.getDamagedEntity().getBbHeight() / 2 + dy,
                        event.getDamagedEntity().zo + dz + Math.signum(dz) * event.getDamagedEntity().getBbWidth());
                iceshard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
                // TODO SET CASTER ON PROJECTILE
                iceshard.setOwner(player);
                event.getDamagedEntity().level().addFreshEntity(iceshard);
            }
        }
    }

    public static void soulbindingRing(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getSource().is(EBDamageSources.WITHER))) return;

        event.getDamagedEntity().addEffect(new MobEffectInstance(EBMobEffects.CURSE_OF_SOULBINDING.get(), 400));
        CurseOfSoulbinding.getSoulboundCreatures(Services.WIZARD_DATA.getWizardData(player, player.level())).add(event.getDamagedEntity().getUUID());
    }

    public static void leechingRing(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (player.level().random.nextFloat() < 0.3f && event.getSource().is(EBDamageSources.WITHER)) {
            if (player.getHealth() < player.getMaxHealth()) {
                float healFactor = Optional.ofNullable(Spells.LIFE_DRAIN.property(DefaultProperties.HEALTH)).map(Number::floatValue).orElse(0.5f);
                player.heal(event.getAmount() * healFactor);
            }
        }
    }

    public static void poisonRing(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getSource().is(EBDamageSources.POISON))) return;
        event.getDamagedEntity().addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
    }

    public static void lightningMeleeRing(EBLivingHurtEvent event, ItemStack stack) {
//        if (!(event.getSource().getEntity() instanceof Player player)) return;
//
//        if (meleeRing(event.getSource(), Elements.LIGHTNING)) {
//            EntityUtils.getLivingWithinRadius(3, player.getX(), player.getY(), player.getZ(), player.level()).stream()
//                    .filter(EntityUtils::isLiving)
//                    .filter(e -> e != player)
//                    .min(Comparator.comparingDouble(player::distanceToSqr))
//                    .ifPresent(target -> handleLightningEffect(player, target, event));
//        }
    }

    public static void wardingAmulet(EBLivingHurtEvent event, ItemStack stack) {
//        if (!event.getSource().isUnblockable() && event.getSource().isMagic()) {
//            event.setAmount(event.getAmount() * 0.9f);
//        }
    }

    public static void fireProtectionAmulet(EBLivingHurtEvent event, ItemStack stack) {
//        if (event.getSource().isFire()) {
//            event.setAmount(event.getAmount() * 0.7f);
//        }
    }

    public static void iceProtectionAmulet(EBLivingHurtEvent event, ItemStack stack) {
//        if (isElementalDamageOfType(event.getSource(), MagicDamage.DamageType.FROST)) {
//            event.setAmount(event.getAmount() * 0.7f);
//        }
    }

    public static void channelingAmulet(EBLivingHurtEvent event, ItemStack stack) {
//        if (!(event.getSource().getEntity() instanceof Player player)) return;
//
//        if (player.level().random.nextFloat() < 0.3f && isElementalDamageOfType(event.getSource(), MagicDamage.DamageType.SHOCK)) {
//            event.setCanceled(true);
//        }
    }

    public static void fireCloakingAmulet(EBLivingHurtEvent event, ItemStack stack) {
//        if (!(event.getSource().getEntity() instanceof Player player)) return;
//
//        if (!event.getSource().isUnblockable()) {
//            List<EntityFireRing> fireRings = player.level().getEntitiesOfClass(EntityFireRing.class, player.getBoundingBox());
//
//            for (EntityFireRing fireRing : fireRings) {
//                if (fireRing.getCaster() instanceof Player && (fireRing.getCaster() == player || AllyDesignationSystem.isOwnerAlly(player, fireRing))) {
//                    event.setAmount(event.getAmount() * 0.25f);
//                    break;
//                }
//            }
//        }
    }

    public static void potentialAmulet(EBLivingHurtEvent event, ItemStack stack) {
//        if (!(event.getSource().getEntity() instanceof Player player)) return;
//
//        if (player.level().random.nextFloat() < 0.2f && event.getSource().isMelee() && event.getSource().getDirectEntity() instanceof LivingEntity target) {
//            handleLightningEffect(player, target, event);
//        }
    }

    public static void lichAmulet(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (player.level().random.nextFloat() < 0.15f) {
            List<LivingEntity> nearbyMobs = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5));
            // TODO SUMMONED CREATURE / MINIONS
            //nearbyMobs.removeIf(e -> !(e instanceof ISummonedCreature && ((ISummonedCreature) e).getCaster() == player));

            if (!nearbyMobs.isEmpty()) {
                Collections.shuffle(nearbyMobs);
                nearbyMobs.get(0).hurt(event.getSource(), event.getAmount());
                event.setCanceled(true);
            }
        }
    }

    public static void banishingAmulet(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (player.level().random.nextFloat() < 0.2f && !event.getSource().isIndirect()
                && event.getSource().getDirectEntity() instanceof LivingEntity target) {
            ((Banish) Spells.BANISH).teleport(target, target.level(), 8 + target.level().random.nextDouble() * 8);
        }
    }

    public static void transienceAmulet(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (player.getHealth() <= 6 && player.level().random.nextFloat() < 0.25f) {
            //player.addEffect(new MobEffectInstance(EBMobEffects.TRANSIENCE.get(), 300)); TODO TRANSIENCE EFFECT
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false));
        }
    }

    public static void extractionRing(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getSource().is(EBDamageSources.SORCERY))) return;

        InventoryUtil.getPrioritisedHotBarAndOffhand(player).stream()
                .filter(s -> s.getItem() instanceof ISpellCastingItem && s.getItem() instanceof IManaStoringItem
                        && !((IManaStoringItem) s.getItem()).isManaFull(s))
                .findFirst()
                .ifPresent(s -> ((IManaStoringItem) s.getItem()).rechargeMana(s, 4 + player.level().random.nextInt(3)));
    }

    public static void arcaneFrost(EBLivingDeathEvent event, ItemStack stack) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getSource().is(EBDamageSources.FROST))) return;

        for (int i = 0; i < 8; i++) {
            double dx = event.getEntity().level().random.nextDouble() - 0.5;
            double dy = event.getEntity().level().random.nextDouble() - 0.5;
            double dz = event.getEntity().level().random.nextDouble() - 0.5;
            IceShardEntity iceshard = new IceShardEntity(event.getEntity().level());
            iceshard.setPos(event.getEntity().xo + dx + Math.signum(dx) * event.getEntity().getBbWidth(),
                    event.getEntity().yo + event.getEntity().getBbHeight() / 2 + dy,
                    event.getEntity().zo + dz + Math.signum(dz) * event.getEntity().getBbWidth());
            iceshard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
            // TODO SET CASTER ON PROJECTILE
            //iceshard.setCaster(player);
            event.getEntity().level().addFreshEntity(iceshard);
        }
    }

    public static void fireMeleeRing(EBLivingHurtEvent event, ItemStack stack) {
        if (meleeRing(event.getSource(), Elements.FIRE)) event.getDamagedEntity().setSecondsOnFire(5);
    }

    public static void iceMeleeRing(EBLivingHurtEvent event, ItemStack stack) {
        if (meleeRing(event.getSource(), Elements.ICE))
            event.getDamagedEntity().addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 200));
    }

    public static void necromancyMeleeRing(EBLivingHurtEvent event, ItemStack stack) {
        if (meleeRing(event.getSource(), Elements.NECROMANCY))
            event.getDamagedEntity().addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
    }

    public static void earthMeleeRing(EBLivingHurtEvent event, ItemStack stack) {
        if (meleeRing(event.getSource(), Elements.EARTH))
            event.getDamagedEntity().addEffect(new MobEffectInstance(MobEffects.POISON, 200));
    }

    public static void blockwrangler(SpellCastEvent.Pre event, ItemStack stack) {
        if (event.getSpell() == Spells.GREATER_TELEKINESIS)
            event.getModifiers().set(SpellModifiers.POTENCY, event.getModifiers().get(SpellModifiers.POTENCY) * 2, false);
    }

    public static void conjurerRing(SpellCastEvent.Pre event, ItemStack stack) {
        if (event.getSpell() instanceof ConjurationSpell)
            event.getModifiers().set(EBItems.DURATION_UPGRADE.get(), event.getModifiers().get(EBItems.DURATION_UPGRADE.get()) * 2, false);
    }

    // ==================================
    // POST CAST
    // ==================================
    public static void paladinRing(SpellCastEvent.Post event, ItemStack stack){
        if(!(event.getCaster() instanceof Player player)) return;

        if(event.getSpell() instanceof Heal || event.getSpell() instanceof HealAlly || event.getSpell() instanceof GreaterHeal){
            float healthGained = event.getSpell().property(DefaultProperties.HEALTH) * event.getModifiers().get(SpellModifiers.POTENCY);

            EntityUtil.getLivingWithinRadius(4, player.xo, player.yo, player.zo, event.getLevel())
                    .stream().filter(livingEntity -> AllyDesignationSystem.isAllied(player, livingEntity)
                            && livingEntity.getHealth() > 0 &&
                            livingEntity.getHealth() < livingEntity.getMaxHealth())
                    .forEach(livingEntity -> {
                            livingEntity.heal(healthGained * 0.2f);
                            if(event.getLevel().isClientSide) ParticleBuilder.spawnHealParticles(event.getLevel(), livingEntity);
                    });
        }
    }

    // ==================================
    // PRE CAST
    // ==================================
    public static void experienceTome(SpellCastEvent.Pre event, ItemStack stack){
        event.getModifiers().set(SpellModifiers.PROGRESSION, event.getModifiers().get(SpellModifiers.PROGRESSION) * 1.5f, false);
    }

    public static void hungerCasting(SpellCastEvent.Pre event, ItemStack stack){
        if(!(event.getCaster() instanceof Player player)) return;
        if(player.isCreative() || event.getSource() != SpellCastEvent.Source.WAND || !event.getSpell().isInstantCast()) return;

        ItemStack wand = player.getMainHandItem();

        if(!(wand.getItem() instanceof ISpellCastingItem && wand.getItem() instanceof IManaStoringItem)){
            wand = player.getOffhandItem();
            if(!(wand.getItem() instanceof ISpellCastingItem && wand.getItem() instanceof IManaStoringItem)) return;
        }

        if(((IManaStoringItem)wand.getItem()).getMana(wand) < event.getSpell().getCost() * event.getModifiers().get(SpellModifiers.COST)){
            int hunger = event.getSpell().getCost() / 5;

            if(player.getFoodData().getFoodLevel() >= hunger){
                player.getFoodData().eat(-hunger, 0);
                event.getModifiers().set(SpellModifiers.COST, 0, false);
            }
        }
    }

    public static void flightCharm(SpellCastEvent.Pre event, ItemStack stack){
        if(event.getSpell() == Spells.FLIGHT){ // TODO Spells.GLIDE
            event.getModifiers().set(SpellModifiers.POTENCY, 1.5f * event.getModifiers().get(SpellModifiers.POTENCY), true);
        }
    }


    public static void stormRing(SpellCastEvent.Pre event, ItemStack stack){
        if(event.getSpell().getElement() == Elements.LIGHTNING && event.getLevel().isThundering()){
            event.getModifiers().set(EBItems.COOLDOWN_UPGRADE.get(), event.getModifiers().get(EBItems.COOLDOWN_UPGRADE.get()) * 0.3f, false);
        }
    }

    public static void fullMoon(SpellCastEvent.Pre event, ItemStack stack) {
        if (event.getSpell().getElement() == Elements.EARTH && !event.getCaster().level().isDay()
                && event.getCaster().level().getMoonPhase() == 0) {
            event.getModifiers().set(EBItems.COOLDOWN_UPGRADE.get(), event.getModifiers().get(EBItems.COOLDOWN_UPGRADE.get()) * 0.3f, false);
        }
    }

    public static void battlemageRingPreCast(SpellCastEvent.Pre event, ItemStack stack) {
        if (!(event.getCaster() instanceof Player player)) return;

        if (player.getOffhandItem().getItem() instanceof ISpellCastingItem && ImbueWeapon.isSword(player.getMainHandItem())) {
            event.getModifiers().set(SpellModifiers.POTENCY, 1.1f * event.getModifiers().get(SpellModifiers.POTENCY), false);
        }
    }

    public static void fireBiomesRingPreCast(SpellCastEvent.Pre event, ItemStack stack) {
        if (event.getSpell().getElement() == Elements.FIRE &&
                Services.PLATFORM.intHotBiomes(event.getCaster().level().getBiome(event.getCaster().blockPosition()))) {
            event.getModifiers().set(SpellModifiers.POTENCY, 1.3f * event.getModifiers().get(SpellModifiers.POTENCY), false);
        }
    }

    public static void iceBiomesRingPreCast(SpellCastEvent.Pre event, ItemStack stack) {
        if (event.getSpell().getElement() == Elements.ICE &&
                Services.PLATFORM.inIceBiomes(event.getCaster().level().getBiome(event.getCaster().blockPosition()))) {
            event.getModifiers().set(SpellModifiers.POTENCY, 1.3f * event.getModifiers().get(SpellModifiers.POTENCY), false);
        }
    }

    public static void earthBiomesRingPreCast(SpellCastEvent.Pre event, ItemStack stack) {
        if (event.getSpell().getElement() == Elements.EARTH &&
                Services.PLATFORM.inEarthBiomes(event.getCaster().level().getBiome(event.getCaster().blockPosition()))) {
            event.getModifiers().set(SpellModifiers.POTENCY, 1.3f * event.getModifiers().get(SpellModifiers.POTENCY), false);
        }
    }

    // ==================================
    // UTILS
    // ==================================

    /**
     * Check if the source isn't melee (like a projectile, explosion or something like that), the direct entity isn't
     * null and if it's a living one, and finally check the main hand for seeing the wand element. Quite weird but this
     * logic is shared for a lot of artifacts
     */
    private static boolean meleeRing(DamageSource source, Element element) {
        return !source.isIndirect() && source.getEntity() instanceof LivingEntity living
                && living.getMainHandItem().getItem() instanceof WandItem wand && wand.element == element;
    }

    /**
     * Helper method that scans through all wands on the given player's hotbar and offhand and casts the given spell if
     * it is bound to any of them. This is a useful code pattern for artefact effects.
     */
    public static boolean findMatchingWandAndCast(Player player, Spell spell){
        return findMatchingWandAndExecute(player, spell, wand -> {
            SpellModifiers modifiers = new SpellModifiers();
            if(((ISpellCastingItem)wand.getItem()).canCast(wand, spell, new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, modifiers))){
                ((ISpellCastingItem)wand.getItem()).cast(wand, spell, new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, modifiers));
            }
        });
    }

    /**
     * Helper method that scans through all wands on the given player's hotbar and offhand and executes the given action
     * if any of them have the given spell bound to them. This is a useful code pattern for artefact effects.
     */
    public static boolean findMatchingWandAndExecute(Player player, Spell spell, Consumer<? super ItemStack> action){
        List<ItemStack> hotbar = InventoryUtil.getPrioritisedHotBarAndOffhand(player);

        Optional<ItemStack> stack = hotbar.stream().filter(s -> s.getItem() instanceof ISpellCastingItem
                && Arrays.asList(((ISpellCastingItem)s.getItem()).getSpells(s)).contains(spell)).findFirst();

        stack.ifPresent(action);
        return stack.isPresent();
    }
}
