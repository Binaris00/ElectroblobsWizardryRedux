package com.binaris.wizardry.api.content.item;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
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
 * Base class for artifact items. Artifacts are special items that provide passive effects when being on the player's
 * hotbar or in an accessory (check {@code AccessoriesArtifactItem} in the Accessories integration module).
 * <p>
 * Artifacts can have an associated {@link IArtifactEffect} which defines what effects they provide. These effects
 * are triggered by various events, such as ticking, the player being hurt, or spell casting. Static methods are provided
 * to handle these events and apply the effects of all equipped artifacts. These effects are optional; an artifact can
 * be created without one.
 *
 * @see IArtifactEffect
 */
@SuppressWarnings("ConstantConditions")
public class ArtifactItem extends Item {
    private final @Nullable IArtifactEffect effect;

    public ArtifactItem(Rarity rarity) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        effect = null;
    }

    public ArtifactItem(Rarity rarity, @Nullable IArtifactEffect effect) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.effect = effect;
    }

    /**
     * Called every tick (if player carries the artifact in their hotbar or accessories) to apply the artifact's effect.
     * This method helps to check all equipped artifacts and call their respective effects {@code onTick} method, so
     * we don't have to register each artifact individually.
     * <p>
     * This event won't be calling artifacts that doesn't have any effect associated with them.
     *
     * @param event The living tick event.
     */
    public static void onTick(EBLivingTick event) {
        if (!(event.getEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtifactItem artifact && artifact.getEffect() != null)
                .forEach(stack -> ((ArtifactItem) stack.getItem()).getEffect().onTick(event.getEntity(), event.getLevel(), stack));
    }

    /**
     * Called when the player is responsible for hurting an entity (if player carries the artifact in their hotbar or accessories)
     * to apply the artifact's effect. This method helps to check all equipped artifacts and call their respective effects
     * {@code onHurtEntity} method, so we don't have to register each artifact individually.
     * <p>
     * This event won't be calling artifacts that doesn't have any effect associated with them.
     *
     * @param event The living hurt event.
     */
    public static void onHurtEntity(EBLivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtifactItem artifact && artifact.getEffect() != null)
                .forEach(stack -> ((ArtifactItem) stack.getItem()).getEffect().onHurtEntity(event, stack));
    }

    /**
     * Called when the player is hurt (if player carries the artifact in their hotbar or accessories) to apply the artifact's effect.
     * This method helps to check all equipped artifacts and call their respective effects {@code onPlayerHurt} method, so
     * we don't have to register each artifact individually.
     * <p>
     * This event won't be calling artifacts that don't have any effect associated with them.
     *
     * @param event The living hurt event.
     */
    public static void onPlayerHurt(EBLivingHurtEvent event) {
        if (!(event.getDamagedEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtifactItem artifact && artifact.getEffect() != null)
                .forEach(stack -> ((ArtifactItem) stack.getItem()).getEffect().onPlayerHurt(event, stack));
    }

    /**
     * Called when the player is responsible for killing an entity (if player carries the artifact in their hotbar or accessories)
     * to apply the artifact's effect.
     */
    public static void onKillEntity(EBLivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtifactItem artifact && artifact.getEffect() != null)
                .forEach(stack -> ((ArtifactItem) stack.getItem()).getEffect().onKillEntity(event, stack));
    }

    /**
     * Called before a spell is cast (if player carries the artifact in their hotbar or accessories) to apply the
     * artifact's effect. This method helps to check all equipped artifacts and call their respective effects
     * {@code onSpellPreCast} method, so we don't have to register each artifact individually.
     * <p>
     * This event won't be calling artifacts that doesn't have any effect associated with them.
     *
     * @param event The spell cast pre-event.
     */
    public static void onSpellPreCast(SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtifactItem artifact && artifact.getEffect() != null)
                .forEach(stack -> ((ArtifactItem) stack.getItem()).getEffect().onSpellPreCast(event, stack));
    }

    /**
     * Called after a spell is cast (if player carries the artifact in their hotbar or accessories) to apply the
     * artifact's effect. This method helps to check all equipped artifacts and call their respective effects
     * {@code onSpellPostCast} method, so we don't have to register each artifact individually.
     * <p>
     * This event won't be calling artifacts that don't have any effect associated with them.
     *
     * @param event The spell cast post-event.
     */
    public static void onSpellPostCast(SpellCastEvent.Post event) {
        if (!(event.getCaster() instanceof Player player)) return;
        List<ItemStack> stacks = EBAccessoriesIntegration.getEquippedItems(player);
        stacks.stream().filter(stack -> stack.getItem() instanceof ArtifactItem artifact && artifact.getEffect() != null)
                .forEach(stack -> ((ArtifactItem) stack.getItem()).getEffect().onSpellPostCast(event, stack));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag isAdvanced) {
        list.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }

    public @Nullable IArtifactEffect getEffect() {
        return effect;
    }
}
