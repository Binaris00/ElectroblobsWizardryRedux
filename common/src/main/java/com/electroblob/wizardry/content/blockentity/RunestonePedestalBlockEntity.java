package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.ArcaneLockData;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.content.block.RunestonePedestalBlock;
import com.electroblob.wizardry.content.entity.living.EvilWizard;
import com.electroblob.wizardry.core.mixin.accessor.RCBEAccessor;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Elements;
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
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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
    // If there's a player within this radius, the pedestal activates its event
    private static final double ACTIVATION_RADIUS = 7;
    // Number of evil wizards to spawn
    private static final int WIZARD_SPAWN_COUNT = 4;
    // Radius around the pedestal to spawn evil wizards
    private static final int WIZARD_SPAWN_RADIUS = 5;

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
        this.linkedPos = null;
        this.natural = false;
        this.activated = false;
        this.spawnedWizards = new ArrayList<>();
        this.playersInContainment = new ArrayList<>();
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T entity) {
        if (!(entity instanceof RunestonePedestalBlockEntity pedestal)) return;
        if (level == null || level.isClientSide) return;


        // delete block entity (we want to only the natural ones to have functionality)
        if (!pedestal.natural) {
            level.removeBlockEntity(pos);
            return;
        }

        // Linking logic - only if not yet linked
        if (pedestal.linkedPos == null) {
            BlockPos abovePos = pos.above();
            BlockEntity blockEntity = level.getBlockEntity(abovePos);

            if (blockEntity instanceof RandomizableContainerBlockEntity container) {
                if (((RCBEAccessor) container).getLootTable() != null) {
                    ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(container);
                    data.setArcaneLockOwner(UUID.randomUUID().toString()); // No player owns this lock :)
                    pedestal.setLinkedPos(abovePos);
                }
            } else {
                EBLogger.warn("Runestone Pedestal at {} is marked as natural but has no valid container block entity above it, check the structure and try to have a container block above it", pos);
                pedestal.setNatural(false); // No valid container above, so this pedestal is no longer natural
                return;
            }
        }

        // Event system - only if natural and not yet activated
        // Check for nearby players
        if (!pedestal.activated && level.getGameTime() % 20 == 0) {
            AABB detectionBox = new AABB(pos).inflate(ACTIVATION_RADIUS);
            List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, detectionBox);
            if (nearbyPlayers.isEmpty()) return;
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            ParticleBuilder.create(EBParticles.SPHERE).pos(x, y + 1, z).color(0xf06495).scale(5)
                    .time(12).allowServer(true).spawn(level);
            pedestal.activateEvent(nearbyPlayers);
            level.playLocalSound(x, y, z, EBSounds.BLOCK_PEDESTAL_ACTIVATE.get(), SoundSource.BLOCKS, 1.5f, 1, false);
        }

        // if activated, check the nearby players and apply containment effect
        if (pedestal.activated && level.getGameTime() % 40 == 0) pedestal.containmentEffect();

        // Check if all wizards are dead to release the player
        if (pedestal.activated && !pedestal.playersInContainment.isEmpty()) pedestal.checkWizardsAlive();
    }

    /**
     * Activates the pedestal event, spawning evil wizards and applying containment effect to nearby players, also sets
     * the activated flag to true.
     *
     * @param players List of players to apply the containment effect to.
     */
    private void activateEvent(List<Player> players) {
        this.activated = true;
        this.spawnedWizards.clear();
        playersInContainment.addAll(players.stream().map(Player::getUUID).toList());
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
            float angle = level.random.nextFloat() * 2 * (float) Math.PI;
            BlockPos spawnPos = findSpawnPositionWizard(angle);
            wizard.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            wizard.setElement(getBlockState().getBlock() instanceof RunestonePedestalBlock runestone
                    ? runestone.getElement() : Elements.FIRE);
            wizard.setShrinePosition(getBlockPos());
            wizard.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(getBlockPos()), MobSpawnType.STRUCTURE, null, null);
            level.addFreshEntity(wizard);
            spawnedWizards.add(wizard.getUUID());
        }
    }

    /**
     * Applies the containment effect to all players currently in the containment list, refreshing the effect duration.
     */
    private void containmentEffect() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        playersInContainment.removeIf(uuid -> {
            Player player = serverLevel.getPlayerByUUID(uuid);
            if (player == null || !player.isAlive()) return true;
            player.addEffect(new MobEffectInstance(EBMobEffects.CONTAINMENT.get(), 80));
            return false;
        });
    }


    /**
     * Checks if all spawned evil wizards are dead, if so, releases the players from containment and deactivates the pedestal.
     */
    private void checkWizardsAlive() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // Remove dead wizards from the list
        spawnedWizards.removeIf(uuid -> {
            var entity = serverLevel.getEntity(uuid);
            return entity == null || !entity.isAlive();
        });

        // If there are still wizards alive or no players in containment, do nothing
        if (!spawnedWizards.isEmpty() || playersInContainment.isEmpty()) return;

        playersInContainment.forEach(playerUUID -> {
            var player = serverLevel.getPlayerByUUID(playerUUID);
            if (player != null) player.removeEffect(EBMobEffects.CONTAINMENT.get());
        });
        playersInContainment.clear();

        double x = getBlockPos().getX() + 0.5;
        double y = getBlockPos().getY() + 0.5;
        double z = getBlockPos().getZ() + 0.5;
        ParticleBuilder.create(EBParticles.SPHERE).scale(5).pos(x, y + 1, z).color(0xf06495)
                .time(12).allowServer(true).spawn(level);
        for (int i = 0; i < 5; i++) {
            float brightness = 0.8f + level.random.nextFloat() * 0.2f;
            ParticleBuilder.create(EBParticles.SPARKLE, level.random, x, y + 1, z, 1, true)
                    .color(1, brightness, brightness).allowServer(true).spawn(level);
        }

        ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(level.getBlockEntity(linkedPos));
        data.clearArcaneLockOwner();
        setNatural(false); // This pedestal is now inactive and the block entity will be deleted
        sync();
    }

    /**
     * Finds a suitable spawn position for an evil wizard around the pedestal based on a given angle.
     *
     * @param angle Angle in radians to determine the spawn position around the pedestal.
     * @return BlockPos representing the spawn position for the evil wizard.
     */
    private BlockPos findSpawnPositionWizard(float angle) {
        double x = getBlockPos().getX() + 0.5 + WIZARD_SPAWN_RADIUS * Mth.sin(angle);
        double z = getBlockPos().getZ() + 0.5 + WIZARD_SPAWN_RADIUS * Mth.cos(angle);
        Integer y = BlockUtil.getNearestFloor(level, new BlockPos((int) x, getBlockPos().getY(), (int) z), 8);

        return y != null ? new BlockPos((int) x, y, (int) z)
                : getBlockPos().offset(1, 0, 0);
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (linkedPos != null) {
            tag.put("LinkedPos", NbtUtils.writeBlockPos(linkedPos));
        }
        tag.putBoolean("Natural", natural);
        tag.putBoolean("Activated", activated);

        if (!spawnedWizards.isEmpty()) {
            ListTag wizardList = new ListTag();
            for (UUID uuid : spawnedWizards) {
                CompoundTag uuidTag = new CompoundTag();
                uuidTag.putUUID("UUID", uuid);
                wizardList.add(uuidTag);
            }
            tag.put("SpawnedWizards", wizardList);
        }

        if (!playersInContainment.isEmpty()) {
            ListTag playerList = new ListTag();
            for (UUID uuid : playersInContainment) {
                CompoundTag uuidTag = new CompoundTag();
                uuidTag.putUUID("UUID", uuid);
                playerList.add(uuidTag);
            }
            tag.put("PlayersInContainment", playerList);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("LinkedPos")) {
            this.linkedPos = NbtUtils.readBlockPos(tag.getCompound("LinkedPos"));
        } else {
            this.linkedPos = null;
        }
        this.natural = tag.getBoolean("Natural");
        this.activated = tag.getBoolean("Activated");

        this.spawnedWizards.clear();
        if (tag.contains("SpawnedWizards")) {
            ListTag wizardList = tag.getList("SpawnedWizards", 10);
            for (int i = 0; i < wizardList.size(); i++) {
                CompoundTag uuidTag = wizardList.getCompound(i);
                this.spawnedWizards.add(uuidTag.getUUID("UUID"));
            }
        }

        this.playersInContainment.clear();
        if (tag.contains("PlayersInContainment")) {
            ListTag playerList = tag.getList("PlayersInContainment", 10);
            for (int i = 0; i < playerList.size(); i++) {
                CompoundTag uuidTag = playerList.getCompound(i);
                this.playersInContainment.add(uuidTag.getUUID("UUID"));
            }
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