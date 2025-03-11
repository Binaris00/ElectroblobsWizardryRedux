package com.electroblob.wizardry.common.content.spell.earth;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

// TODO Bin:  grown aura need some particles tbh
public class GrownAura extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;
        boolean flag = false;

        List<BlockPos> sphere = BlockUtil.getBlockSphere(player.blockPosition(), 2.0F);

        for (BlockPos pos : sphere) {
            BlockState state = player.level().getBlockState(pos);

            if (state.getBlock() instanceof BonemealableBlock) {
                BonemealableBlock plant = (BonemealableBlock) state.getBlock();

                if (plant.isValidBonemealTarget(player.level(), pos, state, player.level().isClientSide)) {
                    if (!player.level().isClientSide) {
                        if (plant.isBonemealSuccess(player.level(), player.level().random, pos, state)) {
                            if (player.level().random.nextFloat() < 0.35f){
                                int i = 0;
                                while (plant.isValidBonemealTarget(player.level(), pos, state, false) && i++ < 100) {
                                    plant.performBonemeal((ServerLevel) player.level(), player.level().random, pos, state);
                                    state = player.level().getBlockState(pos);
                                    plant = (BonemealableBlock) state.getBlock();
                                }
                            } else {
                                plant.performBonemeal((ServerLevel) player.level(), player.level().random, pos, state);
                            }
                        }
                    } else {
                        BoneMealItem.addGrowthParticles(player.level(), pos, 0);
                    }

                    flag = true;
                }
            }
        }

        // TODO Bin: Spell sound
        //if (flag) this.playSound(world, caster, ticksInUse, -1, modifiers);
        //return flag;
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
