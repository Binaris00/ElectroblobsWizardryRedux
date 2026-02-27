package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.content.entity.living.Wizard;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBEntities;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class MinionTestHandler {
    private static final Vec3 CASTER_POS = new Vec3(1.5, 2.0, 1.5);
    private static final Vec3 TARGET_POS = new Vec3(2.5, 2.0, 1.5);
    private static final Vec3 MINION_POS = new Vec3(1.5, 2.0, 2.5);
    private static final int MAX_ATTEMPTS = 20;
    private static final int CHECK_DELAY_TICKS = 25;
    private static final int RETRY_DELAY_TICKS = 10;

    /**
     * Part of minions tests that are made by a mob (in this case, Wizards)
     * <p>
     * Creates a {@link MobTestContext} with Wizard (owner), Wither Skeleton (Wizard minion) and Cow (target) entities.
     * With this test we're checking if the minion is actually copying the target of the owner and attacking it. We can't
     * really check if the minion is killing the target, but we can at least check if it's targeting it, which is a good
     * indication that it's working correctly.
     */
    public static void minionCopyMobOwnerTarget(GameTestHelper helper) {
        runTestWithRetries(helper, "Minion Copy Mob Owner Target", 1,
                (ctx) -> ctx.wizard().setTarget(ctx.cow()),
                (ctx) -> ctx.witherSkeleton().getTarget() != null && ctx.witherSkeleton().getTarget().is(ctx.cow()),
                "Minion did not target the cow");
    }

    /**
     * Creates a {@link MobTestContext} with Wizard (owner), Wither Skeleton (Wizard minion) and Cow (target) entities.
     * With this test we're checking if the minion is targeting the last entity that the owner damaged. We can't really
     * check if the minion is killing the target, but we can at least check if it's targeting it, which is a good
     * indication that it's working correctly.
     */
    public static void minionAttackMobOwnerLastDamagedEntity(GameTestHelper helper) {
        runTestWithRetries(helper, "Minion Attack Mob Owner when attacking target",1,
                (ctx) -> ctx.cow().hurt(ctx.wizard().damageSources().mobAttack(ctx.wizard()), 1.0f),
                (ctx) -> ctx.witherSkeleton().getTarget() != null && ctx.witherSkeleton().getTarget().is(ctx.cow()),
                "Minion did not target the cow after owner damaged it");
    }

    /**
     * Creates Wizard (owner) and Wither Skeleton (Wizard minion) entities. With this test we're checking if the minion
     * is targeting the last entity that damaged the owner. We can't really check if the minion is killing the target,
     * but we can at least check if it's targeting it, which is a good indication that it's working correctly.
     */
    public static void minionAttackMobOwnerDamagedByEntity(GameTestHelper helper) {
        runTestWithRetries(helper, "Minion Attack Mob Owner when damaged by entity", 1,
                (ctx) -> ctx.wizard().hurt(ctx.cow().damageSources().mobAttack(ctx.cow()), 4.0f),
                (ctx) -> ctx.witherSkeleton().getTarget() != null && ctx.witherSkeleton().getTarget().is(ctx.cow()),
                "Minion did not target the cow after owner was damaged");
    }


    static void runTestWithRetries(GameTestHelper helper, String testName, int attempt, Consumer<MobTestContext> setupAction, Predicate<MobTestContext> successCondition, String failureMessage) {
        MobTestContext ctx = createMobCtx(helper);
        setupAction.accept(ctx);

        helper.runAtTickTime(CHECK_DELAY_TICKS, () -> {
            if (successCondition.test(ctx)) {
                EBLogger.info("Test %s succeeded on attempt %s".formatted(testName, attempt));
                helper.succeed();
                return;
            }

            if (attempt < MAX_ATTEMPTS) {
                destroyMobCtx(ctx);
                helper.runAtTickTime(RETRY_DELAY_TICKS, () ->
                        runTestWithRetries(helper, testName, attempt + 1, setupAction, successCondition, failureMessage)
                );
            } else {
                helper.fail(failureMessage + ". Minion target: " + ctx.witherSkeleton().getTarget());
            }
        });
    }

    static MobTestContext createMobCtx(GameTestHelper helper) {
        Wizard wizard = (Wizard) GST.mockEntity(helper, CASTER_POS, EBEntities.WIZARD.get());
        Cow cow = (Cow) GST.mockEntity(helper, TARGET_POS, EntityType.COW);
        WitherSkeleton witherSkeleton = (WitherSkeleton) GST.mockEntity(helper, MINION_POS, EntityType.WITHER_SKELETON);

        MinionData data = Services.OBJECT_DATA.getMinionData(witherSkeleton);
        data.setSummoned(true);
        data.setOwnerUUID(wizard.getUUID());
        data.setLifetime(200);
        data.markGoalRestart(true);

        return new MobTestContext(wizard, cow, witherSkeleton);
    }

    static void destroyMobCtx(MobTestContext ctx) {
        ctx.wizard().discard();
        ctx.cow().discard();
        ctx.witherSkeleton().discard();
    }

    record MobTestContext(Wizard wizard, Cow cow, WitherSkeleton witherSkeleton) {
    }

    private MinionTestHandler() {
    }
}
