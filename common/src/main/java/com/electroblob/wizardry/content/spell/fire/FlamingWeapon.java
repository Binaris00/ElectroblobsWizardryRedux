package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.ImbuementEnchantData;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.sorcery.ImbueWeapon;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class FlamingWeapon extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        for (ItemStack stack : InventoryUtil.getPrioritisedHotBarAndOffhand(ctx.caster())) {
            // If the item isn't a sword or a bow, or if it already has Fire Aspect or Flaming Arrows, skip it
            if ((!ImbueWeapon.isSword(stack) && !ImbueWeapon.isBow(stack)) ||
                    EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.FLAMING_ARROWS) ||
                    EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.FIRE_ASPECT))
                continue;

            ImbuementEnchantData data = Services.OBJECT_DATA.getImbuementData(stack);
            if (data == null) continue;
            int level = ctx.modifiers().get(SpellModifiers.POTENCY) == 1.0f ? 1 : (int) ((ctx.modifiers().get(SpellModifiers.POTENCY) - 1.0f) / EBConfig.POTENCY_INCREASE_PER_TIER + 0.5f);
            long duration = (long) (ctx.world().getGameTime() + (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));

            if (stack.getItem() instanceof SwordItem) {
                stack.enchant(Enchantments.FIRE_ASPECT, level);
                data.addImbuement(Enchantments.FIRE_ASPECT, duration);
            } else {
                stack.enchant(Enchantments.FLAMING_ARROWS, level);
                data.addImbuement(Enchantments.FLAMING_ARROWS, duration);
            }

            if (ctx.world().isClientSide) {
                for (int i = 0; i < 10; i++) {
                    double x = ctx.caster().xo + ctx.world().random.nextDouble() * 2 - 1;
                    double y = ctx.caster().yo + ctx.caster().getEyeHeight() - 0.5 + ctx.world().random.nextDouble();
                    double z = ctx.caster().zo + ctx.world().random.nextDouble() * 2 - 1;
                    ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z)
                            .velocity(0, 0.1, 0).color(0.9f, 0.7f, 1).spawn(ctx.world());
                }
            }

            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.FIRE, SpellType.UTILITY, SpellAction.IMBUE, 35, 0, 70)
                .add(DefaultProperties.EFFECT_DURATION, 900)
                .build();
    }
}
