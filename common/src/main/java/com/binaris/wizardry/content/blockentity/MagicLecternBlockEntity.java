package com.binaris.wizardry.content.blockentity;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBBlockEntities;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// Remembers the spell currently displayed by a lectern and drives the client-side book animation.
public class MagicLecternBlockEntity extends BlockEntity {
    public static final double BOOK_OPEN_DISTANCE = 5.0;
    private static final String SPELL_TAG = "Spell";

    @Nullable
    private ResourceLocation spellLocation;

    public int ticksExisted;
    public float pageFlip;
    public float pageFlipPrev;
    public float flipT;
    public float flipA;
    public float bookSpread;
    public float bookSpreadPrev;

    public MagicLecternBlockEntity(BlockPos pos, BlockState state) {
        super(EBBlockEntities.MAGIC_LECTERN.get(), pos, state);
    }

    public Spell getSpell() {
        if (spellLocation == null) return Spells.NONE;

        Spell spell = Services.REGISTRY_UTIL.getSpell(spellLocation);
        return spell == null ? Spells.NONE : spell;
    }

    public void setSpell(@Nullable Spell spell) {
        this.spellLocation = spell == null || spell == Spells.NONE ? null : spell.getLocation();
        this.setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MagicLecternBlockEntity entity) {
        if (!level.isClientSide) return;

        entity.bookSpreadPrev = entity.bookSpread;

        Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                BOOK_OPEN_DISTANCE, candidate -> !candidate.isSpectator());

        if (player != null) {
            entity.bookSpread += 0.1F;

            if (entity.bookSpread < 0.5F || level.random.nextInt(40) == 0) {
                float previousFlip = entity.flipT;
                while (previousFlip == entity.flipT) {
                    entity.flipT += level.random.nextInt(4) - level.random.nextInt(4);
                }
            }
        } else {
            entity.bookSpread -= 0.1F;
        }

        entity.bookSpread = Mth.clamp(entity.bookSpread, 0.0F, 1.0F);
        entity.ticksExisted++;

        entity.pageFlipPrev = entity.pageFlip;
        float flip = (entity.flipT - entity.pageFlip) * 0.4F;
        flip = Mth.clamp(flip, -0.2F, 0.2F);
        entity.flipA += (flip - entity.flipA) * 0.9F;
        entity.pageFlip += entity.flipA;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.spellLocation = tag.contains(SPELL_TAG, Tag.TAG_STRING)
                ? ResourceLocation.tryParse(tag.getString(SPELL_TAG))
                : null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.spellLocation != null) tag.putString(SPELL_TAG, this.spellLocation.toString());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
