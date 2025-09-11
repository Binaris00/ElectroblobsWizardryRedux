package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Base class for data providers that generate spell properties files. Extend this class and implement
 * {@link #buildSpells(Consumer)} to add spells to the data pack, finally, load it in your mod's data entrypoint.
 * <p>
 * Note that this class does not automatically include all spells registered in the spell registry;
 * you must add them yourself in your implementation of {@code buildSpells}. This is to allow developers to use this
 * class to generate data for their own spells without having to re-implement the entire spell registry.
 */
public abstract class SpellDataProvider implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;
    protected final String namespace;

    public SpellDataProvider(PackOutput output, String namespace) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "spells");
        this.namespace = namespace;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        Set<ResourceLocation> duplicates = Sets.newHashSet();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        this.buildSpells(spell -> {
            if (!duplicates.add(spell.getLocation()))
                throw new IllegalStateException("Duplicate spell " + spell.getLocation());
            JsonObject jsonObject = spell.getProperties().serializeToJson();
            futures.add(DataProvider.saveStable(output, jsonObject, this.pathProvider.json(spell.getLocation())));
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Spell Data Provider: " + this.namespace;
    }

    protected abstract void buildSpells(@NotNull Consumer<Spell> consumer);
}