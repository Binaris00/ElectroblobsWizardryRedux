package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.MinionData;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.entity.living.MagicSlime;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class TrapSlime extends RaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target) || target instanceof MagicSlime) return true;

        // Resist if target is a slime (it would be weird to summon a slime onto another slime)
        if(target instanceof Slime){
            if(!ctx.world().isClientSide && ctx.caster() instanceof Player player) player.displayClientMessage(
                    Component.translatable("spell.resist", target.getName(), this.getDescriptionFormatted()), true);
            return true;
        }

        // Summon the slime
        if(!ctx.world().isClientSide){
            MagicSlime slime = new MagicSlime(ctx.world(), target);

            MinionData data = Services.OBJECT_DATA.getMinionData(slime);
            data.setSummoned(true);
            data.setOwnerUUID(ctx.caster().getUUID());
            data.setShouldFollowOwner(false);
            data.setLifetime((int)(property(DefaultProperties.DURATION).floatValue() * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));

            ctx.world().addFreshEntity(slime);
        }

        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.ITEM_SLIME, x, y, z, 0, 0, 0);
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.2f, 0.8f, 0.1f).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellType.ATTACK, SpellAction.POINT, 20, 0, 50)
                .add(DefaultProperties.RANGE, 8F)
                .add(DefaultProperties.DURATION, 200)
                .build();
    }
}
