package com.binaris.wizardry.core.config;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.config.option.BoolConfigOption;
import com.binaris.wizardry.core.config.option.ConfigOption;
import com.binaris.wizardry.core.config.util.ConfigType;

import java.util.ArrayList;
import java.util.Collection;

public final class EBClientConfig implements ConfigProvider {
    private static final ArrayList<ConfigOption<?>> OPTIONS = new ArrayList<>();
    public static final EBClientConfig INSTANCE = new EBClientConfig();

    public static final ConfigOption<Boolean> SPELL_HUD_FLIP_X = add(BoolConfigOption.booleanOption("spell_hud_flip_x", false));
    public static final ConfigOption<Boolean> SPELL_HUD_FLIP_Y =  add(BoolConfigOption.booleanOption("spell_hud_flip_y", false));
    public static final ConfigOption<Boolean> SPELL_HUD_DYNAMIC_POSITIONING = add(BoolConfigOption.booleanOption("spell_hud_dynamic_positioning", false));
    public static final ConfigOption<Boolean> SHOW_CHARGE_METER = add(BoolConfigOption.booleanOption("show_charge_meter", true));
    public static final ConfigOption<Boolean> SHOW_SPELL_HUD = add(BoolConfigOption.booleanOption("show_spell_hud", true));

    private static <T> ConfigOption<T> add(ConfigOption<T> option) {
        OPTIONS.add(option);
        return option;
    }

    @Override
    public String getModid() {
        return WizardryMainMod.MOD_ID;
    }

    @Override
    public ConfigType getType() {
        return ConfigType.CLIENT;
    }

    @Override
    public Collection<ConfigOption<?>> build() {
        return OPTIONS;
    }

    private EBClientConfig() {
    }
}
