package com.binaris.wizardry.core.networking.c2s;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.content.block.LecternBookshelfUtils;
import com.binaris.wizardry.content.blockentity.MagicLecternBlockEntity;
import com.binaris.wizardry.core.networking.abst.Message;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/// Sent when the lectern screen closes, so the lectern remembers the last spell that was being viewed.
public class LecternSpellPacketC2S implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("lectern_spell");
    private static final double MAX_DISTANCE_SQR = 64.0;

    private final BlockPos pos;
    @Nullable
    private final ResourceLocation spell;

    public LecternSpellPacketC2S(BlockPos pos, @Nullable ResourceLocation spell) {
        this.pos = pos;
        this.spell = spell;
    }

    public LecternSpellPacketC2S(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.spell = buf.readNullable(FriendlyByteBuf::readResourceLocation);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeNullable(this.spell, FriendlyByteBuf::writeResourceLocation);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        if (player.distanceToSqr(Vec3.atCenterOf(this.pos)) > MAX_DISTANCE_SQR) return;
        if (!(player.level().getBlockEntity(this.pos) instanceof MagicLecternBlockEntity lectern)) return;

        if (this.spell == null) {
            lectern.setSpell(Spells.NONE);
            return;
        }

        Spell resolved = Services.REGISTRY_UTIL.getSpell(this.spell);
        if (resolved == null) return;
        if (!LecternBookshelfUtils.findAvailableSpells(player.level(), this.pos).contains(resolved)) return;
        lectern.setSpell(resolved);
    }
}
