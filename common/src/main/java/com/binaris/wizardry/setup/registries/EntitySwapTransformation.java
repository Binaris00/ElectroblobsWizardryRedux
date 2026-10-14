package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.api.content.spell.internal.CastContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class EntitySwapTransformation implements EntityTransformation {
    private final Class<? extends LivingEntity> source;
    private final Function<Level, ? extends LivingEntity> factory;

    public EntitySwapTransformation(Class<? extends LivingEntity> source, Function<Level, ? extends LivingEntity> factory) {
        this.source = source;
        this.factory = factory;
    }

    @Override
    public boolean apply(LivingEntity entity, CastContext ctx) {
        if (!source.isInstance(entity)) return false;

        Level level = ctx.world();
        if (level.isClientSide) return false;

        LivingEntity replacement = factory.apply(level);
        if (replacement == null) return false;

        double x = entity.getX(), y = entity.getY(), z = entity.getZ();

        CompoundTag tag = new CompoundTag();
        entity.saveWithoutId(tag);
        tag.remove("UUID");
        tag.remove("id");
        replacement.load(tag);

        replacement.setHealth(Math.min(entity.getHealth(), replacement.getMaxHealth()));
        replacement.setPos(x, y, z);
        
        entity.discard();
        level.addFreshEntity(replacement);
        return true;
    }
}