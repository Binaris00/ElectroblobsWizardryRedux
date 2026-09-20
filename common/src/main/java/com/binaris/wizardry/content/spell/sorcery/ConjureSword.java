package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class ConjureSword extends ConjureItemSpell {
    private static final UUID SWORD_DAMAGE_MODIFIER = UUID.fromString("0f8c4e9a-2a35-4b87-9b3c-7e8b6a1d4f02");
    private static final double BASE_DAMAGE = 5.0D;

    public ConjureSword() {
        super(EBItems.SPECTRAL_SWORD.get());
    }

    @Override
    protected ItemStack addItemExtras(PlayerCastContext ctx, ItemStack stack) {
        float potency = ctx.modifiers().getFactor(SpellModifiers.POTENCY);
        double bonusDamage = BASE_DAMAGE * (potency - 1.0F);
        stack.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(SWORD_DAMAGE_MODIFIER, "Spectral Sword Damage", bonusDamage, AttributeModifier.Operation.ADDITION),
                EquipmentSlot.MAINHAND);
        return super.addItemExtras(ctx, stack);
    }
}