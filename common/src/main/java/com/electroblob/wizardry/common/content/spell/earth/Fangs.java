package com.electroblob.wizardry.common.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.api.common.util.GeometryUtil;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Fangs extends Spell {
    private static final double FANG_SPACING = 1.25;
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        // TODO Bin: You need artifacts to use this part
        //ArtefactItem.isArtefactActive((Player) caster, WizardryItems.RING_EVOKER.get())
        boolean defensiveCircle = player.isCrouching();

        if (player.level().isClientSide) {
            double x = player.getX();
            double y =  player.getY() + player.getEyeHeight();
            double z = player.getZ();

            for (int i = 0; i < 12; i++) {
                ParticleBuilder.create(EBParticles.DARK_MAGIC, player.getRandom(), x, y, z, 0.5, false)
                        .color(0.4f, 0.3f, 0.35f).spawn(player.level());
            }
        } else {
            if (defensiveCircle) {
                for (int i = 0; i < 5; i++) {
                    float yaw = i * (float) Math.PI * 0.4f;
                    this.spawnFangsAt(player.level(), player, yaw, 0,
                            player.getEyePosition().add(Mth.cos(yaw) * 1.5, 0, Mth.sin(yaw) * 1.5));
                }

                for (int k = 0; k < 8; k++) {
                    float yaw = k * (float) Math.PI * 2f / 8f + ((float) Math.PI * 2f / 5f);
                    this.spawnFangsAt(player.level(), player, yaw, 3,
                            player.getEyePosition().add(Mth.cos(yaw) * 2.5, 0, Mth.sin(yaw) * 2.5));
                }

            } else {
                Vec3 direction = GeometryUtil.horizontalise(player.getLookAngle());
                int count = 10;
                float yaw = (float) Mth.atan2(direction.z, direction.x);

                for (int i = 0; i < count; i++) {
                    Vec3 vec = player.getEyePosition().add(direction.scale((i + 1) * FANG_SPACING));
                    this.spawnFangsAt(player.level(), player, yaw, i, vec);
                }
            }
        }
    }

    private void spawnFangsAt(Level world, LivingEntity caster, float yaw, int delay, Vec3 vec) {
        Integer y = BlockUtil.getNearestFloor(world, new BlockPos((int) vec.x, (int) vec.y, (int) vec.z), 5);

        if (y != null) {
            EvokerFangs fangs = new EvokerFangs(world, vec.x, y, vec.z, yaw, delay, caster);
            world.addFreshEntity(fangs);
        }

    }

//    @SubscribeEvent(priority = EventPriority.HIGH)
//    public static void onLivingAttackEvent(LivingAttackEvent event) {
//        if (event.getSource().getDirectEntity() instanceof EvokerFangs) {
//            if (!AllyDesignationSystem.isValidTarget(event.getSource().getEntity(), event.getEntity())) {
//                event.setCanceled(true);
//            }
//        }
//    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
