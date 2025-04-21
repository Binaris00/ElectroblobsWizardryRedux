package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.IStoredVariable;
import com.electroblob.wizardry.api.content.data.Persistence;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.api.content.util.NBTExtras;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class CurseOfSoulbinding extends RaySpell {
    public static final IStoredVariable<Set<UUID>> TARGETS_KEY = new IStoredVariable.StoredVariable<>("soulboundCreatures", s -> NBTExtras.listToTag(s, NbtUtils::createUUID),
            (ListTag t) -> new HashSet<>(NBTExtras.tagToList(t, NbtUtils::loadUUID)), Persistence.DIMENSION_CHANGE);

    public CurseOfSoulbinding() {
        this.soundValues(1, 1.1f, 0.2f);
        PlayerWizardData.registerStoredVariables(TARGETS_KEY);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity livingTarget && ctx.caster() instanceof Player caster) {
            PlayerWizardData data = Services.WIZARD_DATA.getWizardData(caster, caster.level());
            if (getSoulboundCreatures(data).add(livingTarget.getUUID())) {
                livingTarget.addEffect(new MobEffectInstance(EBMobEffects.CURSE_OF_SOULBINDING.get(), Integer.MAX_VALUE));
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.4f, 0, 0).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(1, 0.8f, 1).spawn(ctx.world());
    }

    public static Set<UUID> getSoulboundCreatures(PlayerWizardData data) {
        if (data.getVariable(TARGETS_KEY) == null) {
            Set<UUID> result = new HashSet<>();
            data.setVariable(TARGETS_KEY, result);
            return result;

        } else return data.getVariable(TARGETS_KEY);
    }

    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (!event.getDamagedEntity().level().isClientSide && event.getDamagedEntity() instanceof Player playerDamaged) {
            PlayerWizardData data = Services.WIZARD_DATA.getWizardData(playerDamaged, playerDamaged.level());

            for (Iterator<UUID> iterator = getSoulboundCreatures(data).iterator(); iterator.hasNext(); ) {
                Entity entity = EntityUtil.getEntityByUUID(playerDamaged.level(), iterator.next());

                if (entity == null || (entity instanceof LivingEntity && !((LivingEntity) entity).hasEffect(EBMobEffects.CURSE_OF_SOULBINDING.get()))) {
                    iterator.remove();
                } else if (entity instanceof LivingEntity) {
                    if(entity.hurt(EBMagicDamageSource.causeDirectMagicDamage(playerDamaged, EBDamageSources.SORCERY), event.getAmount())){
                        entity.playSound(EBSounds.SPELL_CURSE_OF_SOULBINDING_RETALIATE.get(), 1.0F, playerDamaged.level().random.nextFloat() * 0.2F + 1.0F);
                    }
                }
            }
        }
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder().add(DefaultProperties.RANGE, 10F).build();
    }
}
