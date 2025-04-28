package com.electroblob.wizardry.content.data;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.event.EBServerLevelLoadEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.NBTExtras;
import com.electroblob.wizardry.core.networking.s2c.SpellGlyphPacketS2C;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.core.registry.SpellRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

public class SpellGlyphData extends SavedData {
    public static final String NAME = WizardryMainMod.MOD_ID + "_glyphData";

    public Map<Spell, String> randomNames = new HashMap<>();
    public Map<Spell, String> randomDescriptions = new HashMap<>();

    public SpellGlyphData() {
        this(NAME);
    }

    public SpellGlyphData(String name) {

    }

    public void generateGlyphNames(Level world) {
        for (Spell spell : SpellRegistry.getSpells()) {
            if (!randomNames.containsKey(spell)) randomNames.put(spell, generateRandomName(world.random));
        }

        for (Spell spell : SpellRegistry.getSpells()) {
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

    public static SpellGlyphData get(ServerLevel world) {
        SpellGlyphData instance = world.getDataStorage().get(SpellGlyphData::load, NAME);
        if (instance == null) {
            instance = new SpellGlyphData();
        }

        if (instance.randomNames.size() < SpellRegistry.getSpells().size() || instance.randomDescriptions.size() < SpellRegistry.getSpells().size()) {
            instance.generateGlyphNames(world);
            world.getDataStorage().set(NAME, instance);
        }
        return instance;
    }

    public void sync(ServerPlayer player) {
        HashMap<ResourceLocation, String> names = new HashMap<>();
        HashMap<ResourceLocation, String> descriptions = new HashMap<>();

        for(Spell spell : SpellRegistry.getSpells()) {
            names.put(spell.getLocation(), this.randomNames.get(spell));
            descriptions.put(spell.getLocation(), this.randomDescriptions.get(spell));
        }

        SpellGlyphPacketS2C msg = new SpellGlyphPacketS2C(names, descriptions);
        Services.NETWORK_HELPER.sendTo(player, msg);

        EBLogger.info("Synchronising spell glyph data for " + player.getName().getString());
    }
    
    public static String getGlyphName(Spell spell, SpellGlyphData data) {
        Map<Spell, String> names = data.randomNames;
        return names == null ? "" : names.get(spell);
    }

    public static String getGlyphDescription(Spell spell, SpellGlyphData data) {
        Map<Spell, String> descriptions = data.randomDescriptions;
        return descriptions == null ? "" : descriptions.get(spell);
    }

    public static String getGlyphName(Spell spell, ServerLevel world) {
        Map<Spell, String> names = SpellGlyphData.get(world).randomNames;
        return names == null ? "" : names.get(spell);
    }

    public static String getGlyphDescription(Spell spell, ServerLevel world) {
        Map<Spell, String> descriptions = SpellGlyphData.get(world).randomDescriptions;
        return descriptions == null ? "" : descriptions.get(spell);
    }

    public static SpellGlyphData load(CompoundTag nbt) {
        SpellGlyphData data = new SpellGlyphData();
        data.randomNames = new HashMap<>();
        data.randomDescriptions = new HashMap<>();

        ListTag tagList = nbt.getList("spellGlyphData", Tag.TAG_COMPOUND);

        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag tag = tagList.getCompound(i);
            data.randomNames.put(SpellRegistry.get(ResourceLocation.tryParse(tag.getString("spell"))), tag.getString("name"));
            data.randomDescriptions.put(SpellRegistry.get(ResourceLocation.tryParse(tag.getString("spell"))), tag.getString("description"));
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        ListTag tagList = new ListTag();

        for (Spell spell : SpellRegistry.getSpells()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("spell", spell.getLocation().toString());
            if(this.randomNames.get(spell) != null) {
            	tag.putString("name", this.randomNames.get(spell));
            }
            if(this.randomDescriptions.get(spell) != null) {
            	tag.putString("description", this.randomDescriptions.get(spell));
            }
            tagList.add(tag);
        }

        NBTExtras.storeTagSafely(nbt, "spellGlyphData", tagList);
        return nbt;
    }

    public static void onServerLevelLoad(EBServerLevelLoadEvent event) {
        if(event.getLevel().isClientSide) return;

        ServerLevel level = (ServerLevel) event.getLevel();
        if (level.dimension().location().getPath().equals("overworld")) {
            level.getDataStorage().computeIfAbsent((compoundTag) -> SpellGlyphData.get(level), SpellGlyphData::new, NAME);
        }
    }
}
