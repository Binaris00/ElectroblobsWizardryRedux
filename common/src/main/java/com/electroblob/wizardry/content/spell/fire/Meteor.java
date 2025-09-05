package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.entity.MeteorEntity;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Meteor extends RaySpell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if(!(EBAccessoriesIntegration.isEquipped(ctx.caster(), EBItems.RING_METEOR.get()))) return super.cast(ctx);

        if(!ctx.world().isClientSide){
            MeteorEntity meteor = new MeteorEntity(ctx.world(), ctx.caster().getX(), ctx.caster().getY() + ctx.caster().getEyeHeight(), ctx.caster().getZ(),
                    ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()), EntityUtil.canDamageBlocks(ctx.caster(), ctx.world()));

            Vec3 direction = ctx.caster().getLookAngle().scale(2 * ctx.modifiers().get(EBItems.RANGE_UPGRADE.get()));
            meteor.setDeltaMovement(direction);

            ctx.world().addFreshEntity(meteor);
        }

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if(ctx.world().canSeeSky(blockHit.getBlockPos().above())){
            if(!ctx.world().isClientSide){
                MeteorEntity meteor = new MeteorEntity(ctx.world(), blockHit.getBlockPos().getX(), blockHit.getBlockPos().getY() + 50, blockHit.getBlockPos().getZ(),
                        ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()), EntityUtil.canDamageBlocks(ctx.caster(), ctx.world()));
                ctx.world().addFreshEntity(meteor);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getCharge() {
        return 20;
    }

    @Override
    public int getCooldown() {
        return 100;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    public SpellAction getAction() {
        return SpellAction.SUMMON;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.FIRE, SpellType.ATTACK, SpellAction.POINT, 100, 20, 200)
                .add(DefaultProperties.RANGE, 40F)
                .add(DefaultProperties.DAMAGE, 2F)
                .build();
    }
}
