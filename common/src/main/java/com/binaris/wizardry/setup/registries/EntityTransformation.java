package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.api.content.spell.internal.CastContext;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface EntityTransformation {
    boolean apply(LivingEntity entity, CastContext ctx);

    static <T extends LivingEntity> EntityTransformation of(Class<T> type, BiConsumer<T, CastContext> action) {
        return (entity, ctx) -> {
            if (!type.isInstance(entity)) return false;
            action.accept(type.cast(entity), ctx);
            return true;
        };
    }

    static <T extends LivingEntity> EntityTransformation conditional(Class<T> type, Predicate<T> condition, BiConsumer<T, CastContext> action) {
        return (entity, ctx) -> {
            if (!type.isInstance(entity)) return false;
            T cast = type.cast(entity);
            if (!condition.test(cast)) return false;
            action.accept(cast, ctx);
            return true;
        };
    }
}
