package com.binaris.wizardry.content.blockentity;

import com.binaris.wizardry.api.content.item.IElementValue;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.recipe.ImbuementAltarRecipe;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBAdvancementTriggers;
import com.binaris.wizardry.setup.registries.EBBlockEntities;
import com.binaris.wizardry.setup.registries.EBRecipeTypes;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ImbuementAltarBlockEntity extends BlockEntity {
    /**
     * The duration of the imbuement process in ticks.
     */
    private static final int IMBUEMENT_DURATION = 140;

    /**
     * The item currently on the altar. This could be any placeable item that's going to be imbued into another item,
     * so this is also the item resulting from the imbuement process.
     */
    private ItemStack stack;
    /**
     * The timer for the imbuement process. When this reaches {@link #IMBUEMENT_DURATION}, the imbuement is complete.
     * This is set to 0 when there is no item on the altar or when the item is removed.
     */
    private int imbuementTimer;

    /**
     * The last player to use this altar. This is not saved directly; instead, the UUID is saved and this field is
     * populated on demand. Used for events, advancements and more utilities for extension mods.
     */
    private Player lastUser;

    /**
     * Used just for loading - saving the last user UUID
     */
    private UUID lastUserUUID;

    /**
     * The element associated with the current imbuement. For imbuements involving multiple elements, this is the selected
     * element (i.e. random element from the receptacles). Used for particle effects and the imbuement rays.
     * Can be null if there's no imbuement in process.
     */
    private @Nullable Element element;

    public ImbuementAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(EBBlockEntities.IMBUEMENT_ALTAR.get(), pos, blockState);
        this.stack = ItemStack.EMPTY;
    }

    public static <T extends BlockEntity> void update(Level level, BlockPos pos, BlockState state, T entity) {
        if (!(entity instanceof ImbuementAltarBlockEntity altar)) return;

        if (altar.getLastUserUUID() != null && altar.getLastUser() == null)
            altar.lastUser = level.getPlayerByUUID(altar.lastUserUUID);

        if (altar.imbuementTimer <= 0) return;

        if (altar.imbuementTimer == 1) {
            level.playSound(altar.lastUser, pos, EBSounds.BLOCK_IMBUEMENT_ALTAR_IMBUE.get(), SoundSource.BLOCKS, 1, 1);
        }

        // Finalise imbuement
        if (altar.imbuementTimer++ >= IMBUEMENT_DURATION) {
            altar.craftRecipe();
            if (altar.lastUser instanceof ServerPlayer serverPlayer)
                EBAdvancementTriggers.IMBUEMENT_ALTAR.trigger(serverPlayer, altar.stack);
        }
    }

    private static ItemStack[] getReceptacleItems(Level level, BlockPos pos) {
        ItemStack[] items = new ItemStack[4];
        BlockEntity te;

        for (int i = 0; i < 4; i++) {
            te = level.getBlockEntity(pos.relative(BlockUtil.getHorizontals()[i]));
            if (te instanceof ReceptacleBlockEntity e) {
                items[i] = e.getStack();
            } else {
                items[i] = null;
            }
        }

        return items;
    }

    public void checkRecipe() {
        if (level == null || level.isClientSide) return;

        // Check if all receptacles are filled and center item exists
        if (stack.isEmpty()) {
            imbuementTimer = 0;
            return;
        }

        ItemStack[] receptacleItems = getReceptacleItems(level, worldPosition);
        if (Arrays.stream(receptacleItems).anyMatch(Objects::isNull)) {
            imbuementTimer = 0;
            return;
        }

        // Find matching recipe
        ImbuementAltarRecipe recipe = level.getRecipeManager()
                .getAllRecipesFor(EBRecipeTypes.IMBUEMENT_ALTAR)
                .stream()
                .filter(r -> r instanceof ImbuementAltarRecipe)
                .filter(r -> r.matches(stack, receptacleItems))
                .findFirst()
                .orElse(null);

        if (recipe != null && imbuementTimer == 0) {
            imbuementTimer = 1; // Start the imbuement process
            selectElement();
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        } else if (recipe == null) {
            imbuementTimer = 0;
        }
    }

    private void craftRecipe() {
        if (level == null) return;

        ItemStack[] receptacleItems = getReceptacleItems(level, worldPosition);
        if (Arrays.stream(receptacleItems).anyMatch(Objects::isNull)) {
            imbuementTimer = 0;
            return;
        }

        ImbuementAltarRecipe recipe = level.getRecipeManager()
                .getAllRecipesFor(EBRecipeTypes.IMBUEMENT_ALTAR)
                .stream()
                .filter(r -> r instanceof ImbuementAltarRecipe)
                .filter(r -> r.matches(stack, receptacleItems))
                .findFirst()
                .orElse(null);

        if (recipe != null) {
            ItemStack result = recipe.getResultItem(level.registryAccess()).copy();

            // TODO Consume ingredients, not working very well
            stack.shrink(1);
            for (int i = 0; i < 4; i++) {
                receptacleItems[i].shrink(1);
            }

            for (int i = 0; i < 4; i++) {
                BlockEntity entity = level.getBlockEntity(worldPosition.relative(BlockUtil.getHorizontals()[i]));
                if (entity instanceof ReceptacleBlockEntity e) {
                    e.setStack(ItemStack.EMPTY);
                    level.sendBlockUpdated(e.getBlockPos(), e.getBlockState(), e.getBlockState(), 3);
                }
            }

            // Set result
            setStack(result, false);

            level.playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                    EBSounds.BLOCK_IMBUEMENT_ALTAR_IMBUE.get(), SoundSource.BLOCKS, 1, 1, false);
        }

        imbuementTimer = 0;
        setChanged();
    }

    private void selectElement() {
        if (level == null) return;

        ItemStack[] receptacleItems = getReceptacleItems(level, worldPosition);
        if (Arrays.stream(receptacleItems).anyMatch(Objects::isNull)) {
            element = null;
            return;
        }

        // Select the element randomly from the receptacles
        ItemStack item = receptacleItems[level.random.nextInt(receptacleItems.length)];
        if (item.getItem() instanceof IElementValue receptacleItem) {
            element = receptacleItem.getElement();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        CompoundTag itemTag = new CompoundTag();
        stack.save(itemTag);
        nbt.put("item", itemTag);
        nbt.putInt("imbuementTimer", imbuementTimer);
        if (element != null) nbt.putString("element", Services.REGISTRY_UTIL.getElement(element).toString());
        if (lastUser != null) nbt.putUUID("lastUser", lastUser.getUUID());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        CompoundTag itemTag = nbt.getCompound("item");
        this.stack = ItemStack.of(itemTag);
        this.imbuementTimer = nbt.getInt("imbuementTimer");
        if (nbt.contains("element"))
            this.element = Services.REGISTRY_UTIL.getElement(ResourceLocation.tryParse(nbt.getString("element")));
        if (nbt.hasUUID("lastUser")) this.lastUserUUID = nbt.getUUID("lastUser");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public float getImbuementProgress() {
        return (float) imbuementTimer / IMBUEMENT_DURATION;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Player getLastUser() {
        return lastUser;
    }

    public void setLastUser(Player player) {
        this.lastUser = player;
        if (player != null) this.lastUserUUID = player.getUUID();
    }

    public UUID getLastUserUUID() {
        return lastUserUUID;
    }

    public @Nullable Element getElement() {
        return element;
    }

    public void setStack(ItemStack stack, boolean shouldCheckRecipe) {
        this.stack = stack;
        if (shouldCheckRecipe) checkRecipe();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
