package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.event.EBItemPickupEvent;
import com.binaris.wizardry.api.content.event.EBPlayerItemPickupEvent;
import com.binaris.wizardry.content.item.RandomSpellBookItem;
import com.binaris.wizardry.core.ArtifactUtils;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static com.binaris.wizardry.core.ArtifactUtils.findMatchingWandAndCast;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Unique
    private ItemEntity itemEntity = (ItemEntity) (Object) this;

    @Shadow
    public abstract ItemStack getItem();

    @Shadow private int pickupDelay;

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void EBWIZARDRY$stackBeforePlayerTouch(Player entity, CallbackInfo ci) {
        if (WizardryEventBus.getInstance().fire(new EBItemPickupEvent(itemEntity, entity))) ci.cancel();
    }

    @Inject(method = "playerTouch", at = @At("TAIL"))
    private void EBWIZARDRY$stackAfterPlayerTouch(Player entity, CallbackInfo ci) {
        if (pickupDelay !=  0) return;
        WizardryEventBus.getInstance().fire(new EBPlayerItemPickupEvent(itemEntity, entity));
    }
}
