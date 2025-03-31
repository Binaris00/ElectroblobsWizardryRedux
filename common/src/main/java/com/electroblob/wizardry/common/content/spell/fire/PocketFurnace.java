package com.electroblob.wizardry.common.content.spell.fire;

import com.electroblob.wizardry.api.common.spell.internal.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.common.util.InventoryUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.Optional;

public class PocketFurnace extends Spell {
    public static final SpellProperty<Integer> ITEMS_SMELTED = SpellProperty.intProperty("items_smelted");

    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;
        int usesLeft = property(ITEMS_SMELTED);

        ItemStack stack, result;

        for (int i = 0; i < player.getInventory().getContainerSize() && usesLeft > 0; i++) {
            stack = player.getInventory().getItem(i);

            if (!stack.isEmpty() && !player.level().isClientSide) {

                Container dummyInv = new SimpleContainer(1);
                dummyInv.setItem(0, stack);
                Optional<SmeltingRecipe> optionalSmeltingRecipe = player.level().getRecipeManager().getRecipeFor(RecipeType.SMELTING, dummyInv, player.level());

                if (optionalSmeltingRecipe.isPresent()) {
                    optionalSmeltingRecipe.get().assemble(dummyInv, null);

                    result = optionalSmeltingRecipe.get().getResultItem(null);

                    if (!result.isEmpty() && !(stack.getItem() instanceof TieredItem) && !(stack.getItem() instanceof ArmorItem)) {
                        // TODO Bin: implement setting
                        // && !Settings.containsMetaItem(Wizardry.settings.pocketFurnaceItemBlacklist, stack)) {

                        if (stack.getCount() <= usesLeft) {
                            ItemStack stack2 = new ItemStack(result.getItem(), stack.getCount());
                            if (InventoryUtil.doesPlayerHaveItem(player, result.getItem())) {
                                player.addItem(stack2);
                                player.getInventory().setItem(i, ItemStack.EMPTY);
                            } else {
                                player.getInventory().setItem(i, stack2);
                            }
                            usesLeft -= stack.getCount();
                        } else {
                            ItemStack copy = player.getInventory().getItem(i).copy();
                            copy.shrink(usesLeft);
                            player.getInventory().setItem(i, copy);
                            player.getInventory().add(
                                    new ItemStack(result.getItem(), usesLeft));
                            usesLeft = 0;
                        }
                    }
                }
            }
        }

        this.playSound(player.level(), player, 0, -1);

        if (player.level().isClientSide) {
            for (int i = 0; i < 10; i++) {
                double x1 = (float) player.position().x + player.level().random.nextFloat() * 2 - 1.0F;
                double y1 = (float) player.position().y + player.getEyeHeight() - 0.5F + player.level().random.nextFloat();
                double z1 = (float) player.position().z + player.level().random.nextFloat() * 2 - 1.0F;
                player.level().addParticle(ParticleTypes.FLAME, x1, y1, z1, 0, 0.01F, 0);
            }
        }
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(ITEMS_SMELTED, 5)
                .build();
    }
}
