package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.content.item.RandomSpellBookItem;
import com.electroblob.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.electroblob.wizardry.core.ArtifactUtils.findMatchingWandAndCast;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Unique
    private ItemEntity itemEntity = (ItemEntity) (Object) this;

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"), cancellable = true)
    public void EBWIZARDRY$playerTouch(Player entity, CallbackInfo ci) {
        ItemStack itemstack = getItem();
        Item item = itemstack.getItem();
        int i = itemstack.getCount();

        if (EBAccessoriesIntegration.isEquipped(entity, EBItems.CHARM_AUTO_SMELT.get())) {
            if (entity.getInventory().items.stream()
                    .filter(s -> !entity.level().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(s), entity.level()).isEmpty())
                    .mapToInt(ItemStack::getCount).sum() >= 64) {
                findMatchingWandAndCast(entity, Spells.POCKET_FURNACE);
            }
        }

        if (itemstack.getItem() instanceof RandomSpellBookItem) {
            RandomSpellBookItem.create(entity.level(), entity, itemstack);
            entity.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
            itemEntity.discard();
            ci.cancel();
        }
    }
}
