package com.electroblob.wizardry.common.content.spell.sorcery;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class PocketWorkbench extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;
        // TODO Bin: Custom crafting table container component(?(
        player.openMenu(new SimpleMenuProvider((dat, inventory, container) ->
                new CraftingMenu(dat, inventory), Component.translatable("container.crafting")));
        player.awardStat(Stats.OPEN_ENDERCHEST);
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
