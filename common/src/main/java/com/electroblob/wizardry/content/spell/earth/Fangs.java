package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Fangs extends Spell {
    private static final double FANG_SPACING = 1.25;

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if(spawnFangs(ctx, ctx.caster().position(), GeometryUtil.horizontalise(ctx.caster().getLookAngle()))) return false;
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if(ctx.target() == null) return false;
        if(spawnFangs(ctx, ctx.caster().position(), ctx.target().position().subtract(ctx.caster().position()).normalize())) return false;
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        if(spawnFangs(ctx, ctx.vec3(), Vec3.atLowerCornerOf(ctx.direction().getNormal()))) return false;
        this.playSound(ctx.world(), ctx.vec3(), ctx.castingTicks(), -1);
        return true;
    }

    protected boolean spawnFangs(CastContext ctx, Vec3 origin, Vec3 direction){
        boolean defensiveCircle = ctx.caster() instanceof Player caster && caster.isCrouching() && EBAccessoriesIntegration.isEquipped(caster, EBItems.RING_EVOKER.get());
        boolean flag = false;

        if (ctx.world().isClientSide) {
            double x = origin.x;
            double y =  ctx.caster() != null ? origin.y + ctx.caster().getEyeHeight() : origin.y;
            double z = origin.z;

            for (int i = 0; i < 12; i++) {
                ParticleBuilder.create(EBParticles.DARK_MAGIC, ctx.world().getRandom(), x, y, z, 0.5, false)
                        .color(0.4f, 0.3f, 0.35f).spawn(ctx.world());
            }
        }

        if (defensiveCircle) {
            for (int i = 0; i < 5; i++) {
                float yaw = i * (float) Math.PI * 0.4f;
                flag |= this.spawnFangsAt(ctx.world(), ctx.caster(), yaw, 0,
                        ctx.caster().getEyePosition().add(Mth.cos(yaw) * 1.5, 0, Mth.sin(yaw) * 1.5));
            }

            for (int k = 0; k < 8; k++) {
                float yaw = k * (float) Math.PI * 2f / 8f + ((float) Math.PI * 2f / 5f);
                flag |= this.spawnFangsAt(ctx.world(), ctx.caster(), yaw, 3,
                        ctx.caster().getEyePosition().add(Mth.cos(yaw) * 2.5, 0, Mth.sin(yaw) * 2.5));
            }

        } else {
            Vec3 horizontal = GeometryUtil.horizontalise(ctx.caster().getLookAngle());

            int count = (int) (this.property(DefaultProperties.RANGE) * ctx.modifiers().get(EBItems.RANGE_UPGRADE.get()));
            float yaw = (float) Mth.atan2(horizontal.z, horizontal.x);

            for (int i = 0; i < count; i++) {
                Vec3 vec = ctx.caster().getEyePosition().add(horizontal.scale((i + 1) * FANG_SPACING));
                flag |= this.spawnFangsAt(ctx.world(), ctx.caster(), yaw, i, vec);
            }
        }
        return !flag;
    }

    private boolean spawnFangsAt(Level world, LivingEntity caster, float yaw, int delay, Vec3 vec) {
        Integer y = BlockUtil.getNearestFloor(world, BlockPos.containing(vec), 5);

        if (y != null) {
            EvokerFangs fangs = new EvokerFangs(world, vec.x, y, vec.z, yaw, delay, caster);
            //fangs.getEntityData().set(SpellThrowable.DAMAGE_MODIFIER_NBT_KEY, modifiers.get(SpellModifiers.POTENCY));
            world.addFreshEntity(fangs);
            return true;
        }

        return false;
    }

    // TODO ALLY SYSTEM AND EVENT
//    @SubscribeEvent(priority = EventPriority.HIGH)
//    public static void onLivingAttackEvent(LivingAttackEvent event) {
//        if (event.getSource().getDirectEntity() instanceof EvokerFangs) {
//            if (!AllyDesignationSystem.isValidTarget(event.getSource().getEntity(), event.getEntity())) {
//                event.setCanceled(true);
//            }
//        }
//    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellType.ATTACK, SpellAction.SUMMON, 40, 5, 60)
                .add(DefaultProperties.RANGE, 10F).build();
    }
}
