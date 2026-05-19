package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.spell.sorcery.ImbueWeapon;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BattlemageRingEffect implements IArtifactEffect {
    @Override
    public void onSpellPreCast(SpellCastEvent.Pre e, ItemStack artifact) {
        if (e.getCaster() instanceof Player p && p.getOffhandItem().getItem() instanceof ICastItem && ImbueWeapon.isSword(p.getMainHandItem())) {
            e.getModifiers().multiply(SpellModifiers.POTENCY, 1.1f);
        }
    }
}
