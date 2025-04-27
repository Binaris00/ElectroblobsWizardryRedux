package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.jetbrains.annotations.NotNull;

public class ImbueWeapon extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        // Won't work if the weapon already has the enchantment
//        if(WizardData.get(caster) != null){
//
//            for(ItemStack stack : InventoryUtils.getPrioritisedHotbarAndOffhand(caster)){
//
//                if(isSword(stack)
//                        && !EnchantmentHelper.getEnchantments(stack).containsKey(WizardryEnchantments.magic_sword)
//                        && WizardData.get(caster).getImbuementDuration(WizardryEnchantments.magic_sword) <= 0){
//                    // The enchantment level as determined by the damage multiplier. The + 0.5f is so that
//                    // weird float processing doesn't incorrectly round it down.
//                    stack.addEnchantment(WizardryEnchantments.magic_sword, modifiers.get(SpellModifiers.POTENCY) == 1.0f
//                            ? 1
//                            : (int)((modifiers.get(SpellModifiers.POTENCY) - 1.0f) / Constants.POTENCY_INCREASE_PER_TIER
//                            + 0.5f));
//                    WizardData.get(caster).setImbuementDuration(WizardryEnchantments.magic_sword,
//                            (int)(getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
//
//                }else if(isBow(stack)
//                        && !EnchantmentHelper.getEnchantments(stack).containsKey(WizardryEnchantments.magic_bow)
//                        && WizardData.get(caster).getImbuementDuration(WizardryEnchantments.magic_bow) <= 0){
//                    // The enchantment level as determined by the damage multiplier. The + 0.5f is so that
//                    // weird float processing doesn't incorrectly round it down.
//                    stack.addEnchantment(WizardryEnchantments.magic_bow, modifiers.get(SpellModifiers.POTENCY) == 1.0f
//                            ? 1
//                            : (int)((modifiers.get(SpellModifiers.POTENCY) - 1.0f) / Constants.POTENCY_INCREASE_PER_TIER
//                            + 0.5f));
//                    WizardData.get(caster).setImbuementDuration(WizardryEnchantments.magic_bow,
//                            (int)(getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
//
//                }else{
//                    continue;
//                }
//
//                if(world.isRemote){
//                    for(int i=0; i<10; i++){
//                        double x = caster.posX + world.rand.nextDouble() * 2 - 1;
//                        double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
//                        double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
//                        ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(0.9f, 0.7f, 1).spawn(world);
//                    }
//                }
//
//                this.playSound(world, caster, ticksInUse, -1, modifiers);
//                return true;
//            }
//        }
//        return false;

        return false;
    }


    // TODO Settings.containsMetaItem(Wizardry.settings.swordItemWhitelist, stack)
    public static boolean isSword(ItemStack stack){
        return stack.getItem() instanceof SwordItem;
    }

    public static boolean isBow(ItemStack stack){
        return stack.getItem() instanceof BowItem;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder().add(DefaultProperties.EFFECT_DURATION, 900).build();
    }
}
