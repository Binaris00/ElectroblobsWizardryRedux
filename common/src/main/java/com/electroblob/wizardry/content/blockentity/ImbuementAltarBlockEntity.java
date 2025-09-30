package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ImbuementAltarBlockEntity extends BlockEntity {
    private static final int IMBUEMENT_DURATION = 140;
    private ItemStack stack;
    private int imbuementTimer;
    private ItemStack displayStack;
    private Player lastUser;
    private UUID lastUserUUID;

    public ImbuementAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(EBBlockEntities.IMBUEMENT_ALTAR.get(), pos, blockState);
        this.stack = ItemStack.EMPTY;
        this.displayStack = ItemStack.EMPTY;
    }

    public float getImbuementProgress(){
        return (float)imbuementTimer / IMBUEMENT_DURATION;
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setLastUser(Player player) {
        this.lastUser = player;
        if(player != null) this.lastUserUUID = player.getUUID();
    }

    public Player getLastUser() {
        return lastUser;
    }

    public UUID getLastUserUUID() {
        return lastUserUUID;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        CompoundTag itemTag = new CompoundTag();
        stack.save(itemTag);
        nbt.put("item", itemTag);
        nbt.putInt("imbuementTimer", imbuementTimer);
        if(lastUser != null) nbt.putUUID("lastUser", lastUser.getUUID());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        CompoundTag itemTag = nbt.getCompound("item");
        this.stack = ItemStack.of(itemTag);
        this.imbuementTimer = nbt.getInt("imbuementTimer");
        this.lastUserUUID = nbt.getUUID("lastUser");
    }

    public static <T extends BlockEntity> void update(Level level, BlockPos pos, BlockState state, T entity) {
        if(!(entity instanceof ImbuementAltarBlockEntity altar)) return;


    }
}
