package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.content.item.ReceptacleItemValue;
import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ReceptacleBlockEntity extends BlockEntity {
    private @NotNull ItemStack stack;

    public ReceptacleBlockEntity(BlockPos pos, BlockState blockState) {
        super(EBBlockEntities.RECEPTACLE.get(), pos, blockState);
        stack = ItemStack.EMPTY;
    }

    public @NotNull ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        level.blockUpdated(worldPosition, getBlockState().getBlock());
        level.getChunkSource().getLightEngine().checkBlock(worldPosition);
    }

    public Element getElement() {
        return stack.getItem() instanceof ReceptacleItemValue receptacleItem ? receptacleItem.getElement() : null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (!stack.isEmpty()) tag.put("Stack", stack.save(new CompoundTag()));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Stack")) this.stack = ItemStack.of(tag.getCompound("Stack"));
        else this.stack = ItemStack.EMPTY;
    }
}