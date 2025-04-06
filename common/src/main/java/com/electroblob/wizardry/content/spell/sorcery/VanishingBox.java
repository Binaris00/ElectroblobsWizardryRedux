package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

public class VanishingBox extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        PlayerEnderChestContainer enderChestInventory = ctx.caster().getEnderChestInventory();
        // TODO Bin: Custom ender chest container component(?(
        ctx.caster().openMenu(new SimpleMenuProvider((dat, inventory, container) ->
                ChestMenu.threeRows(dat, inventory, enderChestInventory), Component.translatable("container.enderchest")));
        ctx.caster().awardStat(Stats.OPEN_ENDERCHEST);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.empty();
    }
}
