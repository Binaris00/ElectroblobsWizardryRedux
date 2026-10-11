package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.blockentity.SpectralBlockEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ConjureBlock extends RaySpell {
    public static final SpellProperty<Float> BLOCK_LIFETIME = SpellProperty.floatProperty("block_lifetime", 700f);

    public ConjureBlock() {
        this.ignoreLivingEntities(true);
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        Level world = ctx.world();
        BlockPos pos = blockHit.getBlockPos();

        // Delete a spectral block if shifting while pointing at one
        if (ctx.caster() != null && ctx.caster().isShiftKeyDown() && world.getBlockState(pos).getBlock() == EBBlocks.SPECTRAL_BLOCK.get()) {
            if (world.isClientSide) {
                ParticleBuilder.create(EBParticles.FLASH)
                        .pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                        .scale(3).color(0.75f, 1, 0.85f).spawn(world);
            } else {
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
            return true;
        }

        // Normal function
        pos = pos.relative(blockHit.getDirection());

        if (world.isClientSide) {
            ParticleBuilder.create(EBParticles.FLASH)
                    .pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                    .scale(3).color(0.75f, 1, 0.85f).spawn(world);
        }

        if (!BlockUtil.canBlockBeReplaced(world, pos)) return false;
        if (world.isClientSide) return true;

        world.setBlockAndUpdate(pos, EBBlocks.SPECTRAL_BLOCK.get().defaultBlockState());
        if (world.getBlockEntity(pos) instanceof SpectralBlockEntity be) {
            be.setLifetime((int) (property(BLOCK_LIFETIME)
                    * ctx.modifiers().get(SpellModifiers.DURATION, 1f)));
        }
        return true;
    }


    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.POINT, 10, 0, 10)
                .add(DefaultProperties.RANGE, 12F)
                .add(BLOCK_LIFETIME, 700F)
                .build();
    }
}