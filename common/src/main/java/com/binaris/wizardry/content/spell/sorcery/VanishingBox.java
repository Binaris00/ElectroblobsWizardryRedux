package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.NotNull;

public class VanishingBox extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        PlayerEnderChestContainer enderChestInventory = ctx.caster().getEnderChestInventory();
        ctx.caster().openMenu(new SimpleMenuProvider((dat, inventory, container) ->
                ChestMenu.threeRows(dat, inventory, enderChestInventory), Component.translatable("container.enderchest")));
        ctx.caster().awardStat(Stats.OPEN_ENDERCHEST);
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellType.UTILITY, SpellAction.POINT_UP, 45, 10, 70)
                .build();
    }
}
