package com.electroblob.wizardry.content;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.data.WizardData;
import com.electroblob.wizardry.api.content.event.EBDiscoverSpellEvent;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBAdvancementTriggers;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Abstract base class for all spell forfeits. A forfeit is a negative effect that is applied to the caster when they
 * use a spell scroll or wand and the casts fail. Each forfeit has an associated sound and message that can be
 * displayed to the player...
 */
public class Forfeit {
    protected final SoundEvent sound;
    private final ResourceLocation name;
    private final Element element;
    private final SpellTier spellTier;
    private final @Nullable BiConsumer<Level, Player> effect;

    public Forfeit(ResourceLocation name, Element element, SpellTier spellTier, @Nullable BiConsumer<Level, Player> effect) {
        this.name = name;
        this.sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(name.getNamespace(), "forfeit." + name.getPath()));
        this.element = element;
        this.spellTier = spellTier;
        this.effect = effect;
    }

    public Forfeit(String name, Element element, SpellTier spellTier, @Nullable BiConsumer<Level, Player> effect) {
        this(WizardryMainMod.location(name), element, spellTier, effect);
    }

    /**
     * Applies the forfeit to the given player in the given world. If there is no effect, this method does nothing (so
     * addon devs can override this method if they want to implement custom behaviour). With an effect (BiConsumer with
     * world and player parameters), it calls the effect.
     *
     * @param world  The world in which to apply the effect.
     * @param player The player to whom the effect is applied.
     */
    public void apply(Level world, Player player) {
        if (effect != null) effect.accept(world, player);
    }

    /**
     * Returns the forfeit message with the given implement name.
     *
     * @param implementName The name of the implement used to cast the spell.
     * @return The forfeit message.
     */
    public Component getMessage(Component implementName) {
        return Component.translatable("forfeit." + name.getNamespace() + "." + name.getPath(), implementName);
    }

    /**
     * Returns the forfeit message for when it is applied via a wand.
     *
     * @return The forfeit message for wands.
     */
    public Component getMessageForWand() {
        return getMessage(Component.translatable("item.ebwizardry.wand.generic"));
    }

    /**
     * Returns the forfeit message for when it is applied via a scroll.
     *
     * @return The forfeit message for scrolls.
     */
    public Component getMessageForScroll() {
        return getMessage(Component.translatable("item.ebwizardry.scroll.generic"));
    }

    /**
     * Returns the name of this forfeit.
     *
     * @return The forfeit name.
     */
    public ResourceLocation getName() {
        return name;
    }

    /**
     * Returns the sound associated with this forfeit.
     *
     * @return The forfeit sound.
     */
    public SoundEvent getSound() {
        return sound;
    }

    /**
     * Returns the element associated with this forfeit.
     *
     * @return The forfeit element.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Returns the spell tier associated with this forfeit.
     *
     * @return The forfeit spell tier.
     */
    public SpellTier getSpellTier() {
        return spellTier;
    }

    /**
     * Handles pre-spell-cast events to potentially apply a forfeit to the player if they fail to cast the spell.
     *
     * @param event The spell cast pre event.
     */
    public static void onSpellCastPreEvent(SpellCastEvent.Pre event) {
        Player player = event.getCaster() instanceof Player p ? p : null;
        if (player == null || player.isCreative()) return;
        if (event.getSource() != SpellCastEvent.Source.WAND && event.getSource() != SpellCastEvent.Source.SCROLL)
            return;

        SpellManagerData spellData = Services.OBJECT_DATA.getSpellManagerData(player);
        WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);

        if (spellData.hasSpellBeenDiscovered(event.getSpell())) return;

        // Calculate forfeit result - ALWAYS consume Random values in the same order on both sides
        ForfeitResult result = calculateForfeit(player, spellData, wizardData, event.getSpell());

        if (!result.shouldTrigger) return;
        event.setCanceled(true);

        // Apply the pre-calculated forfeit
        applyForfeitToPlayer(event, player, result.forfeit);
    }

    /**
     * Represents the result of a forfeit calculation, including whether it should trigger and which forfeit to apply.
     */
    private record ForfeitResult(boolean shouldTrigger, Forfeit forfeit) {
    }

    /**
     * Calculates whether a forfeit should trigger and which forfeit to apply.
     * IMPORTANT: This method ALWAYS consumes the same Random values on both client and server to maintain sync.
     *
     * @param player     The player attempting to cast the spell.
     * @param spellData  The spell manager data of the player.
     * @param wizardData The wizard data of the player.
     * @param spell      The spell being cast.
     * @return A ForfeitResult containing whether to trigger and which forfeit to apply.
     */
    private static ForfeitResult calculateForfeit(Player player, SpellManagerData spellData, WizardData wizardData, Spell spell) {
        float chance = (float) EBConfig.forfeitChance;
        if (EBAccessoriesIntegration.isEquipped(player, EBItems.AMULET_WISDOM.get())) chance *= 0.5F;

        boolean isUndiscovered = !spellData.hasSpellBeenDiscovered(spell);

        Random random = wizardData.getRandom();
        float roll = random.nextFloat();
        Forfeit forfeit = ForfeitRegistry.getRandomForfeit(random, spell.getTier(), spell.getElement());

        boolean shouldTrigger = roll < chance && isUndiscovered;
        return new ForfeitResult(shouldTrigger, forfeit);
    }

    /**
     * Applies a random forfeit to the given player based on the spell they attempted to cast.
     *
     * @param event   The spell cast event.
     * @param player  The player to whom the forfeit is applied.
     * @param forfeit The forfeit to apply (pre-calculated to maintain Random sync).
     */
    private static void applyForfeitToPlayer(SpellCastEvent.Pre event, Player player, Forfeit forfeit) {
        if (forfeit == null) {
            player.sendSystemMessage(Component.translatable("forfeit.ebwizardry.do_nothing"));
            return;
        }

        forfeit.apply(event.getLevel(), player);
        consumeResourceForForfeit(event, player);

        if (player instanceof ServerPlayer) EBAdvancementTriggers.SPELL_FAILURE.triggerFor(player);
        EntityUtil.playSoundAtPlayer(player, forfeit.getSound(), 1, 1);

        Component message = event.getSource() == SpellCastEvent.Source.WAND ? forfeit.getMessageForWand() : forfeit.getMessageForScroll();
        if (!player.level().isClientSide) player.displayClientMessage(message, true);
    }

    /**
     * Handles post-spell-cast events to discover the spell for the player if they have successfully cast it.
     *
     * @param event The spell cast post event.
     */
    public static void onSpellCastPostEvent(SpellCastEvent.Post event) {
        if (!(event.getCaster() instanceof Player player)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            EBAdvancementTriggers.CAST_SPELL.trigger(serverPlayer, event.getSpell(), player.getItemInHand(player.getUsedItemHand()));
        }

        // Only discover spell on server side - will sync to client automatically
        if (!event.getLevel().isClientSide()) discoverSpell(player, event.getSpell());
    }

    /**
     * Discovers the given spell for the given player, firing an {@link EBDiscoverSpellEvent} first.
     *
     * @param player The player to discover the spell for.
     * @param spell  The spell to be discovered.
     */
    private static void discoverSpell(Player player, Spell spell) {
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
        boolean eventCancelled = WizardryEventBus.getInstance().fire(new EBDiscoverSpellEvent(player, spell, EBDiscoverSpellEvent.Source.CASTING));

        if (eventCancelled || !data.discoverSpell(spell)) return;

        if (!player.level().isClientSide && !player.isCreative()) {
            EntityUtil.playSoundAtPlayer(player, EBSounds.MISC_DISCOVER_SPELL.get(), 1.25f, 1);
            Component message = Component.translatable("spell.discover", spell.getDescriptionFormatted());
            player.sendSystemMessage(message);
        }
    }

    /**
     * Consumes the appropriate resource (scroll or mana from wand) when a forfeit is applied.
     *
     * @param event  The spell cast event.
     * @param player The player to consume the resource from.
     */
    private static void consumeResourceForForfeit(SpellCastEvent.Pre event, Player player) {
        ItemStack stack = findCastingItem(player);
        if (stack.isEmpty()) return;

        if (event.getSource() == SpellCastEvent.Source.SCROLL) {
            if (!player.isCreative()) stack.shrink(1);
        } else if (stack.getItem() instanceof IManaStoringItem manaItem) {
            int cost = (int) (event.getSpell().getCost() * event.getModifiers().get(SpellModifiers.COST) + 0.1f);
            manaItem.consumeMana(stack, cost, player);
        }
    }

    /**
     * Finds the item the player is using to cast the spell (either a wand or a scroll) by checking both hands.
     *
     * @param player The player whose casting item is to be found.
     * @return The casting item, or an empty ItemStack if none is found.
     */
    private static ItemStack findCastingItem(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof ISpellCastingItem) return mainHand;
        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof ISpellCastingItem) return offHand;
        return ItemStack.EMPTY;
    }
}
