package com.electroblob.wizardry.content.block;

import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.setup.registries.Elements;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class ReceptableBlock {
    public static final Map<Element, int[]> PARTICLE_COLOURS;

    static{

        PARTICLE_COLOURS = new HashMap<>();

        PARTICLE_COLOURS.put(Elements.MAGIC, new int[]{0xe4c7cd, 0xfeffbe, 0x9d2cf3});
        PARTICLE_COLOURS.put(Elements.FIRE, new int[]{0xff9600, 0xfffe67, 0xd02700});
        PARTICLE_COLOURS.put(Elements.ICE, new int[]{0xa3e8f4, 0xe9f9fc, 0x138397});
        PARTICLE_COLOURS.put(Elements.LIGHTNING, new int[]{0x409ee1, 0xf5f0ff, 0x225474});
        PARTICLE_COLOURS.put(Elements.NECROMANCY, new int[]{0xa811ce, 0xf575f5, 0x382366});
        PARTICLE_COLOURS.put(Elements.EARTH, new int[]{0xa8f408, 0xc8ffb2, 0x795c28});
        PARTICLE_COLOURS.put(Elements.SORCERY, new int[]{0x56e8e3, 0xe8fcfc, 0x16a64d});
        PARTICLE_COLOURS.put(Elements.HEALING, new int[]{0xfff69e, 0xfffff6, 0xa18200});
    }
}
