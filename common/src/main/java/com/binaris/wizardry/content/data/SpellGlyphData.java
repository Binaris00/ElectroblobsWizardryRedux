package com.binaris.wizardry.content.data;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.EBLogger;
import com.binaris.wizardry.api.content.event.EBServerLevelLoadEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.NBTExtras;
import com.binaris.wizardry.core.networking.s2c.SpellGlyphPacketS2C;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for storing and managing the random names and descriptions assigned to spell glyphs, this
 * is when the player don't know what a spell does and they have to identify it first, all the scrolls/books showing
 * that spell will then have the same random name/description. The names and descriptions are generated randomly when
 * the world is first created and saved to disk.
 * <p>
 * Note that this data is stored per-world, not per-player, so all players in the same world will see the same random
 * names/descriptions for spell glyphs.
 */
public class SpellGlyphData extends SavedData {
    private static final String NAME = WizardryMainMod.MOD_ID + "_glyphData";

    public Map<Spell, String> randomNames = new HashMap<>();
    public Map<Spell, String> randomDescriptions = new HashMap<>();

    /**
     * Retrieves the SpellGlyphData instance for the given world, creating and populating it if it doesn't already
     * exist. This also ensures that all registered spells have entries in the data, adding any that are missing (for
     * example, due to addons being installed or spells being renamed).
     *
     * @param world The server level to get the SpellGlyphData for.
     * @return The SpellGlyphData instance for the given world.
     */
    public static SpellGlyphData get(ServerLevel world) {
        SpellGlyphData instance = world.getDataStorage().get(SpellGlyphData::load, NAME);
        if (instance == null) instance = new SpellGlyphData();

        boolean changed = false;
        // Ensure every registered spell has entries (covers addons / renames)
        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            if (!instance.randomNames.containsKey(spell)) {
                instance.randomNames.put(spell, instance.generateRandomName(world.random));
                changed = true;
            }
            if (!instance.randomDescriptions.containsKey(spell)) {
                instance.randomDescriptions.put(spell, instance.generateRandomDescription(world.random));
                changed = true;
            }
        }

        if (changed) {
            instance.setDirty();
            world.getDataStorage().set(NAME, instance);
        }

        return instance;
    }

    public static Component getGlyphNameFormatted(Spell spell, SpellGlyphData data) {
        return Component.literal(getGlyphName(spell, data)).withStyle(Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt")));
    }

    public static String getGlyphName(Spell spell, SpellGlyphData data) {
        Map<Spell, String> names = data.randomNames;
        return names == null ? "" : names.getOrDefault(spell, "");
    }

    public static String getGlyphDescription(Spell spell, SpellGlyphData data) {
        Map<Spell, String> descriptions = data.randomDescriptions;
        return descriptions == null ? "" : descriptions.getOrDefault(spell, "");
    }

    public static String getGlyphName(Spell spell, ServerLevel world) {
        Map<Spell, String> names = SpellGlyphData.get(world).randomNames;
        return names == null ? "" : names.getOrDefault(spell, "");
    }

    public static String getGlyphDescription(Spell spell, ServerLevel world) {
        Map<Spell, String> descriptions = SpellGlyphData.get(world).randomDescriptions;
        return descriptions == null ? "" : descriptions.getOrDefault(spell, "");
    }

    public static SpellGlyphData load(CompoundTag nbt) {
        SpellGlyphData data = new SpellGlyphData();
        data.randomNames = new HashMap<>();
        data.randomDescriptions = new HashMap<>();

        ListTag tagList = nbt.getList("spellGlyphData", Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag tag = tagList.getCompound(i);
            String spellStr = tag.getString("spell");
            ResourceLocation loc = ResourceLocation.tryParse(spellStr);
            if (loc == null) {
                EBLogger.warn("Skipping malformed spell entry in glyph data: " + spellStr);
                continue;
            }
            Spell spell = Services.REGISTRY_UTIL.getSpell(loc);
            if (spell == null) {
                EBLogger.info("Skipping unknown spell in glyph data: " + loc);
                continue;
            }
            data.randomNames.put(spell, tag.getString("name"));
            data.randomDescriptions.put(spell, tag.getString("description"));
        }
        return data;
    }

    public static void onServerLevelLoad(EBServerLevelLoadEvent event) {
        if (event.getLevel().isClientSide) return;

        ServerLevel level = (ServerLevel) event.getLevel();
        if (level.dimension().location().getPath().equals("overworld")) {
            // Ensure saved data exists and is updated to include any new/missing spells
            SpellGlyphData.get(level);
        }
    }

    public void generateGlyphNames(Level world) {
        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            if (!randomNames.containsKey(spell)) randomNames.put(spell, generateRandomName(world.random));
            if (!randomDescriptions.containsKey(spell))
                randomDescriptions.put(spell, generateRandomDescription(world.random));
        }

        this.setDirty();
    }

    private String generateRandomName(RandomSource random) {
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < random.nextInt(2) + 2; i++) {
            name.append(RandomStringUtils.random(3 + random.nextInt(5), "abcdefghijklmnopqrstuvwxyz")).append(" ");
        }

        return name.toString().trim();
    }

    private String generateRandomDescription(RandomSource random) {
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < random.nextInt(16) + 8; i++) {
            name.append(RandomStringUtils.random(2 + random.nextInt(7), "abcdefghijklmnopqrstuvwxyz")).append(" ");
        }

        return name.toString().trim();
    }

    public void sync(ServerPlayer player) {
        HashMap<ResourceLocation, String> names = new HashMap<>();
        HashMap<ResourceLocation, String> descriptions = new HashMap<>();

        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            names.put(spell.getLocation(), this.randomNames.get(spell));
            descriptions.put(spell.getLocation(), this.randomDescriptions.get(spell));
        }

        SpellGlyphPacketS2C msg = new SpellGlyphPacketS2C(names, descriptions);
        Services.NETWORK_HELPER.sendTo(player, msg);

        EBLogger.info("Synchronising spell glyph data for " + player.getName().getString());
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag tagList = new ListTag();

        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("spell", spell.getLocation().toString());
            if (this.randomNames.get(spell) != null) {
                tag.putString("name", this.randomNames.get(spell));
            }
            if (this.randomDescriptions.get(spell) != null) {
                tag.putString("description", this.randomDescriptions.get(spell));
            }
            tagList.add(tag);
        }

        NBTExtras.storeTagSafely(nbt, "spellGlyphData", tagList);
        return nbt;
    }
}
