package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.client.util.ClientUtils;
import com.electroblob.wizardry.api.client.util.GlyphClientHandler;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.content.data.SpellGlyphData;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellBookItem extends Item {
    public SpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        // TODO: Spell book GUI
        return super.use(level, player, interactionHand);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        Spell spell = SpellUtil.getSpell(stack);

        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, stack);

        list.add(discovered ? Component.literal(spell.getLocation().toString()) :
                Component.literal(SpellGlyphData.getGlyphName(spell, GlyphClientHandler.INSTANCE.getGlyphData())).withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)
                .withFont(new ResourceLocation("minecraft", "alt"))));
        list.add(spell.getTier().getNameForTranslationFormatted());

        Player player = ClientUtils.getPlayer();

        if (EBConfig.discoveryMode && !player.isCreative() && discovered && !Services.WIZARD_DATA.getWizardData(player, player.level()).hasSpellBeenDiscovered(spell)) {
            list.add(Component.translatable(this.getOrCreateDescriptionId() + ".new", Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
        }

        if (discovered && tooltipFlag.isAdvanced()) {
            list.add(spell.getElement().getDisplayName());
            list.add(Component.translatable(spell.getType().getDisplayName()));
        }
    }
}
