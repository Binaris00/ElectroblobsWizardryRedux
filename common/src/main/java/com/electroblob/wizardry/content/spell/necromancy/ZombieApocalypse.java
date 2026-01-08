package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.content.entity.construct.ZombieSpawnerConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConstructSpell;
import com.electroblob.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ZombieApocalypse extends ConstructSpell<ZombieSpawnerConstruct> {
    public static final SpellProperty<Integer> MINION_SPAWN_INTERVAL = SpellProperty.intProperty("minion_spawn_interval", 20);
    private static final int SPAWNER_HEIGHT = 8;
    private static final int MIN_SPAWNER_HEIGHT = 3;

    public ZombieApocalypse() {
        super(ZombieSpawnerConstruct::new, false);
        this.soundValues(1.3f, 1, 0);
    }

    @Override
    protected boolean spawnConstruct(CastContext ctx, Vec3 vec3, @Nullable Direction side) {
        Integer ceiling = BlockUtil.getNearestSurface(ctx.world(), new BlockPos((int) vec3.x, (int) (vec3.y + MIN_SPAWNER_HEIGHT), (int) vec3.z), Direction.UP, SPAWNER_HEIGHT - MIN_SPAWNER_HEIGHT, false, BlockUtil.SurfaceCriteria.COLLIDABLE.flip());
        if (ceiling == null) vec3 = vec3.add(0, SPAWNER_HEIGHT, 0);
        else vec3 = new Vec3(vec3.x, ceiling - 0.5, vec3.z);

        return super.spawnConstruct(ctx, vec3, side);
    }

    @Override
    protected void addConstructExtras(ZombieSpawnerConstruct construct, Direction side, @Nullable LivingEntity caster) {
        construct.spawnHusks = caster instanceof Player && EBAccessoriesIntegration.isEquipped((Player) caster, EBItems.CHARM_MINION_VARIANTS.get());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.NECROMANCY, SpellType.CONSTRUCT, SpellAction.SUMMON, 150, 25, 300)
                .add(DefaultProperties.DURATION, 500)
                .add(DefaultProperties.MINION_LIFETIME, 600)
                .add(MINION_SPAWN_INTERVAL)
                .build();
    }
}
