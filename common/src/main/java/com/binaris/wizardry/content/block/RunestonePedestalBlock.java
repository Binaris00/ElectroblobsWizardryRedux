package com.binaris.wizardry.content.block;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.content.blockentity.RunestonePedestalBlockEntity;
import com.binaris.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunestonePedestalBlock extends BaseEntityBlock {
    private final Element element;

    public RunestonePedestalBlock(Element element) {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).strength(4));
        this.element = element;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type, BlockEntityType<RunestonePedestalBlockEntity> entityType) {
        return createTickerHelper(type, entityType, RunestonePedestalBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RunestonePedestalBlockEntity(pos, state);
    }

    public Element getElement() {
        return element;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTicker(level, type, EBBlockEntities.RUNESTONE_PEDESTAL.get());
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RunestonePedestalBlockEntity pedestal && pedestal.isNatural()) {
            return 0.0F; // Unbreakable if natural
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL; // Render as a normal block model
    }
}
