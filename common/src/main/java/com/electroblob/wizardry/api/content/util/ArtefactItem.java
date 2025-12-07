package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class ArtefactItem extends Item {
    private final @Nullable IArtefactEffect effect;

    public ArtefactItem(Rarity rarity) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        effect = null;
    }

    public ArtefactItem(Rarity rarity, @Nullable IArtefactEffect effect) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.effect = effect;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag isAdvanced) {
        list.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }

    public static void onArtifactTick(EBLivingTick event) {
        if (!(event.getEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onTick(event.getEntity(), event.getLevel(), stack));
    }

    public static void onArtifactHurt(EBLivingHurtEvent event) {
        if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onHurtEntity(event, stack));
    }

    public static void onArtifactDeath(EBLivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onDeath(event, stack));
    }

    public static void onArtifactPreCast(SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onSpellPreCast(event, stack));
    }

    public static void onArtifactPostCast(SpellCastEvent.Post event) {
        if (!(event.getCaster() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onSpellPostCast(event, stack));
    }

    public @Nullable IArtefactEffect getEffect() {
        return effect;
    }
}
