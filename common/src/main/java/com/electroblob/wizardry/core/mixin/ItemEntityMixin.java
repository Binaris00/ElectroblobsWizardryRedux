package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.content.item.RandomSpellBookItem;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        if (itemstack.getItem() instanceof RandomSpellBookItem) {
            RandomSpellBookItem.create(entity.level(), entity, itemstack);
            entity.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
            itemEntity.discard();
            ci.cancel();
        }
    }
}
