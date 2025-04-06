package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.CraftingMenu;

public class PocketWorkbench extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        // TODO Bin: Custom crafting table container component(?(
        ctx.caster().openMenu(new SimpleMenuProvider((dat, inventory, container) ->
                new CraftingMenu(dat, inventory), Component.translatable("container.crafting")));
        ctx.caster().awardStat(Stats.OPEN_ENDERCHEST);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.empty();
    }
}
