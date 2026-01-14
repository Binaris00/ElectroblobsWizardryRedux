package com.binaris.wizardry.content.spell.fire;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.core.EBConfig;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PocketFurnace extends Spell {
    public static final SpellProperty<Integer> ITEMS_SMELTED = SpellProperty.intProperty("items_smelted");

    public PocketFurnace() {
        this.soundValues(1, 1.0f, 0.2f);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        int usesLeft = (int) (property(ITEMS_SMELTED) * ctx.modifiers().get(SpellModifiers.POTENCY));
        ItemStack stack, result;

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);

        if (ctx.world().isClientSide) {
            for (int i = 0; i < 10; i++) {
                double x1 = (float) ctx.caster().position().x + ctx.world().random.nextFloat() * 2 - 1.0F;
                double y1 = (float) ctx.caster().position().y + ctx.caster().getEyeHeight() - 0.5F + ctx.world().random.nextFloat();
                double z1 = (float) ctx.caster().position().z + ctx.world().random.nextFloat() * 2 - 1.0F;
                ctx.world().addParticle(ParticleTypes.FLAME, x1, y1, z1, 0, 0.01F, 0);
            }
        }

        for (int i = 0; i < ctx.caster().getInventory().getContainerSize() && usesLeft > 0; i++) {
            stack = ctx.caster().getInventory().getItem(i);
            if (stack.isEmpty() || ctx.world().isClientSide) continue;

            Container dummyInv = new SimpleContainer(1);
            dummyInv.setItem(0, stack);
            Optional<SmeltingRecipe> optionalSmeltingRecipe = ctx.world().getRecipeManager().getRecipeFor(RecipeType.SMELTING, dummyInv, ctx.caster().level());
            if (optionalSmeltingRecipe.isEmpty()) continue;

            optionalSmeltingRecipe.get().assemble(dummyInv, null);
            result = optionalSmeltingRecipe.get().getResultItem(null);
            if (result.isEmpty() || stack.getItem() instanceof TieredItem || stack.getItem() instanceof ArmorItem)
                continue;
            if (!EBConfig.isOnList(EBConfig.meltItemsBlackList, stack)) continue;

            if (stack.getCount() <= usesLeft) {
                ItemStack stack2 = new ItemStack(result.getItem(), stack.getCount());
                if (InventoryUtil.doesPlayerHaveItem(ctx.caster(), result.getItem())) {
                    ctx.caster().addItem(stack2);
                    ctx.caster().getInventory().setItem(i, ItemStack.EMPTY);
                } else {
                    ctx.caster().getInventory().setItem(i, stack2);
                }
                usesLeft -= stack.getCount();
            } else {
                ItemStack copy = ctx.caster().getInventory().getItem(i).copy();
                copy.shrink(usesLeft);
                ctx.caster().getInventory().setItem(i, copy);
                ctx.caster().getInventory().add(new ItemStack(result.getItem(), usesLeft));
                usesLeft = 0;
            }
        }
        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.FIRE, SpellType.UTILITY, SpellAction.IMBUE, 30, 0, 40)
                .add(ITEMS_SMELTED, 5)
                .build();
    }
}
