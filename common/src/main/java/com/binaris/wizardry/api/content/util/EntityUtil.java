package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.api.content.entity.living.ISpellCaster;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellContexts;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.content.item.ScrollItem;
import com.binaris.wizardry.content.item.armor.WizardArmorItem;
import com.binaris.wizardry.content.item.armor.WizardArmorMaterial;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.google.common.collect.Streams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class containing helper methods for entity manipulation, checking spell casting, querying entities within physical
 * bounds (radius, range, cylinder), handling player inventories, and performing other utility functions related to Minecraft entities.
 */
public final class EntityUtil {
    /** Array of all armor equipment slots for convenience. */
    public static final EquipmentSlot[] ARMOR_SLOTS;

    static {
        List<EquipmentSlot> slots = new ArrayList<>(Arrays.asList(EquipmentSlot.values()));
        slots.removeIf(slot -> slot.getType() != EquipmentSlot.Type.ARMOR);
        ARMOR_SLOTS = slots.toArray(new EquipmentSlot[0]);
    }

    /**
     * Returns the entity with the given UUID, or null if no such entity exists. Keep in mind that this process is made in the server
     * thread. Using this in common/client expecting a client-side entity will always return null.
     *
     * @param world The world to search in
     * @param id    The UUID of the entity to search for
     * @return The entity with the given UUID, or null if no such entity exists
     */
    @Nullable
    public static Entity getEntityByUUID(Level world, @Nullable UUID id) {
        if (id == null) return null; // It would return null eventually, but there's no point even looking
        if (!(world instanceof ServerLevel serverWorld)) return null;

        for (Entity entity : serverWorld.getAllEntities()) {
            if (entity.getUUID().equals(id)) return entity;
        }
        return null;
    }

    /**
     * Gets all living entities within a cubic bounding box centered on the specified coordinates. The results are filtered to
     * only include entities within the specified range (distance limit).
     *
     * @param world The level to search in.
     * @param x     The X coordinate of the center.
     * @param y     The Y coordinate of the center.
     * @param z     The Z coordinate of the center.
     * @param range The maximum distance from the center.
     * @return A list of living entities in range.
     */
    public static List<LivingEntity> getLivingEntitiesInRange(Level world, double x, double y, double z, double range) {
        return getEntitiesInRange(world, x, y, z, range, LivingEntity.class);
    }

    /**
     * Gets all living entities within a spherical radius around the specified coordinates.
     *
     * @param radius The radius of the sphere.
     * @param x      The X coordinate of the center.
     * @param y      The Y coordinate of the center.
     * @param z      The Z coordinate of the center.
     * @param world  The level to search in.
     * @return A list of living entities within the radius.
     */
    public static List<LivingEntity> getLivingWithinRadius(double radius, double x, double y, double z, Level world) {
        return getEntitiesWithinRadius(radius, x, y, z, world, LivingEntity.class);
    }

    /**
     * Gets all entities of a specific class within a cubic bounding box centered on the specified coordinates, filtered by
     * maximum range.
     *
     * @param <T>         The type of entity to search for.
     * @param world       The level to search in.
     * @param x           The X coordinate of the center.
     * @param y           The Y coordinate of the center.
     * @param z           The Z coordinate of the center.
     * @param range       The maximum distance from the center.
     * @param entityClass The class of the entities to search for.
     * @return A list of entities in range matching the specified class.
     */
    public static <T extends Entity> List<T> getEntitiesInRange(Level world, double x, double y, double z, double range, Class<T> entityClass) {
        AABB boundingBox = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
        Predicate<T> alwaysTrue = entity -> true;

        List<T> entities = world.getEntitiesOfClass(entityClass, boundingBox, alwaysTrue);
        double rangeSq = range * range;
        entities.removeIf(entity -> entity.distanceToSqr(x, y, z) > rangeSq);
        return entities;
    }

    /**
     * Gets all entities of a specific class within a spherical radius around the specified coordinates.
     *
     * @param <T>        The type of entity to search for.
     * @param radius     The radius of the sphere.
     * @param x          The X coordinate of the center.
     * @param y          The Y coordinate of the center.
     * @param z          The Z coordinate of the center.
     * @param world      The level to search in.
     * @param entityType The class of the entities to search for.
     * @return A list of entities of the specified type within the radius.
     */
    public static <T extends Entity> List<T> getEntitiesWithinRadius(double radius, double x, double y, double z, Level world, Class<T> entityType) {
        AABB box = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        List<T> entityList = world.getEntitiesOfClass(entityType, box);
        double radiusSq = radius * radius;
        entityList.removeIf(entity -> entity.distanceToSqr(x, y, z) > radiusSq);
        return entityList;
    }

    /**
     * Hurts the specified entity without causing it to receive knockback.
     *
     * @param entity The entity to damage.
     * @param source The damage source.
     * @param amount The amount of damage to inflict.
     * @return True if the entity was successfully hurt, false otherwise.
     */
    public static boolean attackEntityWithoutKnockback(Entity entity, DamageSource source, float amount) {
        Vec3 originalVec = entity.getDeltaMovement();
        boolean succeeded = entity.hurt(source, amount);
        entity.setDeltaMovement(originalVec);
        return succeeded;
    }

    /**
     * Gets the first rider (passenger) of the specified entity, if any.
     *
     * @param entity The entity to check.
     * @return The first passenger entity, or null if there are no passengers.
     */
    @Nullable
    public static Entity getRider(Entity entity) {
        return !entity.getPassengers().isEmpty() ? entity.getPassengers().get(0) : null;
    }

    /**
     * Finds the nearest space to the specified position that the given entity can teleport to without being inside one or more
     * solid blocks. The search volume is twice the size of the entity's bounding box (meaning that when teleported to the
     * returned position, the original destination remains within the entity's bounding box).
     *
     * @param entity               The entity being teleported
     * @param destination          The target position to search around
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

        Iterable<BlockPos> cuboid = BlockPos.betweenClosed(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ), Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ));

        if (Streams.stream(cuboid).allMatch(b -> world.noCollision(new AABB(b)))) {
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
                    Predicate<BlockPos> nearSolidBlock = b -> b.getX() >= pos.getX() - nx && b.getX() <= pos.getX() + px && b.getY() >= pos.getY() - ny && b.getY() <= pos.getY() + py && b.getZ() >= pos.getZ() - nz && b.getZ() <= pos.getZ() + pz;
                    nearby.removeIf(nearSolidBlock);
                    possiblePositions.removeIf(nearSolidBlock);
                }
            }

            if (possiblePositions.isEmpty()) return null;

            BlockPos nearest = possiblePositions.stream().min(Comparator.comparingDouble(b -> destination.distanceToSqr(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5))).get();

            return VecUtils.getFaceCentre(nearest, Direction.DOWN);
        }
    }


    /**
     * Gets all living entities within a vertical cylinder centered on the specified coordinates.
     *
     * @param radius The radius of the cylinder.
     * @param x      The X coordinate of the center.
     * @param y      The Y coordinate of the bottom of the cylinder.
     * @param z      The Z coordinate of the center.
     * @param height The height of the cylinder.
     * @param world  The level to search in.
     * @return A list of living entities within the cylinder.
     */
    public static List<LivingEntity> getLivingWithinCylinder(double radius, double x, double y, double z, double height, Level world) {
        return getEntitiesWithinCylinder(radius, x, y, z, height, world, LivingEntity.class);
    }

    /**
     * Gets all entities of a specific class within a vertical cylinder centered on the specified coordinates.
     *
     * @param <T>        The type of entity to search for.
     * @param radius     The radius of the cylinder.
     * @param x          The X coordinate of the center.
     * @param y          The Y coordinate of the bottom of the cylinder.
     * @param z          The Z coordinate of the center.
     * @param height     The height of the cylinder.
     * @param world      The level to search in.
     * @param entityType The class of the entities to search for.
     * @return A list of entities of the specified type within the cylinder.
     */
    public static <T extends Entity> List<T> getEntitiesWithinCylinder(double radius, double x, double y, double z, double height, Level world, Class<T> entityType) {
        AABB aabb = new AABB(x - radius, y, z - radius, x + radius, y + height, z + radius);
        List<T> entityList = world.getEntitiesOfClass(entityType, aabb);
        double radiusSq = radius * radius;
        entityList.removeIf(entity -> entity.distanceToSqr(x, entity.yo, z) > radiusSq);
        return entityList;
    }

    /**
     * Determines whether the given entity is allowed to damage blocks in the given world, this doesn't check specific block
     * properties, just whether the entity in general can damage blocks.
     *
     * @param entity The entity to check.
     * @param world  The world in which the entity is attempting to damage blocks.
     * @return True if the entity can damage blocks, false otherwise.
     */
    public static boolean canDamageBlocks(LivingEntity entity, Level world) {
        if (entity instanceof Player player) {
            return player.mayBuild() && !player.isSpectator();
        }

        if (entity instanceof Mob mob) {
            return !Services.PLATFORM.fireMobBlockBreakEvent(world, null, mob);
        }

        // Non-player / non-mob entities cannot damage blocks
        return false;
    }

    /**
     * Gets the default aiming error angle (in degrees) for a projectile cast by an NPC based on the level's current difficulty.
     *
     * @param difficulty The difficulty of the level.
     * @return The aiming error value.
     */
    public static int getDefaultAimingError(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 5;
            case NORMAL -> 3;
            case HARD -> 0;
            default -> 4;
        };
    }

    /**
     * Plays a sound at the specified player's position, audible to everyone.
     *
     * @param player The player whose position to play the sound at.
     * @param sound  The sound event to play.
     * @param volume The volume of the sound.
     * @param pitch  The pitch of the sound.
     */
    public static void playSoundAtPlayer(Player player, SoundEvent sound, float volume, float pitch) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
    }

    /**
     * Checks whether the given living entity is currently casting the specified spell.
     *
     * @param caster The entity to check.
     * @param spell  The spell to check.
     * @return True if the entity is casting the spell, false otherwise.
     */
    public static boolean isCasting(LivingEntity caster, Spell spell) {
        if (spell.isInstantCast()) return false;

        if (caster instanceof ISpellCaster spellCaster) {
            return spellCaster.getContinuousSpell() == spell;
        }

        // Player checking now
        if (!(caster instanceof Player)) return false;
        if (!caster.isUsingItem()) return false;

        ItemStack stack = caster.getItemInHand(caster.getUsedItemHand());
        boolean isSpellCastingItem = stack.getItem() instanceof ICastItem;
        if (!isSpellCastingItem) return false;
        Spell currentSpell = ((ICastItem) stack.getItem()).getCurrentSpell(stack);

        if (stack.getItem() instanceof ScrollItem) {
            return currentSpell == spell;
        }

        int ticksInUse = caster.getUseItem().getUseDuration() - caster.getUseItemRemainingTicks();
        if (ticksInUse >= spell.getChargeUp()) {
            return currentSpell == spell;
        }

        return false;
    }


    /**
     * Adds n random spells to the given list. The spells will be of the given element if possible. Extracted as a
     * separate function since it was the same in both EntityWizard and EntityEvilWizard.
     *
     * @param spells The spell list to be populated.
     * @param e      The element that the spells should belong to, or {@link Elements#MAGIC} for a random element each time.
     * @param master Whether to include master spells.
     * @param n      The number of spells to add.
     * @param random A random number generator to use.
     * @return The tier of the highest-tier spell that was added to the list.
     *
     */
    public static SpellTier populateSpells(List<Spell> spells, Element e, boolean master, int n, RandomSource random) {
        // This is the tier of the highest tier spell added, novice only at the start
        SpellTier maxTier = SpellTiers.NOVICE;

        List<Spell> npcSpells = RegistryUtils.getSpells(Spell::canCastByEntity);

        for (int i = 0; i < n; i++) {
            SpellTier tier;
            Element element = e == Elements.MAGIC ? RegistryUtils.getRandomElement(random) : e;

            int randomizer = random.nextInt(20);
            if (randomizer < 10) tier = SpellTiers.NOVICE;
            else if (randomizer < 16) tier = SpellTiers.APPRENTICE;
            else if (randomizer < 19 || !master) tier = SpellTiers.ADVANCED;
            else tier = SpellTiers.MASTER;
            if (tier.getLevel() > maxTier.getLevel()) maxTier = tier;

            List<Spell> list = RegistryUtils.getSpells(spell -> spell.getTier() == tier && spell.getElement() == element && spell.canCastByEntity() && spell.isEnabled(SpellContexts.NPCS));

            list.retainAll(npcSpells);
            list.removeAll(spells);

            if (list.isEmpty()) {
                list = npcSpells;
                list.removeAll(spells);
            }
            if (!list.isEmpty()) spells.add(list.get(random.nextInt(list.size())));
        }
        return maxTier;
    }

    /**
     * Finds and returns the casting item (e.g. wand) currently being used or held by the player. Checks the main hand first,
     * then falls back to the offhand.
     *
     * @param player The player to check.
     * @return The ItemStack of the wand/casting item in use, or {@link ItemStack#EMPTY} if none is found.
     */
    public static @NotNull ItemStack getWandInUse(Player player) {
        ItemStack wand = player.getMainHandItem();

        if (!(wand.getItem() instanceof ICastItem) || ((ICastItem) wand.getItem()).getSpells(wand).length < 2) {
            wand = player.getOffhandItem();
            if (!(wand.getItem() instanceof ICastItem) || ((ICastItem) wand.getItem()).getSpells(wand).length < 2) return ItemStack.EMPTY;
        }

        return wand;
    }

    /**
     * Gets all items in the player's inventory. (This does not include the carried item (cursor item when inventory is open).)
     * Check {@link #getAllItemsIncludingCarried(Player)} for a version that includes the carried item.
     *
     * @param player The player to get the items from.
     * @return A collection of all items in the player's inventory.
     */
    public static Collection<ItemStack> getAllItems(Player player) {
        // This could also be {@code Player#compartments}!! But its private :(
        List<ItemStack> items = new ArrayList<>();
        items.addAll(player.getInventory().items);
        items.addAll(player.getInventory().armor);
        items.addAll(player.getInventory().offhand);
        return items;
    }

    /**
     * Gets all items including the carried item (cursor item when inventory is open).
     * This is important for systems that need to track ALL items a player has access to.
     *
     * @param player The player to get the items from.
     * @return A collection of all items in the player's inventory, including the carried item.
     */
    public static Collection<ItemStack> getAllItemsIncludingCarried(Player player) {
        List<ItemStack> items = new ArrayList<>(getAllItems(player));
        ItemStack carried = player.containerMenu.getCarried();
        if (!carried.isEmpty()) {
            items.add(carried);
        }
        return items;
    }

    /**
     * Gets the player's hotbar and offhand/mainhand items.
     *
     * @param player The player to get the items from.
     * @return A list of the player's hotbar and offhand slots.
     */
    public static List<ItemStack> getHotBarAndHandItems(Player player) {
        List<ItemStack> hotbar = getHotbarItems(player);
        hotbar.add(0, player.getOffhandItem());
        hotbar.remove(player.getMainHandItem());
        hotbar.add(0, player.getMainHandItem());
        return hotbar;
    }

    /**
     * Search the player's inventory for a specific item.
     *
     * @param player The player to search.
     * @param item   The item to search for.
     * @return True if the player has the item, false otherwise.
     */
    public static boolean doesPlayerHaveItem(Player player, Item item) {
        for (ItemStack stack : getAllItems(player)) {
            if (stack != null && stack.is(item)) return true;
        }
        return false;
    }

    /**
     * Gets the player's hotbar items. (If you want to include the hands use {@link #getHotBarAndHandItems(Player)} (Player)})
     *
     * @param player The player to get the items from.
     * @return A list of the player's hotbar slots.
     */
    public static List<ItemStack> getHotbarItems(Player player) {
        NonNullList<ItemStack> hotBar = NonNullList.create();
        hotBar.addAll(player.getInventory().items.subList(0, 9));
        return hotBar;
    }

    /**
     * Checks if the entity has a full set of the given element and armor type. (it could be null in case you want the default
     * armors without a specific element or armor type)
     *
     * @param entity  The entity to check.
     * @param element The element of the armor.
     * @param armor   The type of the armor.
     * @return True if the entity has a full set of the given element and armor type, false otherwise.
     */
    public static boolean isWearingFullMagicArmorSet(LivingEntity entity, @Nullable Element element, @Nullable WizardArmorMaterial armor) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof WizardArmorItem wizardArmor)) return false;

        Element e = element == null ? wizardArmor.getElement() : element;
        WizardArmorMaterial ac = armor == null ? wizardArmor.getWizardArmorType() : armor;
        return Arrays.stream(ARMOR_SLOTS).allMatch(slot -> entity.getItemBySlot(slot).getItem() instanceof WizardArmorItem armor2 && armor2.getElement() == e && armor2.getWizardArmorType() == ac);
    }

    /**
     * Checks if all the armor pieces the entity is wearing have mana.
     *
     * @param entity The entity to check.
     * @return True if all the armor pieces the entity is wearing have mana, false otherwise.
     */
    public static boolean doAllArmorPiecesHaveMana(LivingEntity entity) {
        return Arrays.stream(ARMOR_SLOTS).noneMatch(s -> entity.getItemBySlot(s).getItem() instanceof IManaItem manaStoringItem && manaStoringItem.isManaEmpty(entity.getItemBySlot(s)));
    }

    private EntityUtil() {
        // Prevent instantiation
    }
}
