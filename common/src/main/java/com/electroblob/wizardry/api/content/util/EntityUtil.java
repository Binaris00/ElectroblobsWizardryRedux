package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.content.entity.living.ISpellCaster;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.google.common.collect.Streams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class EntityUtil {
    private EntityUtil() {}

    @Nullable
    public static Entity getEntityByUUID(Level world, @Nullable UUID id){
        if(id == null) return null; // It would return null eventually, but there's no point even looking

        if(world instanceof ServerLevel serverWorld){
            for (Entity entity : serverWorld.getAllEntities()) {
                if(entity.getUUID().equals(id)) return entity;
            }
        }
        return null;
    }

    public static List<LivingEntity> getLivingEntitiesInRange(Level world, double x, double y, double z, double range) {
        return getEntitiesInRange(world, x, y, z, range, LivingEntity.class);
    }

    public static List<LivingEntity> getLivingWithinRadius(double radius, double x, double y, double z, Level world) {
        return getEntitiesWithinRadius(radius, x, y, z, world, LivingEntity.class);
    }

    public static <T extends Entity> List<T> getEntitiesInRange(Level world, double x, double y, double z, double range, Class<T> entityClass){
        AABB boundingBox = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
        Predicate<T> alwaysTrue = entity -> true;

        List<T> entities = world.getEntitiesOfClass(entityClass, boundingBox, alwaysTrue);
        entities.removeIf(livingEntity -> livingEntity.distanceToSqr(x, y, z) > range);
        return entities;
    }

    public static <T extends Entity> List<T> getEntitiesWithinRadius(double radius, double x, double y, double z, Level world, Class<T> entityType) {
        AABB box = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        List<T> entityList = world.getEntitiesOfClass(entityType, box);
        for (int i = 0; i < entityList.size(); i++) {
            if (entityList.get(i).distanceToSqr(x, y, z) > radius) {
                entityList.remove(i);
                break;
            }
        }
        return entityList;
    }

    //@Deprecated(forRemoval = true, since = "1.20.1")
    public static boolean isLiving(Entity entity){
        return entity instanceof LivingEntity && !(entity instanceof ArmorStand);
    }

    public static boolean attackEntityWithoutKnockback(Entity entity, DamageSource source, float amount) {
        Vec3 originalVec = entity.getDeltaMovement();
        boolean succeeded = entity.hurt(source, amount);
        entity.setDeltaMovement(originalVec);
        return succeeded;
    }

    @Nullable
    public static Entity getRider(Entity entity) {
        return !entity.getPassengers().isEmpty() ? entity.getPassengers().get(0) : null;
    }

    /**
     * Finds the nearest space to the specified position that the given entity can teleport to without being inside one
     * or more solid blocks. The search volume is twice the size of the entity's bounding box (meaning that when
     * teleported to the returned position, the original destination remains within the entity's bounding box).
     * @param entity The entity being teleported
     * @param destination The target position to search around
     * @param accountForPassengers True to take passengers into account when searching for a space, false to ignore them
     * @return The resulting position, or null if no space was found.
     */
    public static Vec3 findSpaceForTeleport(Entity entity, Vec3 destination, boolean accountForPassengers) {
        Level world = entity.level();
        AABB box = entity.getBoundingBox();

        if (accountForPassengers) {
            for (Entity passenger : entity.getPassengers()) {
                box = box.minmax(passenger.getBoundingBox());
            }
        }

        box = box.move(destination.subtract(entity.getX(), entity.getY(), entity.getZ()));

        Iterable<BlockPos> cuboid = BlockPos.betweenClosed(Mth.floor(box.minX), Mth.floor(box.minY),
                Mth.floor(box.minZ), Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ));

        if (Streams.stream(cuboid).noneMatch(b -> !world.noCollision(new AABB(b)))) {
            return destination;

        } else {
            double dx = box.maxX - box.minX;
            double dy = box.maxY - box.minY;
            double dz = box.maxZ - box.minZ;

            int nx = Mth.ceil(dx) / 2;
            int px = Mth.ceil(dx) - nx;
            int ny = Mth.ceil(dy) / 2;
            int py = Mth.ceil(dy) - ny;
            int nz = Mth.ceil(dz) / 2;
            int pz = Mth.ceil(dz) - nz;

            List<BlockPos> nearby = Streams.stream(BlockPos.betweenClosed(Mth.floor(box.minX) - 1, Mth.floor(box.minY) - 1, Mth.floor(box.minZ) - 1, Mth.floor(box.maxX) + 1, Mth.floor(box.maxY) + 1, Mth.floor(box.maxZ) + 1)).collect(Collectors.toList());

            List<BlockPos> possiblePositions = Streams.stream(cuboid).collect(Collectors.toList());

            while (!nearby.isEmpty()) {
                BlockPos pos = nearby.remove(0);

                if (!world.noCollision(new AABB(pos))) {
                    Predicate<BlockPos> nearSolidBlock = b -> b.getX() >= pos.getX() - nx && b.getX() <= pos.getX() + px
                            && b.getY() >= pos.getY() - ny && b.getY() <= pos.getY() + py
                            && b.getZ() >= pos.getZ() - nz && b.getZ() <= pos.getZ() + pz;
                    nearby.removeIf(nearSolidBlock);
                    possiblePositions.removeIf(nearSolidBlock);
                }
            }

            if (possiblePositions.isEmpty()) return null;

            BlockPos nearest = possiblePositions.stream().min(Comparator.comparingDouble(b -> destination.distanceToSqr(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5))).get();

            return GeometryUtil.getFaceCentre(nearest, Direction.DOWN);
        }
    }


    public static List<LivingEntity> getLivingWithinCylinder(double radius, double x, double y, double z, double height, Level world) {
        return getEntitiesWithinCylinder(radius, x, y, z, height, world, LivingEntity.class);
    }

    public static <T extends Entity> List<T> getEntitiesWithinCylinder(double radius, double x, double y, double z, double height, Level world, Class<T> entityType) {
        AABB aabb = new AABB(x - radius, y, z - radius, x + radius, y + height, z + radius);
        List<T> entityList = world.getEntitiesOfClass(entityType, aabb);
        for(T entity : entityList) {
            if (entity.distanceToSqr(x, entity.yo, z) > radius) {
                entityList.remove(entity);
                break;
            }
        }
        return entityList;
    }


    public static boolean canDamageBlocks(@Nullable Entity entity, Level world) {
//        if (entity == null) return WizardryConfig.dispenserBlockDamage;
//        else if (entity instanceof PlayerEntity) return ((PlayerEntity) entity).canModifyBlocks() && WizardryConfig.playerBlockDamage;
//        // TODO: Forge Event Factory
//        //return ForgeEventFactory.getMobGriefingEvent(world, entity);
        return true;
    }

    public static int getDefaultAimingError(Difficulty difficulty){
        return switch (difficulty) {
            case EASY -> 10;
            case NORMAL -> 6;
            case HARD -> 2;
            default -> 10;
        };
    }

    public static void playSoundAtPlayer(Player player, SoundEvent sound, float volume, float pitch) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
    }

    public static boolean isCasting(LivingEntity caster, Spell spell) {
        if(spell.isInstantCast()) return false;

        if (caster instanceof Player) {
            // TODO WIZARD DATA CURRENT SPELL
//            WizardData data = WizardData.get((Player) caster);
//            if (data != null && data.currentlyCasting() == spell) return true;

            // TODO Something weird here
            // Normal to return negative ticks?? anyway, I convert
            // the ticks to positive to it can be compared correctly with the charge time
            if (caster.isUsingItem() && Math.abs(caster.getUseItemRemainingTicks()) >= spell.getCharge()) {
                ItemStack stack = caster.getItemInHand(caster.getUsedItemHand());
                return stack.getItem() instanceof ISpellCastingItem && ((ISpellCastingItem) stack.getItem()).getCurrentSpell(stack) == spell;
            }
        } else if (caster instanceof ISpellCaster spellCaster){
            return spellCaster.getContinuousSpell() == spell;
        }

        return false;
    }
}
