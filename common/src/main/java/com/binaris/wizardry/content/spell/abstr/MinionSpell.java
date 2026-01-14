package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.*;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Base class for spells that summon minions. Handles the common functionality such as spawning the minions, setting
 * their lifetime and applying modifiers. The actual type of minion is specified by a factory function passed to the
 * constructor.
 * <p>
 * Minions are spawned on the ground near the caster by default, but this can be changed to midair by calling
 * {@link #flying(boolean)} with true as the argument. In this case, if no suitable ground position can be found within
 * the summon radius, they will be spawned at y=2 at a random horizontal position within the summon radius instead.
 * <p>
 * The process of creating the minion data and overwriting the original entity logic is handled by the {@link MinionData}
 *
 * @param <T> The type of mob that is summoned by this spell.
 */
public class MinionSpell<T extends Mob> extends Spell {
    /**
     * Attribute Modifier id
     */
    public static final String HEALTH_MODIFIER = "minion_health";

    /**
     * Attribute Modifier id
     */
    public static final String POTENCY_ATTRIBUTE_MODIFIER = "potency";

    /**
     * A factory that creates the minions.
     */
    protected final Function<Level, T> minionFactory;

    /**
     * Whether the minions are spawned in midair. Defaults to false.
     */
    protected boolean flying = false;

    /**
     * When the created minion should follow the owner
     */
    protected boolean shouldFollowOwner = true;

    public MinionSpell(Function<Level, T> minionFactory) {
        this.minionFactory = minionFactory;
    }

    public MinionSpell<T> setShouldFollowOwner(boolean shouldFollowOwner) {
        this.shouldFollowOwner = shouldFollowOwner;
        return this;
    }

    /**
     * Sets whether the minions are spawned in midair.
     *
     * @param flying True to spawn the minions in midair, false to spawn them on the ground.
     * @return The spell instance, allowing this method to be chained onto the constructor.
     */
    public MinionSpell<T> flying(boolean flying) {
        this.flying = flying;
        return this;
    }

    @Override
    public boolean canCastByLocation() {
        return true;
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!extraConditions(ctx, BlockPos.containing(ctx.caster().xo, ctx.caster().yo, ctx.caster().zo))) {
            return false;
        }

        if (!this.spawnMinions(ctx)) {
            return false;
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (!extraConditions(ctx, BlockPos.containing(ctx.caster().xo, ctx.caster().yo, ctx.caster().zo))) {
            return false;
        }

        if (!this.spawnMinions(ctx)) {
            return false;
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        if (!extraConditions(ctx, ctx.pos())) {
            return false;
        }

        if (!ctx.world().isClientSide) {
            for (int i = 0; i < property(DefaultProperties.MINION_COUNT); i++) {
                T minion = minionFactory.apply(ctx.world());
                minion.setPos(ctx.pos().getX() + 0.5, ctx.pos().getY(), ctx.pos().getZ() + 0.5);
                setLifetime(minion, (int) (property(DefaultProperties.MINION_LIFETIME).floatValue() * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));
                this.addMinionExtras(minion, ctx, i);

                ctx.world().addFreshEntity(minion);
            }
        }

        this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(), ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(), ctx.castingTicks(), ctx.duration());
        return true;
    }

    protected boolean spawnMinions(CastContext ctx) {
        if (ctx.world().isClientSide) return true; // No need to go further on the client side

        for (int i = 0; i < property(DefaultProperties.MINION_COUNT); i++) {
            int range = property(DefaultProperties.SUMMON_RADIUS);
            BlockPos pos = BlockUtil.findNearbyFloorSpace(ctx.caster(), range, range * 2);

            if (flying) {
                if (pos != null) {
                    pos = pos.atY(2);
                } else {
                    pos = BlockPos.containing(ctx.caster().position().relative(Direction.NORTH, ctx.world().random.nextInt(range * 2) - range)
                            .relative(Direction.EAST, ctx.world().random.nextInt(range * 2) - range));
                }
            } else {
                if (pos == null) {
                    return false;
                }
            }


            T minion = createMinion(ctx.world(), ctx.caster(), ctx.modifiers());
            minion.setCustomName(Component.translatable("entity.ebwizardry.minion_name", ctx.caster().getDisplayName(), minion.getDisplayName()));


            MinionData data = Services.OBJECT_DATA.getMinionData(minion);
            minion.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            data.setSummoned(true);
            data.setOwnerUUID(ctx.caster().getUUID());
            setLifetime(minion, (int) (property(DefaultProperties.MINION_LIFETIME) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));
            data.setShouldFollowOwner(shouldFollowOwner);

            if (minion.getAttribute(Attributes.ATTACK_DAMAGE) != null)
                minion.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(POTENCY_ATTRIBUTE_MODIFIER, ctx.modifiers().get(SpellModifiers.POTENCY) - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            if (minion.getAttribute(Attributes.MAX_HEALTH) != null)
                minion.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER, ctx.modifiers().get(HEALTH_MODIFIER) - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));

            minion.setHealth(minion.getMaxHealth());
            this.addMinionExtras(minion, ctx, i);
            ctx.world().addFreshEntity(minion);
        }
        return true;
    }

    private void setLifetime(T minion, int lifetime) {
        MinionData data = Services.OBJECT_DATA.getMinionData(minion);
        data.setLifetime(lifetime);
    }


    protected T createMinion(Level world, @Nullable LivingEntity caster, SpellModifiers modifiers) {
        return minionFactory.apply(world);
    }


    protected void addMinionExtras(T minion, CastContext ctx, int alreadySpawned) {
        //minion.spawn(minion.world.getDifficultyForLocation(pos), null);
    }


    protected boolean extraConditions(CastContext ctx, BlockPos containing) {
        return true;
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
