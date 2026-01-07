package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.ArcaneLockData;
import com.electroblob.wizardry.api.content.data.ContainmentData;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.content.block.RunestonePedestalBlock;
import com.electroblob.wizardry.content.entity.living.EvilWizard;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.mixin.accessor.RCBEAccessor;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RunestonePedestalBlockEntity extends BlockEntity {
    /** Radius around the pedestal to check for nearby players to activate the event */
    private static final double ACTIVATION_RADIUS = 7;
    /** Number of evil wizards to spawn when the pedestal event is activated */
    private static final int WIZARD_SPAWN_COUNT = 3;
    /** Radius around the pedestal to spawn evil wizards */
    private static final int WIZARD_SPAWN_RADIUS = 5;
    /** Delay before the pedestal regenerates after being conquered (in ticks) */
    private static final int REGENERATION_DELAY_TICKS = 72000; // 1 hour in ticks

    /**
     * BlockPos of the linked container (chest, barrel, etc.) above the pedestal, this could be null if the logic didn't
     * find a {@code RandomizableContainerBlockEntity} with a loot table above the pedestal.
     */
    private @Nullable BlockPos linkedPos;

    /**
     * Whether this pedestal was naturally generated. Used for special functionality.
     * If false, the block entity will be deleted automatically on the tick.
     */
    private boolean natural;

    /**
     * Whether the pedestal has been activated (i.e. players are nearby and event has started), Spawning evil wizards
     * and applying containment effect to nearby players.
     */
    private boolean activated;

    /**
     * Whether the pedestal event has been conquered (i.e. all evil wizards have been defeated), players are released
     * from containment and the pedestal is set to regenerate after a delay.
     */
    private boolean conquered;

    /**
     * Game time at which the pedestal will regenerate if conquered is true. Used to schedule regeneration of the
     * pedestal and its linked container.
     */
    private long regenerationTime;

    /**
     * List of UUIDs of spawned evil wizards for this pedestal event, used to check if they are alive and manage the event state,
     * if all wizards are dead, the players will be released from containment and finish the event.
     */
    private final List<UUID> spawnedWizards;

    /**
     * List of UUIDs of players currently affected by the containment effect from this pedestal. Used to apply the effect
     * and release them when the event ends.
     */
    private final List<UUID> playersInContainment;

    public RunestonePedestalBlockEntity(BlockPos pos, BlockState blockState) {
        super(EBBlockEntities.RUNESTONE_PEDESTAL.get(), pos, blockState);
        this.spawnedWizards = new ArrayList<>();
        this.playersInContainment = new ArrayList<>();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T entity) {
        if (!(entity instanceof RunestonePedestalBlockEntity pedestal) || level == null || level.isClientSide) return;

        if (pedestal.conquered && EBConfig.shrineRegenerationEnabled && pedestal.regenerationTime > 0) {
            if (level.getGameTime() >= pedestal.regenerationTime) {
                regenerate(pedestal, pos);
                return;
            }
        }

        if (!pedestal.natural) {
            level.removeBlockEntity(pos);
            return;
        }

        if (pedestal.linkedPos == null) {
            pedestal.tryLinkContainer(pos);
            if (!pedestal.natural) return;
        }

        long gameTime = level.getGameTime();
        if (!pedestal.activated && gameTime % 20 == 0) pedestal.checkEvent(pos);
        if (pedestal.activated) pedestal.containmentEffect();
        if (pedestal.activated && !pedestal.playersInContainment.isEmpty()) pedestal.checkWizardsAlive();
    }

    private static void regenerate(RunestonePedestalBlockEntity pedestal, BlockPos pos) {
        pedestal.natural = true;
        pedestal.activated = false;
        pedestal.conquered = false;

        BlockPos chestPos = pos.above();
        BlockEntity blockEntity = pedestal.level.getBlockEntity(chestPos);

        if (!(blockEntity instanceof RandomizableContainerBlockEntity container)) {
            pedestal.level.destroyBlock(chestPos, false);
            pedestal.level.setBlockAndUpdate(chestPos, Blocks.CHEST.defaultBlockState());
            regenerate(pedestal, pos);
            return;
        }

        container.setLootTable(EBLootTables.SHRINE, pedestal.level.getRandom().nextLong());
        ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(container);
        data.setArcaneLockOwner(UUID.randomUUID().toString());
        pedestal.sync();
    }

    private void tryLinkContainer(BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos.above());

        if (!(blockEntity instanceof RandomizableContainerBlockEntity container) || ((RCBEAccessor) container).getLootTable() == null) {
            EBLogger.warn("Runestone Pedestal at {} is marked as natural but has no valid container block entity above it, check the structure and try to have a container block above it", pos);
            setNatural(false);
            return;
        }

        ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(container);
        data.setArcaneLockOwner(UUID.randomUUID().toString());
        setLinkedPos(pos.above());
    }

    private void checkEvent(BlockPos pos) {
        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(ACTIVATION_RADIUS));
        nearbyPlayers.removeIf(player -> player.isSpectator() || !player.isAlive());
        if (nearbyPlayers.isEmpty()) return;

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        ParticleBuilder.create(EBParticles.SPHERE).pos(x, y + 1, z).color(0xf06495).scale(5).time(12).allowServer(true).spawn(level);
        level.playSound(null, x, y, z, EBSounds.BLOCK_PEDESTAL_ACTIVATE.get(), SoundSource.BLOCKS, 1.5f, 1);

        this.activated = true;
        this.spawnedWizards.clear();
        playersInContainment.addAll(nearbyPlayers.stream().map(Player::getUUID).toList());
        containmentEffect();
        spawnEvilWizards();
        sync();
    }

    /**
     * Spawns evil wizards around the pedestal at random positions within a defined radius, they are assigned an element
     * based on the pedestal's runestone element. The UUIDs of the spawned wizards are stored for event management.
     */
    private void spawnEvilWizards() {
        if (level.isClientSide) return;

        for (int i = 0; i < WIZARD_SPAWN_COUNT; i++) {
            EvilWizard wizard = new EvilWizard(level);
            BlockPos spawnPos = findSpawnPositionWizard(level.random.nextFloat() * 2 * (float) Math.PI);
            wizard.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            wizard.setElement(getBlockState().getBlock() instanceof RunestonePedestalBlock runestone ? runestone.getElement() : Elements.FIRE);
            wizard.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(getBlockPos()), MobSpawnType.STRUCTURE, null, null);
            level.addFreshEntity(wizard);
            spawnedWizards.add(wizard.getUUID());
        }
    }

    /** Applies the containment effect to all players and wizards within the containment area. */
    private void containmentEffect() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        playersInContainment.removeIf(uuid -> {
            Player player = serverLevel.getPlayerByUUID(uuid);
            if (player == null || !player.isAlive()) return true;
            setContainmentPos(player);
            player.addEffect(new MobEffectInstance(EBMobEffects.CONTAINMENT.get(), 200, 0, false, false, true));
            return false;
        });

        spawnedWizards.removeIf(uuid -> {
            var entity = serverLevel.getEntity(uuid);
            if (entity == null || !entity.isAlive()) return true;
            if (entity instanceof LivingEntity livingEntity) {
                setContainmentPos(livingEntity);
                livingEntity.addEffect(new MobEffectInstance(EBMobEffects.CONTAINMENT.get(), 200, 0, false, false, true));
            }
            return false;
        });
    }

    /**
     * Checks if all spawned evil wizards are dead, if so, calls {@link #conquered()} to release players from containment
     * and end the event.
     */
    private void checkWizardsAlive() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        spawnedWizards.removeIf(uuid -> {
            var entity = serverLevel.getEntity(uuid);
            return entity == null || !entity.isAlive();
        });


        if (spawnedWizards.isEmpty()) {
            boolean playersEmpty = playersInContainment.isEmpty();
            conquered();
            // After conquering, if there are no players left in containment, regenerate immediately
            // this happens if all players disconnect or die during the event, for example
            if (playersEmpty) regenerate(this, getBlockPos());
        }
    }

    /**
     * Handles the logic for when the pedestal event is conquered, releasing players from containment,
     * playing particle effects, and resetting the pedestal state for regeneration.
     */
    private void conquered() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        serverLevel.playSound(null, getBlockPos(), EBSounds.BLOCK_PEDESTAL_CONQUER.get(), SoundSource.BLOCKS, 1.5f, 1);

        playersInContainment.forEach(uuid -> {
            var player = serverLevel.getPlayerByUUID(uuid);
            if (player != null) player.removeEffect(EBMobEffects.CONTAINMENT.get());
        });
        playersInContainment.clear();

        spawnConqueredParticles();
        Services.OBJECT_DATA.getArcaneLockData(level.getBlockEntity(linkedPos)).clearArcaneLockOwner();

        conquered = true;
        regenerationTime = level.getGameTime() + REGENERATION_DELAY_TICKS;
        sync();
    }

    private void spawnConqueredParticles() {
        double x = getBlockPos().getX() + 0.5;
        double y = getBlockPos().getY() + 0.5;
        double z = getBlockPos().getZ() + 0.5;

        ParticleBuilder.create(EBParticles.SPHERE).scale(5).pos(x, y + 1, z).color(0xf06495).time(12).allowServer(true).spawn(level);
        for (int i = 0; i < 5; i++) {
            float brightness = 0.8f + level.random.nextFloat() * 0.2f;
            ParticleBuilder.create(EBParticles.SPARKLE, level.random, x, y + 1, z, 1, true)
                    .color(1, brightness, brightness).allowServer(true).spawn(level);
        }
    }

    /**
     * Finds a suitable spawn position for an evil wizard around the pedestal based on a given angle.
     *
     * @param angle Angle in radians to determine the spawn position around the pedestal.
     * @return BlockPos representing the spawn position for the evil wizard.
     */
    private BlockPos findSpawnPositionWizard(float angle) {
        int x = (int) (getBlockPos().getX() + 0.5 + WIZARD_SPAWN_RADIUS * Mth.sin(angle));
        int z = (int) (getBlockPos().getZ() + 0.5 + WIZARD_SPAWN_RADIUS * Mth.cos(angle));
        Integer y = BlockUtil.getNearestFloor(level, new BlockPos(x, getBlockPos().getY(), z), 8);
        return y != null ? new BlockPos(x, y, z) : getBlockPos().offset(1, 0, 0);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (linkedPos != null) tag.put("LinkedPos", NbtUtils.writeBlockPos(linkedPos));
        tag.putBoolean("Natural", natural);
        tag.putBoolean("Activated", activated);
        tag.putBoolean("Conquered", conquered);
        tag.putLong("RegenerationTime", regenerationTime);
        saveUUIDList(tag, "SpawnedWizards", spawnedWizards);
        saveUUIDList(tag, "PlayersInContainment", playersInContainment);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.linkedPos = tag.contains("LinkedPos") ? NbtUtils.readBlockPos(tag.getCompound("LinkedPos")) : null;
        this.natural = tag.getBoolean("Natural");
        this.activated = tag.getBoolean("Activated");
        this.conquered = tag.getBoolean("Conquered");
        this.regenerationTime = tag.getLong("RegenerationTime");
        loadUUIDList(tag, "SpawnedWizards", spawnedWizards);
        loadUUIDList(tag, "PlayersInContainment", playersInContainment);
    }

    private void saveUUIDList(CompoundTag tag, String key, List<UUID> list) {
        if (list.isEmpty()) return;
        ListTag listTag = new ListTag();
        for (UUID uuid : list) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putUUID("UUID", uuid);
            listTag.add(uuidTag);
        }
        tag.put(key, listTag);
    }

    private void loadUUIDList(CompoundTag tag, String key, List<UUID> list) {
        list.clear();
        if (!tag.contains(key)) return;
        ListTag listTag = tag.getList(key, 10);
        for (int i = 0; i < listTag.size(); i++) {
            list.add(listTag.getCompound(i).getUUID("UUID"));
        }
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    /**
     * All players and wizards affected by the containment effect have their containment position set to this pedestal's
     * position, avoiding the problem of them having different containment origins depending on where they were when the
     * effect was applied.
     */
    private void setContainmentPos(LivingEntity entity) {
        ContainmentData data = Services.OBJECT_DATA.getContainmentData(entity);
        data.setContainmentPos(this.worldPosition);
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void setLinkedPos(@Nullable BlockPos pos) {
        this.linkedPos = pos;
        sync();
    }

    public void setNatural(boolean natural) {
        this.natural = natural;
        sync();
    }

    public boolean isNatural() {
        return natural;
    }
}