package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

/**
 * Example spell that demonstrates the new temporary enchantment system.
 * This spell applies fire-related enchantments temporarily to weapons:
 * <ul>
 *   <li>Flame enchantment to bows (vanilla)</li>
 *   <li>Fire Aspect to swords (vanilla)</li>
 *   <li>Fire Aspect to axes (vanilla enchantment on non-standard item)</li>
 * </ul>
 * <p>
 * This shows how the new system can use ANY enchantment (vanilla or modded) 
 * as a temporary effect, not just custom mod enchantments.
 */
public class ImbueWeaponFire extends Spell {
    
    public static boolean isSword(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    public static boolean isBow(ItemStack stack) {
        return stack.getItem() instanceof BowItem;
    }
    
    public static boolean isAxe(ItemStack stack) {
        return stack.getItem() instanceof AxeItem;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(ctx.getCaster());
        int duration = (int) getProperty(DefaultProperties.EFFECT_DURATION).floatValue();
        
        // Look for a weapon in the player's hotbar and offhand
        for (ItemStack stack : InventoryUtil.getPrioritisedHotbarAndOffhand(ctx.getCaster())) {
            if (stack.isEmpty()) continue;
            
            Enchantment enchantment = null;
            int level = 1;
            
            // Apply Flame to bows
            if (isBow(stack)) {
                enchantment = Enchantments.FLAMING_ARROWS;
                level = 1;
            }
            // Apply Fire Aspect to swords
            else if (isSword(stack)) {
                enchantment = Enchantments.FIRE_ASPECT;
                level = 2;
            }
            // Apply Fire Aspect to axes (non-standard but works with new system!)
            else if (isAxe(stack)) {
                enchantment = Enchantments.FIRE_ASPECT;
                level = 1;
            }
            
            if (enchantment != null) {
                // Check if the item already has this temporary enchantment
                if (data.getTemporaryEnchantmentDuration(stack, enchantment) <= 0) {
                    data.setTemporaryEnchantment(stack, enchantment, level, duration);
                    
                    playSound(ctx.getLevel(), ctx.getCaster(), 0, -1, ctx.getModifiers());
                    spawnParticles(ctx);
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void spawnParticles(PlayerCastContext ctx) {
        if (ctx.getLevel().isClientSide()) {
            for (int i = 0; i < 10; i++) {
                double x = ctx.getCaster().getX() + ctx.getLevel().random.nextDouble() * 2 - 1;
                double y = ctx.getCaster().getY() + ctx.getCaster().getEyeHeight() - 0.5 + ctx.getLevel().random.nextDouble();
                double z = ctx.getCaster().getZ() + ctx.getLevel().random.nextDouble() * 2 - 1;
                // Spawn fire-colored particles (orange/red)
                // Note: In actual implementation, use proper particle system
                // ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(1.0f, 0.5f, 0.0f).spawn(world);
            }
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.EFFECT_DURATION, 1200) // 60 seconds
                .build();
    }
}
