package com.electroblob.wizardry.common.content.spell.sorcery;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

public class VanishingBox extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        PlayerEnderChestContainer enderChestInventory = player.getEnderChestInventory();
        // TODO Bin: Custom ender chest container component(?(
        player.openMenu(new SimpleMenuProvider((dat, inventory, container) ->
                ChestMenu.threeRows(dat, inventory, enderChestInventory), Component.translatable("container.enderchest")));
        player.awardStat(Stats.OPEN_ENDERCHEST);
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
