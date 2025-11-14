package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GrowthAura extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.caster().level().isClientSide) return false;

        boolean flag = false;
        Level level = ctx.caster().level();
        List<BlockPos> sphere = BlockUtil.getBlockSphere(ctx.caster().blockPosition(), property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()));

        for (BlockPos pos : sphere) {
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof BonemealableBlock plant) || !(plant.isBonemealSuccess(level, level.random, pos, state)))
                continue;

            if (!(plant.isValidBonemealTarget(level, pos, state, false))) {
                continue;
            } else {
                flag = true;
            }

            if (level.random.nextFloat() < 0.35f && EBAccessoriesIntegration.isEquipped(ctx.caster(), EBItems.CHARM_GROWTH.get())) {
                for (int i = 0; i < 5 && plant.isValidBonemealTarget(level, pos, state, false); i++) {
                    plant.performBonemeal((ServerLevel) level, level.random, pos, state);
                    state = level.getBlockState(pos);
                    plant = (BonemealableBlock) state.getBlock();
                }
            } else {
                plant.performBonemeal((ServerLevel) level, level.random, pos, state);
            }
        }

        if (flag) this.playSound(level, ctx.caster(), ctx.castingTicks(), -1);

        return flag;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellType.UTILITY, SpellAction.POINT_UP, 20, 0, 50)
                .add(DefaultProperties.EFFECT_RADIUS, 2).build();
    }
}