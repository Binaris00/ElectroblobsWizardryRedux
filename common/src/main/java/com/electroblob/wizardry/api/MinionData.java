package com.electroblob.wizardry.api;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.event.EBEntityJoinLevelEvent;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.api.content.event.EBPlayerInteractEntityEvent;
import com.electroblob.wizardry.core.mixin.accessor.MobGoalsAccessor;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

/**
 * This class is used to store data about minions summoned by the player. Each minion(Mob) has a {@link MinionData}
 * instance associated with it which stores its lifetime and whether it was summoned by a player. The lifetime is compared
 * to the with the minion's tick count to determine when it should be removed. If the lifetime is -1, the minion will
 * persist until it is killed or dismissed by other means.
 * <p>
 * Minions are identified by checking by {@link #isMinion(Entity)} which checks if the entity is a Mob and if it has
 * been marked as summoned.
 * <p>
 * Note that this class does not handle saving/loading of the minion data; this must be done separately using the
 * {@link #serializeNBT(CompoundTag)} and {@link #deserializeNBT(CompoundTag)} methods on each mod-loader (As an Addon
 * dev you should worry about this part).
 */
public class MinionData {
    private final Mob minion;
    private int lifetime = -1;
    private boolean summoned = false;

    public MinionData(Mob minion) {
        this.minion = minion;
    }

    public static boolean isMinion(Entity entity) {
        return entity instanceof Mob && Services.WIZARD_DATA.getMinionData((Mob) entity).summoned;
    }

    public void updateGoals() {
        ((MobGoalsAccessor) minion).getTargetSelector().removeAllGoals((goal) -> true);
        ((MobGoalsAccessor) minion).getGoalSelector().removeAllGoals((goal) -> true);
    }

    public void tick() {
        if (!summoned) return;

        if (minion.tickCount > this.getLifetime() && this.getLifetime() > 0) {
            // TODO should add event when minion is dismissed this way
            this.minion.discard();
        }

        if (minion.level().isClientSide && minion.level().random.nextInt(8) == 0) {
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(minion.xo, minion.yo + (minion.level().random.nextDouble() * 1.5 + 1), minion.zo).color(0.1f, 0.0f, 0.0f).spawn(minion.level());
        }
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
        Services.WIZARD_DATA.onMinionDataUpdate(this, minion);
    }

    public boolean isSummoned() {
        return summoned;
    }

    public void summoned(boolean summoned) {
        this.summoned = summoned;
        Services.WIZARD_DATA.onMinionDataUpdate(this, minion);
    }

    public @Nullable Mob getMinion() {
        return minion;
    }

    public CompoundTag serializeNBT(CompoundTag tag) {
        tag.putInt("lifetime", lifetime);
        tag.putBoolean("summoned", summoned);
        return tag;
    }

    public MinionData deserializeNBT(CompoundTag tag) {
        this.lifetime = tag.getInt("lifetime");
        this.summoned = tag.getBoolean("summoned");
        return this;
    }

    public void copyFrom(MinionData data) {
        this.lifetime = data.lifetime;
        this.summoned = data.summoned;
    }

    public static void onLivingTick(EBLivingTick event) {
        if (event.getEntity() instanceof Mob mob && isMinion(mob)) Services.WIZARD_DATA.getMinionData(mob).tick();
    }

    public static void onEntityJoinLevel(EBEntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Mob mob && isMinion(mob))
            Services.WIZARD_DATA.getMinionData(mob).updateGoals();
    }

    public static void onPlayerInteractEntity(EBPlayerInteractEntityEvent event) {
        if (event.getTarget() instanceof Mob mob && isMinion(mob)) {
            event.setCanceled(true);
        }
    }
}