package com.electroblob.wizardry.api.content.item;

import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
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

/**
 * Base class for artefact items. Artefacts are special items that provide passive effects when being on the player's
 * hotbar or in an accessory (check {@code AccessoriesArtefactItem} in the Accessories integration module).
 * <p>
 * Artefacts can have an associated {@link IArtefactEffect} which defines what effects they provide. These effects
 * are triggered by various events, such as ticking, the player being hurt, or spell casting. Static methods are provided
 * to handle these events and apply the effects of all equipped artefacts. These effects are optional; an artefact can
 * be created without one.
 *
 * @see IArtefactEffect
 */
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
    public @Nullable IArtefactEffect getEffect() {
        return effect;
    }

    /**
     * Called every tick (if player carries the artefact in their hotbar or accessories) to apply the artefact's effect.
     * This method helps to check all equipped artefacts and call their respective effects {@code onTick} method, so
     * we don't have to register each artefact individually.
     * <p>
     * This event won't be calling artefacts that doesn't have any effect associated with them.
     *
     * @param event The living tick event.
     */
    public static void onArtifactTick(EBLivingTick event) {
        if (!(event.getEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onTick(event.getEntity(), event.getLevel(), stack));
    }

    /**
     * Called when the player is responsible for hurting an entity (if player carries the artefact in their hotbar or accessories)
     * to apply the artefact's effect. This method helps to check all equipped artefacts and call their respective effects
     * {@code onHurtEntity} method, so we don't have to register each artefact individually.
     * <p>
     * This event won't be calling artefacts that doesn't have any effect associated with them.
     *
     * @param event The living hurt event.
     */
    public static void onArtifactHurt(EBLivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onHurtEntity(event, stack));
    }

    /**
     * Called when the player dies (if player carries the artefact in their hotbar or accessories) to apply the
     * artefact's effect. This method helps to check all equipped artefacts and call their respective effects
     * {@code onDeath} method, so we don't have to register each artefact individually.
     * <p>
     * This event won't be calling artefacts that doesn't have any effect associated with them.
     *
     * @param event The living death event.
     */
    public static void onArtifactDeath(EBLivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onDeath(event, stack));
    }

    /**
     * Called before a spell is cast (if player carries the artefact in their hotbar or accessories) to apply the
     * artefact's effect. This method helps to check all equipped artefacts and call their respective effects
     * {@code onSpellPreCast} method, so we don't have to register each artefact individually.
     * <p>
     * This event won't be calling artefacts that doesn't have any effect associated with them.
     *
     * @param event The spell cast pre-event.
     */
    public static void onArtifactPreCast(SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onSpellPreCast(event, stack));
    }

    /**
     * Called after a spell is cast (if player carries the artefact in their hotbar or accessories) to apply the
     * artefact's effect. This method helps to check all equipped artefacts and call their respective effects
     * {@code onSpellPostCast} method, so we don't have to register each artefact individually.
     * <p>
     * This event won't be calling artefacts that don't have any effect associated with them.
     *
     * @param event The spell cast post-event.
     */
    public static void onArtifactPostCast(SpellCastEvent.Post event) {
        if (!(event.getCaster() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtefactItem artefact && artefact.getEffect() != null)
                .forEach(stack -> ((ArtefactItem) stack.getItem()).getEffect().onSpellPostCast(event, stack));
    }
}
