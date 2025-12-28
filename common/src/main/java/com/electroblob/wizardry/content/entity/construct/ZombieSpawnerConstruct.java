package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.MinionData;
import com.electroblob.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.MinionSpell;
import com.electroblob.wizardry.content.spell.necromancy.ZombieApocalypse;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class ZombieSpawnerConstruct extends MagicConstructEntity {

    private static final double MAX_NUDGE_DISTANCE = 0.1;
    public boolean spawnHusks;
    private int spawnTimer = 10;

    public ZombieSpawnerConstruct(EntityType<?> type, Level level) {
        super(type, level);
    }

    public ZombieSpawnerConstruct(Level level) {
        super(EBEntities.ZOMBIE_SPAWNER.get(), level);
    }

    @Override
    public void tick() {
        super.tick();
        if (getCaster() == null) return;

        if (lifetime - tickCount > 10 && spawnTimer-- == 0) {
            playSound(EBSounds.ENTITY_ZOMBIE_SPAWNER_SPAWN.get(), 1, 1);

            if (!level().isClientSide) {
                Zombie zombie = spawnHusks ? new Husk(EntityType.HUSK, level()) : new Zombie(EntityType.ZOMBIE, level());
                zombie.setCustomName(Component.translatable("entity.ebwizardry.minion_name", getCaster().getDisplayName(), zombie.getDisplayName()));

                MinionData data = Services.OBJECT_DATA.getMinionData(zombie);
                data.setSummoned(true);

                double nx = getX() + (random.nextDouble() * 2 - 1) * MAX_NUDGE_DISTANCE;
                double nz = getZ() + (random.nextDouble() * 2 - 1) * MAX_NUDGE_DISTANCE;
                zombie.setPos(nx, getY(), nz);

                if (getCaster() != null) {
                    data.setOwnerUUID(getCaster().getUUID());
                }

                data.setLifetime(Spells.ZOMBIE_APOCALYPSE.property(DefaultProperties.MINION_LIFETIME));

                AttributeInstance attackAttr = zombie.getAttribute(Attributes.ATTACK_DAMAGE);
                if (attackAttr != null) {
                    attackAttr.addPermanentModifier(
                            new AttributeModifier(MinionSpell.POTENCY_ATTRIBUTE_MODIFIER, damageMultiplier - 1, Operation.MULTIPLY_TOTAL)
                    );
                }

                zombie.setHealth(zombie.getMaxHealth());
                zombie.invulnerableTime = 30;
                level().addFreshEntity(zombie);
            }

            spawnTimer += Spells.ZOMBIE_APOCALYPSE.property(ZombieApocalypse.MINION_SPAWN_INTERVAL) + random.nextInt(20);
        }

        if (level().isClientSide) {
            float colorFactor = 0.15f;
            for (double r = 1.5; r < 4; r += 0.2) {
                colorFactor -= 0.02F;
                ParticleBuilder.create(EBParticles.CLOUD)
                        .color(colorFactor, 0, 0)
                        .pos(getX(), getY() - 0.3, getZ())
                        .scale(0.5f / (float) r)
                        .spin(r, 0.02 / r * (1 + level().random.nextDouble()))
                        .spawn(level());
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("spawnTimer", spawnTimer);
        nbt.putBoolean("spawnHusks", spawnHusks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        spawnTimer = nbt.getInt("spawnTimer");
        spawnHusks = nbt.getBoolean("spawnHusks");
    }
}
