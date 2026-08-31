package com.binaris.wizardry.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;

public final class AdvancementEvents {

    public static final Event<AdvancementGrant> GRANT = EventFactory.createArrayBacked(
            AdvancementGrant.class,
            listeners -> (player, advancement, progress) -> {
                for (AdvancementGrant listener : listeners) {
                    listener.onAdvancementGrant(player, advancement, progress);
                }
            }
    );

    public static final Event<AdvancementRevoke> REVOKE = EventFactory.createArrayBacked(
            AdvancementRevoke.class,
            listeners -> (player, advancement, progress) -> {
                for (AdvancementRevoke listener : listeners) {
                    listener.onAdvancementRevoke(player, advancement, progress);
                }
            }
    );

    public static final Event<AdvancementProgressUpdate> PROGRESS_UPDATE = EventFactory.createArrayBacked(
            AdvancementProgressUpdate.class,
            listeners -> (player, advancement, progress) -> {
                for (AdvancementProgressUpdate listener : listeners) {
                    listener.onProgressUpdate(player, advancement, progress);
                }
            }
    );

    @FunctionalInterface
    public interface AdvancementGrant {
        void onAdvancementGrant(ServerPlayer player, Advancement advancement, AdvancementProgress progress);
    }

    @FunctionalInterface
    public interface AdvancementRevoke {
        void onAdvancementRevoke(ServerPlayer player, Advancement advancement, AdvancementProgress progress);
    }

    @FunctionalInterface
    public interface AdvancementProgressUpdate {
        void onProgressUpdate(ServerPlayer player, Advancement advancement, AdvancementProgress progress);
    }
}
