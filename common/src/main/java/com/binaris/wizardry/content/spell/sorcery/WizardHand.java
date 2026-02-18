package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.entity.living.Wizard;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class WizardHand extends RaySpell {
    Random rand = new Random();

    @Override
    public boolean canCastByEntity() {
        return false;
    }

    @Override
    public boolean canCastByLocation() {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        Player player = (Player) ctx.caster();
        BlockPos pos = blockHit.getBlockPos();
        if (ctx.world().isClientSide) return true; // important to prevent desyncs

        Wizard wizard = EBEntities.WIZARD.get().create(ctx.world());
        wizard.setPos(player.getX() + 10, player.getY() + 100, player.getZ() + 10);
        ctx.world().addFreshEntity(wizard);

        if (BlockUtil.canBreak(wizard, ctx.world(), pos)) {
            ctx.world().destroyBlock(pos, true, wizard);
            return true;
        }

        return true;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();

        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(r, g, b).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(r, g, b).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellType.UTILITY, SpellAction.POINT, 35, 0, 40)
                .add(DefaultProperties.RANGE, 8F)
                .add(DefaultProperties.SENSIBLE, true)
                .build();
    }
}
