package com.binaris.wizardry.client.compat;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.compat.jei.SpellSubtypeInterpreter;
import com.binaris.wizardry.setup.registries.EBItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class WizardryJeiPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return WizardryMainMod.location("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration register) {
        IModPlugin.super.registerItemSubtypes(register);
        register.registerSubtypeInterpreter(EBItems.SCROLL.get(), SpellSubtypeInterpreter.INSTANCE);
        register.registerSubtypeInterpreter(EBItems.SPELL_BOOK.get(), SpellSubtypeInterpreter.INSTANCE);
    }
}
