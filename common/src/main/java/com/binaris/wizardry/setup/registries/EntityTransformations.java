package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.content.spell.necromancy.Metamorphosis;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class EntityTransformations {
    static final Map<String, EntityTransformation> TRANSFORMATIONS = new LinkedHashMap<>();

    private EntityTransformations() {}

    public static void init() {
        swap("pig_to_zombified_piglin", Pig.class, l -> new ZombifiedPiglin(EntityType.ZOMBIFIED_PIGLIN, l));
        swap("zombified_piglin_to_pig", ZombifiedPiglin.class, l -> new Pig(EntityType.PIG, l));

        swap("cow_to_mushroom_cow", Cow.class, l -> new MushroomCow(EntityType.MOOSHROOM, l));
        swap("mushroom_cow_to_cow", MushroomCow.class, l -> new Cow(EntityType.COW, l));

        swap("chicken_to_bat", Chicken.class, l -> new Bat(EntityType.BAT, l));
        swap("bat_to_chicken", Bat.class, l -> new Chicken(EntityType.CHICKEN, l));

        swap("zombie_to_husk", Zombie.class, l -> new Husk(EntityType.HUSK, l));
        swap("husk_to_zombie", Husk.class, l -> new Zombie(EntityType.ZOMBIE, l));

        swap("skeleton_to_stray", Skeleton.class, l -> new Stray(EntityType.STRAY, l));
        swap("skeleton_to_wither_skeleton", Skeleton.class, l -> new WitherSkeleton(EntityType.WITHER_SKELETON, l));
        swap("stray_to_wither_skeleton", Stray.class, l -> new WitherSkeleton(EntityType.WITHER_SKELETON, l));
        swap("wither_skeleton_to_skeleton", WitherSkeleton.class, l -> new Skeleton(EntityType.SKELETON, l));

        swap("spider_to_cave_spider", Spider.class, l -> new CaveSpider(EntityType.CAVE_SPIDER, l));
        swap("cave_spider_to_spider", CaveSpider.class, l -> new Spider(EntityType.SPIDER, l));

        swap("slime_to_magma_cube", Slime.class, l -> new MagmaCube(EntityType.MAGMA_CUBE, l));
        swap("magma_cube_to_slime", MagmaCube.class, l -> new Slime(EntityType.SLIME, l));

        change("axolotl_variant", Axolotl.class, (axolotl, ctx) -> Metamorphosis.axolotlVariant(ctx, axolotl));

        change("sheep_color", Sheep.class, (sheep, ctx) -> sheep.setColor(Sheep.getRandomSheepColor(ctx.world().random)));
        change("fox_type", Fox.class, (fox, ctx) -> fox.setVariant(pick(ctx, Fox.Type.values())));
        change("horse_variant", Horse.class, (horse, ctx) -> horse.setVariant(pick(ctx, Variant.values())));
        change("llama_variant", Llama.class, (llama, ctx) -> llama.setVariant(pick(ctx, Llama.Variant.values())));
        change("panda_gene", Panda.class, (panda, ctx) -> {
            panda.setMainGene(pick(ctx, Panda.Gene.values()));
            panda.setHiddenGene(pick(ctx, Panda.Gene.values()));
        });
        change("rabbit_variant", Rabbit.class, (rabbit, ctx) -> rabbit.setVariant(pick(ctx, Rabbit.Variant.values())));
         change("frog_variant", Frog.class, (frog, ctx) -> frog.setVariant(pick(ctx,
                 new FrogVariant[]{FrogVariant.TEMPERATE, FrogVariant.WARM, FrogVariant.COLD})));
        
        change("cat_variant", Cat.class, (cat, ctx) -> {
            List<CatVariant> variants = BuiltInRegistries.CAT_VARIANT.stream().toList();
            cat.setVariant(variants.get(ctx.world().random.nextInt(variants.size())));
        });
    }

    public static void register(String id, EntityTransformation transformation) {
        if (TRANSFORMATIONS.putIfAbsent(id, transformation) != null)
            throw new IllegalArgumentException("Duplicate entity transformation id: " + id);
    }

    public static List<EntityTransformation> getAll() {
        return List.copyOf(TRANSFORMATIONS.values());
    }

    public static boolean applyRandom(LivingEntity entity, CastContext ctx) {
        List<EntityTransformation> transformations = new ArrayList<>(TRANSFORMATIONS.values());
        for (int i = transformations.size() - 1; i > 0; i--) {
            Collections.swap(transformations, i, ctx.world().random.nextInt(i + 1));
        }

        for (EntityTransformation transformation : transformations) {
            if (transformation.apply(entity, ctx)) return true;
        }
        return false;
    }

    private static void swap(String id, Class<? extends LivingEntity> source, Function<Level, ? extends LivingEntity> factory) {
        register(id, new EntitySwapTransformation(source, factory));
    }

    private static <T extends LivingEntity> void change(String id, Class<T> type, BiConsumer<T, CastContext> action) {
        register(id, EntityTransformation.of(type, action));
    }

    private static <T extends LivingEntity> void changeIf(String id, Class<T> type, Predicate<T> condition, BiConsumer<T, CastContext> action) {
        register(id, EntityTransformation.conditional(type, condition, action));
    }

    private static <T> T pick(CastContext ctx, T[] values) {
        RandomSource random = ctx.world().random;
        return values[random.nextInt(values.length)];
    }
}
