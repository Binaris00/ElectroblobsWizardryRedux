package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.EntityTransformations;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Metamorphosis extends RaySpell {

    public Metamorphosis() {
        this.soundValues(0.5f, 1f, 0);
    }

    @Override
    public boolean canCastByEntity() {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        if (target instanceof Mob mob && Services.OBJECT_DATA.isMinion(mob)) return false;

        if (ctx.world().isClientSide) {
            spawnTransformParticles(ctx, target.getX(), target.getY(), target.getZ());
            return true;
        }

        return EntityTransformations.applyRandom(target, ctx);
    }

    private void spawnTransformParticles(CastContext ctx, double x, double y, double z) {
        for (int i = 0; i < 20; i++) {
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y + 1, z).scale(0.5F)
                    .color(0.1f, 0, 0).spawn(ctx.world());
        }
        ParticleBuilder.create(EBParticles.BUFF).pos(x, y, z).color(0xd363cb).spawn(ctx.world());
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.NECROMANCY, SpellTypes.UTILITY, SpellAction.POINT, 15, 0, 30)
                .add(DefaultProperties.RANGE, 10F)
                .build();
    }

    public static void axolotlVariant(CastContext ctx, Axolotl axolotl) {
        RandomSource random = ctx.world().random;
        Axolotl.Variant current = axolotl.getVariant();
        Axolotl.Variant next = current;
        for (int i = 0; i < 5 && next == current; i++) {
            next = random.nextInt(Axolotl.RARE_VARIANT_CHANCE) == 0
                    ? Axolotl.Variant.getRareSpawnVariant(random)
                    : Axolotl.Variant.getCommonSpawnVariant(random);
        }
        axolotl.setVariant(next);
    }
}
