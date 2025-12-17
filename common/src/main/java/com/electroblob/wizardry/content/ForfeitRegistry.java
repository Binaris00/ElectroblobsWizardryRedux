package com.electroblob.wizardry.content;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.data.WizardData;
import com.electroblob.wizardry.api.content.event.EBDiscoverSpellEvent;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.SpellContext;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.entity.ArrowRainConstruct;
import com.electroblob.wizardry.content.entity.MeteorEntity;
import com.electroblob.wizardry.content.entity.construct.BlizzardConstruct;
import com.electroblob.wizardry.content.entity.construct.HailstormConstruct;
import com.electroblob.wizardry.content.entity.construct.IceSpikeConstruct;
import com.electroblob.wizardry.content.entity.construct.LightningSigilConstruct;
import com.electroblob.wizardry.content.entity.living.*;
import com.electroblob.wizardry.content.entity.projectile.FireBombEntity;
import com.electroblob.wizardry.content.spell.necromancy.Banish;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.core.mixin.accessor.FallingBlockEntityAccessor;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class ForfeitRegistry {
    private static final Set<Forfeit> FORFEITS = new HashSet<>();

    public static void create(String name, SpellTier tier, Element element, BiConsumer<Level, Player> effect) {
        create(new Forfeit(name, element, tier, effect));
    }

    public static void create(ResourceLocation location, SpellTier tier, Element element, BiConsumer<Level, Player> effect) {
        create(new Forfeit(location, element, tier, effect));
    }

    public static void create(Forfeit forfeit) {
        FORFEITS.add(forfeit);
    }

    public static Set<Forfeit> getForfeits() {
        return FORFEITS;
    }

    public static Forfeit getRandomForfeit(Random random, SpellTier tier, Element element) {
        List<Forfeit> forfeits = FORFEITS.stream().filter(forfeit ->
                forfeit.getSpellTier() == tier && forfeit.getElement() == element).toList();

        if (forfeits.isEmpty()) {
            EBLogger.warn("No forfeits with tier {} and element {}!", tier, element);
            return null;
        }
        return forfeits.get(random.nextInt(forfeits.size()));
    }

    public static void onSpellCastPreEvent(SpellCastEvent.Pre event) {
        if (!EBConfig.discoveryMode) return;
        // Only the server should do the random roll to avoid desynchronization
        if (event.getLevel().isClientSide) return;

        if (event.getCaster() instanceof Player player && !player.isCreative() && (event.getSource() == SpellCastEvent.Source.WAND || event.getSource() == SpellCastEvent.Source.SCROLL)) {

            SpellManagerData spellData = Services.OBJECT_DATA.getSpellManagerData(player);
            WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);

            float chance = (float) EBConfig.forfeitChance;
            if (EBAccessoriesIntegration.isEquipped(player, EBItems.AMULET_WISDOM.get())) chance *= 0.5F;

            boolean discover = !spellData.hasSpellBeenDiscovered(event.getSpell());
            float r = wizardData.getRandom().nextFloat();
            boolean trigger = r < chance && discover;
            EBLogger.warn("Forfeit roll: random={} chance={} discover={} trigger={}", r, chance, discover, trigger);

            if (trigger) {
                event.setCanceled(true);

                Forfeit forfeit = getRandomForfeit(wizardData.getRandom(), event.getSpell().getTier(), event.getSpell().getElement());

                if (forfeit == null) { // Should never happen, but just in case...
                    player.sendSystemMessage(Component.translatable("forfeit.ebwizardry.do_nothing"));
                    return;
                }

                EBLogger.warn("Applying forfeit {} to player {}", forfeit.getName(), player.getScoreboardName());
                forfeit.apply(event.getLevel(), player);

                ItemStack stack = player.getMainHandItem();

                if (!(stack.getItem() instanceof ISpellCastingItem)) {
                    stack = player.getOffhandItem();
                    if (!(stack.getItem() instanceof ISpellCastingItem)) stack = ItemStack.EMPTY;
                }

                if (!stack.isEmpty()) {
                    // Still need to charge the player mana or consume the scroll
                    if (event.getSource() == SpellCastEvent.Source.SCROLL) {
                        if (!player.isCreative()) stack.shrink(1);
                    } else if (stack.getItem() instanceof IManaStoringItem) {
                        int cost = (int) (event.getSpell().getCost() * event.getModifiers().get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding
                        ((IManaStoringItem) stack.getItem()).consumeMana(stack, cost, player);
                    }
                }

                EBAdvancementTriggers.SPELL_FAILURE.triggerFor(player);

                EntityUtil.playSoundAtPlayer(player, forfeit.getSound(), 1, 1);

                player.displayClientMessage(
                        event.getSource() == SpellCastEvent.Source.WAND ? forfeit.getMessageForWand() : forfeit.getMessageForScroll()
                        , true);
            }
        }
    }

    public static void onSpellCastPostEvent(SpellCastEvent.Post event) {
        if (event.getCaster() instanceof Player player) {

            if (player instanceof ServerPlayer serverPlayer)
                EBAdvancementTriggers.CAST_SPELL.trigger(serverPlayer, event.getSpell(), player.getItemInHand(player.getUsedItemHand()));

            SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
            if (!WizardryEventBus.getInstance().fire(new EBDiscoverSpellEvent(player, event.getSpell(), EBDiscoverSpellEvent.Source.CASTING))
                    && data.discoverSpell(event.getSpell())) {
                if (!event.getCaster().level().isClientSide && !player.isCreative() && EBConfig.discoveryMode) {
                    EntityUtil.playSoundAtPlayer(player, EBSounds.MISC_DISCOVER_SPELL.get(), 1.25f, 1);
                    Component message = Component.translatable("spell.discover", event.getSpell().getDescriptionFormatted());
                    player.sendSystemMessage(message);

                }
            }
        }
    }

    public static void register() {
        create("burn_self", SpellTiers.NOVICE, Elements.FIRE, (w, p) -> p.setSecondsOnFire(5));

        create("firebomb", SpellTiers.APPRENTICE, Elements.FIRE, (w, p) ->
                summon(w, p.blockPosition(), new FireBombEntity(w), 0, 5, 0));

        create("explode", SpellTiers.ADVANCED, Elements.FIRE, (w, p) ->
                w.explode(null, p.getX(), p.getY(), p.getZ(), 2, Level.ExplosionInteraction.NONE));

        create("blazes", SpellTiers.ADVANCED, Elements.FIRE, (w, p) -> {
            IntStream.range(0, 3).forEach(i -> summon(w, BlockUtil.findNearbyFloorSpace(p, 4, 2),
                    new Blaze(EntityType.BLAZE, w), 0.5F, 0, 0.5F));
        });

        create("burn_surroundings", SpellTiers.MASTER, Elements.FIRE, (w, p) -> {
            if (w.isClientSide || !EntityUtil.canDamageBlocks(p, w)) return;
            BlockUtil.getBlockSphere(p.blockPosition(), 6).stream()
                    .filter((pos) -> w.random.nextBoolean() && w.isEmptyBlock(pos) && BlockUtil.canPlaceBlock(p, w, pos))
                    .forEach((pos) -> w.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState()));
        });

        create("meteors", SpellTiers.MASTER, Elements.FIRE, (w, p) -> {
            if (!w.isClientSide) for (int i = 0; i < 5; i++)
                w.addFreshEntity(new MeteorEntity(w, p.getX() + w.random.nextDouble() * 16 - 8,
                        p.getY() + 40 + w.random.nextDouble() * 30, p.getZ() + w.random.nextDouble() * 16 - 8,
                        1, EntityUtil.canDamageBlocks(p, w)));
        });

        create("freeze_self", SpellTiers.NOVICE, Elements.ICE, (w, p) ->
                p.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 200)));

        create("freeze_self_2", SpellTiers.NOVICE, Elements.ICE, (w, p) ->
                p.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 300, 1)));

        create("ice_spikes", SpellTiers.APPRENTICE, Elements.ICE, (w, p) -> {
            if (w.isClientSide) return;
            for (int i = 0; i < 5; i++) {
                IceSpikeConstruct iceSpike = new IceSpikeConstruct(w);
                double x = p.getX() + 2 - w.random.nextFloat() * 4;
                double z = p.getZ() + 2 - w.random.nextFloat() * 4;
                Integer y = BlockUtil.getNearestSurface(w, BlockPos.containing(x, p.getY(), z), Direction.UP, 2, true,
                        BlockUtil.SurfaceCriteria.basedOn(ForfeitRegistry::isCollisionShapeFullBlock));
                iceSpike.lifetime = 45 + w.random.nextInt(15);
                if (y == null) break;
                iceSpike.setFacing(Direction.UP);
                iceSpike.setPos(x, y, z);
                w.addFreshEntity(iceSpike);
            }
        });

        create("blizzard", SpellTiers.ADVANCED, Elements.ICE, (w, p) -> {
            summon(w, p.blockPosition(), new BlizzardConstruct(w), 0, 0, 0);
        });

        create("ice_wraiths", SpellTiers.ADVANCED, Elements.ICE, (w, p) -> {
            IntStream.range(0, 3).forEach(i -> summon(w, BlockUtil.findNearbyFloorSpace(p, 4, 2),
                    new IceWraith(w), 0, 0, 0));
        });

        create("hailstorm", SpellTiers.MASTER, Elements.ICE, (w, p) -> {
            summon(w, p.blockPosition(), new HailstormConstruct(w), 0, 5, -3);
        });

        create("ice_giant", SpellTiers.MASTER, Elements.ICE, (w, p) -> {
            summon(w, p.blockPosition(), new IceGiant(w), p.getLookAngle().x * 4, 0, p.getLookAngle().z * 4);
        });

        create("thunder", SpellTiers.NOVICE, Elements.LIGHTNING, (w, p) -> {
            summon(w, BlockUtil.findNearbyFloorSpace(p, 4, 2),
                    new LightningWraith(w), 0.5, 0, 0.5);
        });

        create("storm", SpellTiers.APPRENTICE, Elements.LIGHTNING, (w, p) -> {
            if (!Spells.INVOKE_WEATHER.isEnabled(SpellContext.WANDS)) return;
            int shortWeatherTime = (100 + (new Random()).nextInt(200)) * 20;
            if (!w.isClientSide) ((ServerLevel) w).setWeatherParameters(0, shortWeatherTime, true, true);
        });


        create("lightning_sigils", SpellTiers.APPRENTICE, Elements.LIGHTNING, (w, p) -> {
            for (Direction direction : BlockUtil.getHorizontals()) {
                BlockPos pos = p.blockPosition().relative(direction, 2);
                Integer y = BlockUtil.getNearestFloor(w, pos, 2);
                if (y == null) continue;
                summon(w, pos.atY(y), new LightningSigilConstruct(w), 0.5, 0, 0.5);
            }
        });

        create("lightning", SpellTiers.ADVANCED, Elements.LIGHTNING, (w, p) -> {
            summon(w, p.blockPosition(), new LightningBolt(EntityType.LIGHTNING_BOLT, w), 0, 0, 0);
        });

        create("lightning_wraiths", SpellTiers.ADVANCED, Elements.LIGHTNING, (w, p) -> {
            IntStream.range(0, 3)
                    .forEach(i -> summon(w, BlockUtil.findNearbyFloorSpace(p, 4, 2),
                            new LightningWraith(w), 0.5, 0, 0.5));
        });

        create("storm_elemental", SpellTiers.MASTER, Elements.LIGHTNING, (w, p) -> {
            Arrays.stream(BlockUtil.getHorizontals()).forEach(direction ->
                    summon(w, p.blockPosition().relative(direction, 3), new StormElemental(w), 0.5, 0, 0.5));
        });

        create("nausea", SpellTiers.NOVICE, Elements.NECROMANCY, (w, p) ->
                p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400)));

        create("zombie_horde", SpellTiers.APPRENTICE, Elements.NECROMANCY, (w, p) -> {
            IntStream.range(0, 3).forEach(i ->
                    summon(w, BlockUtil.findNearbyFloorSpace(p, 4, 2),
                            new Zombie(EntityType.ZOMBIE, w), 0.5, 0, 0.5));
        });

        create("wither_self", SpellTiers.ADVANCED, Elements.NECROMANCY, (w, p) ->
                p.addEffect(new MobEffectInstance(MobEffects.WITHER, 400)));

        create("cripple_self", SpellTiers.ADVANCED, Elements.NECROMANCY, (w, p) ->
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 2)));

        create("shadow_wraiths", SpellTiers.MASTER, Elements.NECROMANCY, (w, p) -> {
            IntStream.range(0, 3).forEach(i ->
                    summon(w, BlockUtil.findNearbyFloorSpace(p, 4, 2),
                            new ShadowWraith(w), 0.5, 0, 0.5));
        });

        create("squid", SpellTiers.NOVICE, Elements.EARTH, (w, p) -> {
            summon(w, p.blockPosition(), new Squid(EntityType.SQUID, w), 0, 3, 0);
        });

        create("uproot_plants", SpellTiers.APPRENTICE, Elements.EARTH, (w, p) -> {
            if (!w.isClientSide && BlockUtil.canDamageBlocks(p, w)) {
                List<BlockPos> sphere = BlockUtil.getBlockSphere(p.blockPosition(), 5);
                sphere.removeIf(pos -> !BlockUtil.canBreakBlock(p, w, pos));
                sphere.forEach(pos -> w.destroyBlock(pos, true));
            }
        });

        create("poison_self", SpellTiers.APPRENTICE, Elements.EARTH, (w, p) ->
                p.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 1)));

        create("flood", SpellTiers.ADVANCED, Elements.EARTH, (w, p) -> {
            if (!w.isClientSide && BlockUtil.canDamageBlocks(p, w)) {
                List<BlockPos> sphere = BlockUtil.getBlockSphere(p.blockPosition().above(), 2);
                sphere.removeIf(pos -> !BlockUtil.canBlockBeReplaced(w, pos, true) || !BlockUtil.canPlaceBlock(p, w, pos));
                sphere.forEach(pos -> w.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState()));
            }
        });

        create("bury_self", SpellTiers.MASTER, Elements.EARTH, (w, p) -> {
            if (w.isClientSide) return;
            List<BlockPos> sphere = BlockUtil.getBlockSphere(p.blockPosition(), 4);
            sphere.forEach(pos -> {
                FallingBlockEntity blockEntity = FallingBlockEntityAccessor.createFallingBlockEntity(w, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Blocks.DIRT.defaultBlockState());
                blockEntity.setDeltaMovement(blockEntity.getDeltaMovement().x, 0.3 * (4 - (p.blockPosition().getY() - pos.getY())), blockEntity.getDeltaMovement().z);
                w.addFreshEntity(blockEntity);
            });
        });

        create("spill_inventory", SpellTiers.NOVICE, Elements.SORCERY, (w, p) -> {
            for (int i = 0; i < p.getInventory().items.size(); i++) {
                ItemStack stack = p.getInventory().items.get(i);
                if (!stack.isEmpty()) {
                    p.drop(stack, true, false);
                    p.getInventory().items.set(i, ItemStack.EMPTY);
                }
            }
        });

        create("teleport_self", SpellTiers.APPRENTICE, Elements.SORCERY, (w, p) ->
                ((Banish) Spells.BANISH).teleport(p, w, 8 + w.random.nextDouble() * 8));

        create("levitate_self", SpellTiers.ADVANCED, Elements.SORCERY, (w, p) ->
                p.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200)));

        create("vex_horde", SpellTiers.ADVANCED, Elements.SORCERY, (w, p) -> {
            IntStream.range(0, 4).forEach(i ->
                    summon(w, BlockUtil.findNearbyFloorSpace(p, 4, 2),
                            new Vex(EntityType.VEX, w), 0.5, 1, 0.5));
        });

        create("arrow_rain", SpellTiers.MASTER, Elements.SORCERY, (w, p) -> {
            summon(w, p.blockPosition(), new ArrowRainConstruct(w), 0, 5, -3);
        });

        create("damage_self", SpellTiers.NOVICE, Elements.HEALING, (w, p) -> p.hurt(p.damageSources().magic(), 4));

        create("spill_armour", SpellTiers.NOVICE, Elements.HEALING, (w, p) -> {
            IntStream.range(0, p.getInventory().armor.size()).forEach(i -> {
                ItemStack stack = p.getInventory().armor.get(i);
                if (!stack.isEmpty()) {
                    p.drop(stack, true, false);
                    p.getInventory().armor.set(i, ItemStack.EMPTY);
                }
            });
        });

        create("hunger", SpellTiers.APPRENTICE, Elements.HEALING, (w, p) ->
                p.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400, 4)));

        create("blind_self", SpellTiers.APPRENTICE, Elements.HEALING, (w, p) ->
                p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200)));

        create("weaken_self", SpellTiers.ADVANCED, Elements.HEALING, (w, p) ->
                p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 3)));

        create("jam_self", SpellTiers.ADVANCED, Elements.HEALING, (w, p) ->
                p.addEffect(new MobEffectInstance(EBMobEffects.ARCANE_JAMMER.get(), 300)));

        create("curse_self", SpellTiers.MASTER, Elements.HEALING, (w, p) ->
                p.addEffect(new MobEffectInstance(EBMobEffects.CURSE_OF_UNDEATH.get(), Integer.MAX_VALUE)));

        //        add(SpellTiers.NOVICE, Elements.EARTH, create("snares", (w, p) -> {
//            if (!w.isClientSide && EntityUtil.canDamageBlocks(p, w)) {
//                for (Direction direction : BlockUtil.getHorizontals()) {
//                    BlockPos pos = p.blockPosition().relative(direction);
//                    //if(BlockUtil.canBlockBeReplaced(w, pos) && BlockUtil.canPlaceBlock(p, w, pos))
//                    // todo snare block
//                    //w.setBlockAndUpdate(pos, EBBlocks.SNARE.get().defaultBlockState());
//                }
//            }
//        }));

        // TODO
//        add(SpellTiers.MASTER, Elements.SORCERY, create("black_hole", (w, p) -> {
//            EntityBlackHole blackHole = new EntityBlackHole(w);
//            Vec3 vec = p.getEyePosition(1).add(p.getLookAngle().scale(4));
//            blackHole.setPos(vec.x, vec.y, vec.z);
//            w.addFreshEntity(blackHole);
//        }));


        // TODO
        //add(Tiers.ADVANCED, Elements.LIGHTNING, create("paralyse_self", (w, p) -> p.addEffect(new MobEffectInstance(EBMobEffects.PARALYSIS.get(), 200))));
    }

    public static boolean isCollisionShapeFullBlock(BlockGetter blockGetter, BlockPos pos) {
        return blockGetter.getBlockState(pos).isCollisionShapeFullBlock(blockGetter, pos);
    }

    public static void summon(Level world, @Nullable BlockPos pos, Entity entity, double xOffset, double yOffset, double zOffset) {
        if (world.isClientSide) return;
        if (pos == null) return;
        entity.setPos(pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset);
        world.addFreshEntity(entity);
    }
}
