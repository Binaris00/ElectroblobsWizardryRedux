package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.entity.construct.DecayConstruct;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ConstructRangedSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Decay extends ConstructRangedSpell<DecayConstruct> {
    public static final SpellProperty<Integer> PATCHES_SPAWNED = SpellProperty.intProperty("decay_patches_spawned", 5);

    public Decay() {
        super(DecayConstruct::new, false);
    }

    @Override
    protected boolean spawnConstruct(CastContext ctx, Vec3 vec3, @Nullable Direction side) {
        BlockPos origin = BlockPos.containing(vec3);
        if (ctx.world().getBlockState(origin).isCollisionShapeFullBlock(ctx.world(), origin)) return false;

        super.spawnConstruct(ctx, vec3, side);

        float decayCount = property(PATCHES_SPAWNED);
        int quantity = (int) (decayCount * ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()));
        int horizontalRange = (int) (0.4 * decayCount * ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()));
        int verticalRange = (int) (6 * ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()));

        for (int i = 0; i < quantity; i++) {
            BlockPos pos = BlockUtil.findNearbyFloorSpace(ctx.world(), origin, horizontalRange, verticalRange, false);
            if (pos == null) break;
            super.spawnConstruct(ctx, new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), side);
        }

        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellType.DEFENCE, SpellAction.POINT, 35, 0, 80)
                .add(DefaultProperties.RANGE, 12F)
                .add(DefaultProperties.DURATION, 400)
                .add(DefaultProperties.EFFECT_DURATION, 400)
                .add(PATCHES_SPAWNED)
                .build();
    }
}
