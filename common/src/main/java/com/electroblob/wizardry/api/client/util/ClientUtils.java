package com.electroblob.wizardry.api.client.util;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public class ClientUtils {

    public static boolean isFirstPerson(Entity entity) {
        return entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
    }
}
