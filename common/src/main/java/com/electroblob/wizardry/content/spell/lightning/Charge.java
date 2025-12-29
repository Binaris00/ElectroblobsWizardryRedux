package com.electroblob.wizardry.content.spell.lightning;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.ISpellVar;
import com.electroblob.wizardry.api.content.data.Persistence;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.MagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Charge extends Spell {
    public static final ISpellVar<SpellModifiers> CHARGE_MODIFIERS = new ISpellVar.SpellVar<>(Persistence.NEVER);
    public static final SpellProperty<Float> CHARGE_SPEED = SpellProperty.floatProperty("charge_speed");
    public static final ISpellVar<Integer> CHARGE_TIME = new ISpellVar.SpellVar<Integer>(Persistence.NEVER).withTicker(Charge::update);

    public Charge() {
        this.soundValues(0.6f, 1, 0);
    }

    private static int update(Player player, Integer chargeTime) {
        if (chargeTime == null) chargeTime = 0;

        if (chargeTime > 0 && !player.level().isClientSide) {
            SpellModifiers modifiers = Services.OBJECT_DATA.getSpellManagerData(player).getVariable(CHARGE_MODIFIERS);
            if (modifiers == null) modifiers = new SpellModifiers();

            Vec3 look = player.getLookAngle();

            float speed = Spells.CHARGE.property(Charge.CHARGE_SPEED) * modifiers.get(EBItems.RANGE_UPGRADE.get());

            player.setDeltaMovement(look.x * speed, player.getDeltaMovement().y, look.z * speed);

            if (player.level().isClientSide) {
                for (int i = 0; i < 5; i++) {
                    ParticleBuilder.create(EBParticles.SPARK, player).spawn(player.level());
                }
            }

            List<LivingEntity> collided = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(1));

            collided.remove(player);
            float damage = Spells.CHARGE.property(DefaultProperties.DAMAGE) * modifiers.get(SpellModifiers.POTENCY);
            float knockback = Spells.CHARGE.property(DefaultProperties.KNOCKBACK);

            collided.forEach(e -> e.hurt(MagicDamageSource.causeDirectMagicDamage(player, EBDamageSources.SHOCK), damage));
            collided.forEach(e -> e.push(player.getDeltaMovement().x * knockback, player.getDeltaMovement().y * knockback + 0.3f, player.getDeltaMovement().z * knockback));

            if (player.level().isClientSide)
                player.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, player.getX() + player.getDeltaMovement().x, player.getY() + player.getBbHeight() / 2, player.getZ() + player.getDeltaMovement().z, 0, 0, 0);

            if (collided.isEmpty()) chargeTime--;
            else {
                EntityUtil.playSoundAtPlayer(player, SoundEvents.GENERIC_HURT, 1, 1);
                chargeTime = 0;
            }
            player.hurtMarked = true;
        }
        return chargeTime;
    }

    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (event.isCanceled()) return;

        if (event.getDamagedEntity() instanceof Player player && event.getSource().getEntity() instanceof LivingEntity attacker) {
            SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
            Integer chargeTime = data.getVariable(CHARGE_TIME);

            if (chargeTime != null && chargeTime > 0 && player.getBoundingBox().inflate(1).intersects(attacker.getBoundingBox())) {
                event.setCanceled(true);
            }

        }
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(ctx.caster());
        data.setVariable(CHARGE_TIME, (int) (property(DefaultProperties.DURATION).floatValue() * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));
        data.setVariable(CHARGE_MODIFIERS, ctx.modifiers());

        if (ctx.world().isClientSide)
            ctx.world().addParticle(ParticleTypes.EXPLOSION_EMITTER, ctx.caster().getX(), ctx.caster().getY() + ctx.caster().getBbHeight() / 2, ctx.caster().getZ(), 0, 0, 0);

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.LIGHTNING, SpellType.ATTACK, SpellAction.POINT, 20, 0, 50)
                .add(CHARGE_SPEED, 2.0F)
                .add(DefaultProperties.DURATION, 10)
                .add(DefaultProperties.DAMAGE, 8F)
                .add(DefaultProperties.KNOCKBACK, 1.0F)
                .build();
    }
}
