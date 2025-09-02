package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class EBDiscoverSpellEvent extends WizardryCancelableEvent {
    private final Player player;
    private final Spell spell;
    private final Source source;

    public EBDiscoverSpellEvent(Player player, Spell spell, Source source) {
        this.player = player;
        this.spell = spell;
        this.source = source;
    }

    public Spell getSpell() {
        return spell;
    }

    public Source getSource() {
        return source;
    }

    public Player getPlayer() {
        return player;
    }

    public enum Source {
        CASTING("casting"),
        IDENTIFICATION_SCROLL("identification_scroll"),
        COMMAND("command"),
        PURCHASE("purchase"),
        OTHER("other");

        final String name;

        Source(String name) {
            this.name = name;
        }

        @Nullable
        public static Source byName(String name) {
            for (Source source : values()) {
                if (source.name.equals(name)) return source;
            }
            return null;
        }
    }
}
