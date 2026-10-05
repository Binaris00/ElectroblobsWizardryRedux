package com.binaris.wizardry.core.networking.s2c;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.networking.ClientMessageHandler;
import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;


/// Custom packet for syncing wizardry mob effects particles (sadly vanilla only lets you use Effect Particle and nothing else)
public class MagicEffectSyncS2C implements Message {

    /// What happened to the effect on the server. (to know if it should be removed/added)
    public enum Action {
        ADD,
        REMOVE
    }

    public static final ResourceLocation ID = WizardryMainMod.location("magic_effect_sync");

    private final int entityId;
    private final ResourceLocation effectId;
    private final Action action;
    private final int duration;
    private final int amplifier;
    private final boolean ambient;
    private final boolean visible;
    private final boolean showIcon;

    public MagicEffectSyncS2C(int entityId, MobEffectInstance instance, Action action) {
        this.entityId = entityId;
        this.effectId = BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect());
        this.action = action;
        this.duration = instance.getDuration();
        this.amplifier = instance.getAmplifier();
        this.ambient = instance.isAmbient();
        this.visible = instance.isVisible();
        this.showIcon = instance.showIcon();
    }

    public MagicEffectSyncS2C(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.effectId = buf.readResourceLocation();
        this.action = buf.readEnum(Action.class);
        this.duration = buf.readVarInt();
        this.amplifier = buf.readVarInt();
        this.ambient = buf.readBoolean();
        this.visible = buf.readBoolean();
        this.showIcon = buf.readBoolean();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        pBuf.writeVarInt(entityId);
        pBuf.writeResourceLocation(effectId);
        pBuf.writeEnum(action);
        pBuf.writeVarInt(duration);
        pBuf.writeVarInt(amplifier);
        pBuf.writeBoolean(ambient);
        pBuf.writeBoolean(visible);
        pBuf.writeBoolean(showIcon);
    }

    @Override
    public void handleClient() {
        ClientMessageHandler.magicEffectSync(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public ResourceLocation getEffectId() {
        return effectId;
    }

    public Action getAction() {
        return action;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isShowIcon() {
        return showIcon;
    }
}