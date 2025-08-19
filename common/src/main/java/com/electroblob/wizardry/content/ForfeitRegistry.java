package com.electroblob.wizardry.content;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.entity.MeteorEntity;
import com.electroblob.wizardry.content.entity.construct.BlizzardConstruct;
import com.electroblob.wizardry.content.entity.construct.HailstormConstruct;
import com.electroblob.wizardry.content.entity.construct.IceSpikeConstruct;
import com.electroblob.wizardry.content.entity.construct.LightningSigilConstruct;
import com.electroblob.wizardry.content.entity.projectile.FireBombEntity;
import com.electroblob.wizardry.content.entity.projectile.MagicFireballEntity;
import com.electroblob.wizardry.content.spell.necromancy.Banish;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import static com.electroblob.wizardry.content.Forfeit.HORIZONTALS;

public class ForfeitRegistry {
    private static final ListMultimap<Pair<SpellTier, Element>, Forfeit> forfeits = ArrayListMultimap.create();
    private static final float TIER_CHANGE_CHANCE = 0.2f;

    public static void add(SpellTier tier, Element element, Forfeit forfeit){
        forfeits.put(Pair.of(tier, element), forfeit);
    }

    public static Forfeit getRandomForfeit(RandomSource random, SpellTier tier, Element element){
        float f = random.nextFloat();
        if(f < TIER_CHANGE_CHANCE) tier = tier.previous();
        else if(f > 1 - TIER_CHANGE_CHANCE) tier = tier.next();
        List<Forfeit> matches = forfeits.get(Pair.of(tier, element));
        if(matches.isEmpty()){
            EBLogger.warn("No forfeits with tier {} and element {}!", tier, element);
            return null;
        }
        return matches.get(random.nextInt(matches.size()));
    }

    public static Collection<Forfeit> getForfeits(){
        return Collections.unmodifiableCollection(forfeits.values());
    }

    public static Forfeit create(ResourceLocation name, BiConsumer<Level, Player> effect){
        return new Forfeit(name){
            @Override
            public void apply(Level world, Player player){
                effect.accept(world, player);
            }
        };
    }

    private static Forfeit create(String name, BiConsumer<Level, Player> effect){
        return create(new ResourceLocation(WizardryMainMod.MOD_ID, name), effect);
    }

    public static void onSpellCastPreEvent(SpellCastEvent.Pre event){
        if(!EBConfig.discoveryMode) return;
        if(!(event.getCaster() instanceof Player player)) return;
        if(player.isCreative()) return;

        PlayerWizardData data = Services.WIZARD_DATA.getWizardData(player, player.level());

        if(event.getSource() == SpellCastEvent.Source.WAND || event.getSource() == SpellCastEvent.Source.SCROLL){
            // TODO ARTIFACT
            //if(ArtefactItem.isArtefactActive(player, WizardryItems.AMULET_WISDOM.get())) chance *= 0.5;

            float f = WizardryMainMod.getRandom(player).nextFloat();

            if(f > (float)EBConfig.forfeitChance || data.hasSpellBeenDiscovered(event.getSpell())) return;

            event.setCanceled(true);
            Forfeit forfeit = getRandomForfeit(player.getRandom(), event.getSpell().getTier(), event.getSpell().getElement());
            if(forfeit == null){
                if(!event.getLevel().isClientSide) player.sendSystemMessage(Component.translatable("forfeit.ebwizardry.do_nothing"));
                return;
            }

            forfeit.apply(event.getLevel(), player);
            ItemStack stack = player.getMainHandItem();

            if(!(stack.getItem() instanceof ISpellCastingItem)){
                stack = player.getOffhandItem();
                if(!(stack.getItem() instanceof ISpellCastingItem)) stack = ItemStack.EMPTY;
            }

            if(!stack.isEmpty()){
                if(event.getSource() == SpellCastEvent.Source.SCROLL){
                    if(!player.isCreative()) stack.shrink(1);
                }else if(stack.getItem() instanceof IManaStoringItem){
                    int cost = (int)(event.getSpell().getCost() * event.getModifiers().get(SpellModifiers.COST) + 0.1f);
                    ((IManaStoringItem)stack.getItem()).consumeMana(stack, cost, player);
                }
            }

            // TODO
            // WizardryAdvancementTriggers.SPELL_FAILURE.triggerFor(player);

            //TODO
            //EntityUtils.playSoundAtPlayer(player, forfeit.getSound(), SoundSource.PLAYERS, 1, 1);

            if(!event.getLevel().isClientSide) player.displayClientMessage(
                    event.getSource() == SpellCastEvent.Source.WAND ? forfeit.getMessageForWand() : forfeit.getMessageForScroll(), true);
        }
    }

    public static void onSpellCastPostEvent(SpellCastEvent.Post event) {
        if (event.getCaster() instanceof Player player) {

            if (player instanceof ServerPlayer) {
                // TODO ADVANCEMENT
                //WizardryAdvancementTriggers.CAST_SPELL.trigger((ServerPlayer) player, event.getSpell(), player.getItemInHand(player.getUsedItemHand()));
            }

            PlayerWizardData data = Services.WIZARD_DATA.getWizardData(player, player.level());
            // TODO DISCOVER SPELL EVENT
            // !MinecraftForge.EVENT_BUS.post(new DiscoverSpellEvent(player, event.getSpell(), DiscoverSpellEvent.Source.CASTING))
            if(data.discoverSpell(event.getSpell())) {
                if(!event.getCaster().level().isClientSide && !player.isCreative() && EBConfig.discoveryMode) {
                    EntityUtil.playSoundAtPlayer(player, EBSounds.MISC_DISCOVER_SPELL.get(), 1.25f, 1);
                    Component message = Component.translatable("spell.discover", event.getSpell().getDescriptionFormatted());
                    player.sendSystemMessage(message);

                }
            }
        }
    }

    public static void register(){
        add(SpellTiers.NOVICE, Elements.FIRE, create("burn_self", (w, p) -> p.setSecondsOnFire(5)));

        add(SpellTiers.APPRENTICE, Elements.FIRE, create("fireball", (w, p) -> {
            if(!w.isClientSide){
                MagicFireballEntity fireball = new MagicFireballEntity(w);
                Vec3 vec = p.getEyePosition(1).add(p.getLookAngle().scale(6));
                fireball.setPos(vec.x, vec.y, vec.z);
                fireball.shoot(p.getX(), p.getY() + p.getEyeHeight(), p.getZ(), 1.5f, 1);
                w.addFreshEntity(fireball);
            }
        }));

        add(SpellTiers.APPRENTICE, Elements.FIRE, create("firebomb", (w, p) -> {
            if(!w.isClientSide){
                FireBombEntity firebomb = new FireBombEntity(w);
                firebomb.setPos(p.getX(), p.getY() + 5, p.getZ());
                w.addFreshEntity(firebomb);
            }
        }));

        add(SpellTiers.ADVANCED, Elements.FIRE, create("explode", (w, p) -> w.explode(null, p.getX(), p.getY(), p.getZ(), 1, Level.ExplosionInteraction.NONE)));

        add(SpellTiers.ADVANCED, Elements.FIRE, create("blazes", (w, p) -> {
            if(!w.isClientSide){
                for(int i = 0; i < 3; i++){
                    BlockPos pos = BlockUtil.findNearbyFloorSpace(p, 4, 2);
                    if(pos == null) break;
                    // TODO MINION
//                    BlazeMinion blaze = new BlazeMinion(w);
//                    blaze.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//                    w.addFreshEntity(blaze);
                }
            }
        }));

        add(SpellTiers.MASTER, Elements.FIRE, create("burn_surroundings", (w, p) -> {
            if(!w.isClientSide && EntityUtil.canDamageBlocks(p, w)){
                List<BlockPos> sphere = BlockUtil.getBlockSphere(p.blockPosition(), 6);
                for(BlockPos pos : sphere){
                    if(w.random.nextBoolean() && w.isEmptyBlock(pos) && BlockUtil.canPlaceBlock(p, w, pos))
                        w.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                }
            }
        }));

        add(SpellTiers.MASTER, Elements.FIRE, create("meteors", (w, p) -> {
            if(!w.isClientSide) for(int i=0; i<5; i++) w.addFreshEntity(new MeteorEntity(w, p.getX() + w.random.nextDouble() * 16 - 8,
                        p.getY() + 40 + w.random.nextDouble() * 30, p.getZ() + w.random.nextDouble() * 16 - 8,
                        1, EntityUtil.canDamageBlocks(p, w)));
        }));

        add(SpellTiers.NOVICE, Elements.ICE, create("freeze_self", (w, p) -> p.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 200))));

        add(SpellTiers.APPRENTICE, Elements.ICE, create("freeze_self_2", (w, p) -> p.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 300, 1))));

        add(SpellTiers.APPRENTICE, Elements.ICE, create("ice_spikes", (w, p) -> {
            if(!w.isClientSide){
                for(int i = 0; i < 5; i++){
                    IceSpikeConstruct iceSpike = new IceSpikeConstruct(w);
                    double x = p.getX() + 2 - w.random.nextFloat() * 4;
                    double z = p.getZ() + 2 - w.random.nextFloat() * 4;
                    Integer y = BlockUtil.getNearestSurface(w, BlockPos.containing(x, p.getY(), z), Direction.UP, 2, true,
                            BlockUtil.SurfaceCriteria.basedOn(ForfeitRegistry::isCollisionShapeFullBlock));
                    if(y == null) break;
                    iceSpike.setFacing(Direction.UP);
                    iceSpike.setPos(x, y, z);
                    w.addFreshEntity(iceSpike);
                }
            }
        }));

        add(SpellTiers.ADVANCED, Elements.ICE, create("blizzard", (w, p) -> {
            if(!w.isClientSide){
                BlizzardConstruct blizzard = new BlizzardConstruct(w);
                blizzard.setPos(p.getX(), p.getY(), p.getZ());
                w.addFreshEntity(blizzard);
            }
        }));

        add(SpellTiers.ADVANCED, Elements.ICE, create("ice_wraiths", (w, p) -> {
            if(!w.isClientSide){
                for(int i = 0; i < 3; i++){
                    BlockPos pos = BlockUtil.findNearbyFloorSpace(p, 4, 2);
                    // TODO MINION
//                    if(pos == null) break;
//                    IceWraith iceWraith = new IceWraith(w);
//                    iceWraith.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//                    w.addFreshEntity(iceWraith);
                }
            }
        }));

        add(SpellTiers.MASTER, Elements.ICE, create("hailstorm", (w, p) -> {
            if(!w.isClientSide){
                HailstormConstruct hailstorm = new HailstormConstruct(w);
                hailstorm.setPos(p.getX(), p.getY() + 5, p.getZ() - 3);
                w.addFreshEntity(hailstorm);
            }
        }));

        //TODO
        /*add(Tiers.MASTER, Elements.ICE, create("ice_giant", (w, p) -> {
            if(!w.isClientSide){
                EntityIceGiant iceGiant = new EntityIceGiant(w);
                iceGiant.setPos(p.getX() + p.getLookAngle().x * 4, p.getY(), p.getZ() + p.getLookAngle().z * 4);
                w.addFreshEntity(iceGiant);
            }
        }));*/

        add(SpellTiers.NOVICE, Elements.LIGHTNING, create("thunder", (w, p) -> {
            p.push(-p.getLookAngle().x, 0, -p.getLookAngle().z);
            if(w.isClientSide) w.addParticle(ParticleTypes.EXPLOSION_EMITTER, p.getX(), p.getY(), p.getZ(), 0, 0, 0);
        }));

        add(SpellTiers.APPRENTICE, Elements.LIGHTNING, create("storm", (w, p) -> {
            // TODO
            //if(!Spells.INVOKE_WEATHER.isEnabled(Context.WANDS)) return;
            int standardWeatherTime = (300 + (new Random()).nextInt(600)) * 20;
            if(!w.isClientSide) {
                ((ServerLevel)w).setWeatherParameters(standardWeatherTime, standardWeatherTime, true, true);
            }
        }));

        add(SpellTiers.APPRENTICE, Elements.LIGHTNING, create("lightning_sigils", (w, p) -> {
            if(!w.isClientSide){
                for(Direction direction : HORIZONTALS){
                    BlockPos pos = p.blockPosition().relative(direction, 2);
                    Integer y = BlockUtil.getNearestFloor(w, pos, 2);
                    if(y == null) continue;
                    LightningSigilConstruct sigil = new LightningSigilConstruct(w);
                    sigil.setPos(pos.getX() + 0.5, y, pos.getZ() + 0.5);
                    w.addFreshEntity(sigil);
                }
            }
        }));

        add(SpellTiers.ADVANCED, Elements.LIGHTNING, create("lightning", (w, p) -> {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, w);
            bolt.setPos(p.getX(), p.getY(), p.getZ());
            w.addFreshEntity(bolt);
        }));

        // TODO
        //add(Tiers.ADVANCED, Elements.LIGHTNING, create("paralyse_self", (w, p) -> p.addEffect(new MobEffectInstance(EBMobEffects.PARALYSIS.get(), 200))));

        //TODO
        /*add(Tiers.ADVANCED, Elements.LIGHTNING, create("lightning_wraiths", (w, p) -> {
            if(!w.isClientSide){
                for(int i = 0; i < 3; i++){
                    BlockPos pos = BlockUtils.findNearbyFloorSpace(p, 4, 2);
                    if(pos == null) break;
                    EntityLightningWraith lightningWraith = new EntityLightningWraith(w);
                    lightningWraith.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    w.addFreshEntity(lightningWraith);
                }
            }
        }));*/

        add(SpellTiers.MASTER, Elements.LIGHTNING, create("storm_elemental", (w, p) -> {
            if(!w.isClientSide){
                for(Direction direction : HORIZONTALS){
                    // TODO
//                    BlockPos pos = p.blockPosition().relative(direction, 3);
//                    StormElementsal stormElementsal = new StormElementsal(WizardryEntities.STORM_ElementsAL.get(), w);
//                    stormElementsal.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//                    w.addFreshEntity(stormElementsal);
                }
            }
        }));

        add(SpellTiers.NOVICE, Elements.NECROMANCY, create("nausea", (w, p) -> p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400))));

        add(SpellTiers.APPRENTICE, Elements.NECROMANCY, create("zombie_horde", (w, p) -> {
            if(!w.isClientSide){
                for(int i = 0; i < 3; i++){
                    BlockPos pos = BlockUtil.findNearbyFloorSpace(p, 4, 2);
//                    if(pos == null) break;
//                    ZombieMinion zombie = new ZombieMinion(w);
//                    zombie.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//                    w.addFreshEntity(zombie);
                }
            }
        }));

        add(SpellTiers.ADVANCED, Elements.NECROMANCY, create("wither_self", (w, p) -> p.addEffect(new MobEffectInstance(MobEffects.WITHER, 400))));

        add(SpellTiers.MASTER, Elements.NECROMANCY, create("cripple_self", (w, p) -> p.hurt(p.damageSources().magic(), p.getHealth() - 1)));

        /*add(Tiers.MASTER, Elements.NECROMANCY, create("shadow_wraiths", (w, p) -> {
            if(!w.isClientSide){
                for(Direction direction : Direction.HORIZONTALS){
                    BlockPos pos = p.blockPosition().relative(direction, 3);
                    EntityShadowWraith wraith = new EntityShadowWraith(w);
                    wraith.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    w.addFreshEntity(wraith);
                }
            }
        }));*/

        add(SpellTiers.NOVICE, Elements.EARTH, create("snares", (w, p) -> {
            if(!w.isClientSide && EntityUtil.canDamageBlocks(p, w)){
                for(Direction direction : HORIZONTALS){
                    BlockPos pos = p.blockPosition().relative(direction);
                    //if(BlockUtil.canBlockBeReplaced(w, pos) && BlockUtil.canPlaceBlock(p, w, pos))
                        // todo snare block
                        //w.setBlockAndUpdate(pos, EBBlocks.SNARE.get().defaultBlockState());
                }
            }
        }));

        add(SpellTiers.NOVICE, Elements.EARTH, create("squid", (w, p) -> {
            if(!w.isClientSide){
                Squid squid = new Squid(EntityType.SQUID, w);
                squid.setPos(p.getX(), p.getY() + 3, p.getZ());
                w.addFreshEntity(squid);
            }
        }));

        add(SpellTiers.APPRENTICE, Elements.EARTH, create("uproot_plants", (w, p) -> {
            if(!w.isClientSide && BlockUtil.canDamageBlocks(p, w)){
                List<BlockPos> sphere = BlockUtil.getBlockSphere(p.blockPosition(), 5);
                sphere.removeIf(pos -> !BlockUtil.canBreakBlock(p, w, pos));
                sphere.forEach(pos -> w.destroyBlock(pos, true));
            }
        }));

        add(SpellTiers.APPRENTICE, Elements.EARTH, create("poison_self", (w, p) -> p.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 1))));

        add(SpellTiers.ADVANCED, Elements.EARTH, create("flood", (w, p) -> {
            if(!w.isClientSide && BlockUtil.canDamageBlocks(p, w)){
                List<BlockPos> sphere = BlockUtil.getBlockSphere(p.blockPosition().above(), 2);
                sphere.removeIf(pos -> !BlockUtil.canBlockBeReplaced(w, pos, true) || !BlockUtil.canPlaceBlock(p, w, pos));
                sphere.forEach(pos -> w.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState()));
            }
        }));

        add(SpellTiers.MASTER, Elements.EARTH, create("bury_self", (w, p) -> {
            if(!w.isClientSide){
                List<BlockPos> sphere = BlockUtil.getBlockSphere(p.blockPosition(), 4);
                sphere.removeIf(pos -> !w.getBlockState(pos).isCollisionShapeFullBlock(w, pos) || BlockUtil.isBlockUnbreakable(w, pos) || BlockUtil.canBreakBlock(p, w, pos));
                sphere.forEach(pos -> {
                    try {
                        Constructor<FallingBlockEntity> constructor = FallingBlockEntity.class.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        FallingBlockEntity fallingblockentity = constructor.newInstance(w, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, w.getBlockState(pos));
                        fallingblockentity.setDeltaMovement(fallingblockentity.getDeltaMovement().x, 0.3 * (4 - (p.blockPosition().getY() - pos.getY())), fallingblockentity.getDeltaMovement().z);
                        w.addFreshEntity(fallingblockentity);
                    } catch (Exception  e) {
                        e.printStackTrace();
                    }
                });
            }
        }));

        add(SpellTiers.NOVICE, Elements.SORCERY, create("spill_inventory", (w, p) -> {
            for(int i = 0; i < p.getInventory().items.size(); i++){
                ItemStack stack = p.getInventory().items.get(i);
                if(!stack.isEmpty()){
                    p.drop(stack, true, false);
                    p.getInventory().items.set(i, ItemStack.EMPTY);
                }
            }
        }));

        add(SpellTiers.APPRENTICE, Elements.SORCERY, create("teleport_self", (w, p) -> ((Banish)Spells.BANISH).teleport(p, w, 8 + w.random.nextDouble() * 8)));

        add(SpellTiers.ADVANCED, Elements.SORCERY, create("levitate_self", (w, p) -> p.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200))));

        //TODO
        /*add(Tiers.ADVANCED, Elements.SORCERY, create("vex_horde", (w, p) -> {
            if(!w.isClientSide){
                for(int i = 0; i < 4; i++){
                    BlockPos pos = BlockUtils.findNearbyFloorSpace(p, 4, 2);
                    if(pos == null) break;
                    EntityVexMinion vex = new EntityVexMinion(w);
                    vex.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    w.addFreshEntity(vex);
                }
            }
        }));

        add(Tiers.MASTER, Elements.SORCERY, create("black_hole", (w, p) -> {
            EntityBlackHole blackHole = new EntityBlackHole(w);
            Vec3 vec = p.getEyePosition(1).add(p.getLookAngle().scale(4));
            blackHole.setPos(vec.x, vec.y, vec.z);
            w.addFreshEntity(blackHole);
        }));

        add(Tiers.MASTER, Elements.SORCERY, create("arrow_rain", (w, p) -> {
            if(!w.isClientSide){
                EntityArrowRain arrowRain = new EntityArrowRain(w);
                arrowRain.setPos(p.getX(), p.getY() + 5, p.getZ() - 3);
                w.addFreshEntity(arrowRain);
            }
        }));*/

        add(SpellTiers.NOVICE, Elements.HEALING, create("damage_self", (w, p) -> p.hurt(p.damageSources().magic(), 4)));

        add(SpellTiers.NOVICE, Elements.HEALING, create("spill_armour", (w, p) -> {
            for(int i = 0; i < p.getInventory().armor.size(); i++){
                ItemStack stack = p.getInventory().armor.get(i);
                if(!stack.isEmpty()){
                    p.drop(stack, true, false);
                    p.getInventory().armor.set(i, ItemStack.EMPTY);
                }
            }
        }));

        add(SpellTiers.APPRENTICE, Elements.HEALING, create("hunger", (w, p) -> p.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400, 4))));

        add(SpellTiers.APPRENTICE, Elements.HEALING, create("blind_self", (w, p) -> p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200))));

        add(SpellTiers.ADVANCED, Elements.HEALING, create("weaken_self", (w, p) -> p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 3))));

        // todo
        //add(Tiers.ADVANCED, Elements.HEALING, create("jam_self", (w, p) -> p.addEffect(new MobEffectInstance(EBMobEffects.ARCANE_JAMMER.get(), 300))));

        add(SpellTiers.MASTER, Elements.HEALING, create("curse_self", (w, p) -> p.addEffect(new MobEffectInstance(EBMobEffects.CURSE_OF_UNDEATH.get(), Integer.MAX_VALUE))));
    }

    public static boolean isCollisionShapeFullBlock(BlockGetter blockGetter, BlockPos pos) {
        return blockGetter.getBlockState(pos).isCollisionShapeFullBlock(blockGetter, pos);
    }
}
