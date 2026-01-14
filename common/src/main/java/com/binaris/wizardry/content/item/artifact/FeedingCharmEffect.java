package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.content.spell.healing.ReplenishHunger;
import com.binaris.wizardry.core.IArtefactEffect;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.binaris.wizardry.core.ArtifactUtils.findMatchingWandAndCast;

public class FeedingCharmEffect implements IArtefactEffect {
    @Override
    public void onTick(LivingEntity entity, Level level, ItemStack stack) {
        if (!(entity instanceof Player player)) return;
        if (player.tickCount % 100 != 0) return;

        if (player.getFoodData().getFoodLevel() < 20 - Spells.SATIETY.property(ReplenishHunger.HUNGER_POINTS))
            if (findMatchingWandAndCast(player, Spells.SATIETY)) return;

        if (player.getFoodData().getFoodLevel() < 20 - Spells.REPLENISH_HUNGER.property(ReplenishHunger.HUNGER_POINTS))
            findMatchingWandAndCast(player, Spells.REPLENISH_HUNGER);

    }
}
