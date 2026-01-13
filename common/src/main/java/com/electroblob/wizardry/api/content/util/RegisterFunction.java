package com.electroblob.wizardry.api.content.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

/**
 * Functional interface used by Electroblob's Wizardry Redux registration helpers to perform actual registrations
 * into the different loaders' registries (Forge, Fabric).
 * <p>
 * We use this inside our registration helpers to abstract away the differences between platforms while still
 * allowing for type safety and compile-time checks. After all the registration process is finished, each loader handle
 * the registration of the deferred objects by calling the provided implementation of this interface.
 *
 * @param <T> the type being registered (e.g. {@code Item}, {@code Block})
 */
@FunctionalInterface
public interface RegisterFunction<T> {
    /**
     * Register a single object into the supplied {@link Registry} under the given {@link ResourceLocation}.
     * <p>
     * Implementations should bridge to the platform-specific registration call (Forge/Fabric or other)
     * and ensure the provided object is registered under the supplied id.
     *
     * @param registry the {@link Registry} to register into, may be {@code null} when the registry instance
     *                 is not required by the caller
     * @param id       the {@link ResourceLocation} to register the object under
     * @param obj      the object instance to register
     */
    void register(Registry<T> registry, ResourceLocation id, T obj);
}
