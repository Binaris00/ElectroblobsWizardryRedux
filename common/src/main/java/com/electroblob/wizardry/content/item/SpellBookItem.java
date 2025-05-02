package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.util.ClientUtils;
import com.electroblob.wizardry.api.client.util.GlyphClientHandler;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.content.data.SpellGlyphData;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.google.common.collect.ImmutableMap;
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
import java.util.Map;

public class SpellBookItem extends Item {
    private static final Map<SpellTier, ResourceLocation> GUI_TEXTURES = ImmutableMap.of(
            SpellTiers.NOVICE, WizardryMainMod.location("textures/gui/spell_book_novice.png"),
            SpellTiers.APPRENTICE, WizardryMainMod.location("textures/gui/spell_book_apprentice.png"),
            SpellTiers.ADVANCED, WizardryMainMod.location("textures/gui/spell_book_advanced.png"),
            SpellTiers.MASTER, WizardryMainMod.location("textures/gui/spell_book_master.png"));

    public SpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide) {
            ClientUtils.openSpellBook(stack);
        }
        return super.use(level, player, interactionHand);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        Spell spell = SpellUtil.getSpell(stack);

        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, stack);

        list.add(discovered ? Component.literal(spell.getDescriptionId().toString()) :
                Component.literal(SpellGlyphData.getGlyphName(spell, GlyphClientHandler.INSTANCE.getGlyphData())).withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)
                .withFont(new ResourceLocation("minecraft", "alt"))));
        list.add(spell.getTier().getDescriptionFormatted());

        Player player = ClientUtils.getPlayer();

        if (EBConfig.discoveryMode && !player.isCreative() && discovered && !Services.WIZARD_DATA.getWizardData(player, player.level()).hasSpellBeenDiscovered(spell)) {
            list.add(Component.translatable(this.getOrCreateDescriptionId() + ".new", Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)));
        }

        if (discovered && tooltipFlag.isAdvanced()) {
            list.add(spell.getElement().getDescriptionFormatted());
            list.add(Component.translatable(spell.getType().getDisplayName()));
        }
    }

    public ResourceLocation getGuiTexture(Spell spell) {
        return GUI_TEXTURES.get(spell.getTier());
    }
}
