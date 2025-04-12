package com.electroblob.wizardry.api.content.spell.internal;

import com.electroblob.wizardry.api.content.util.ByteBufUtils;
import com.electroblob.wizardry.setup.registries.WandUpgrades;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SpellModifiers {
    public static final String POTENCY = "potency";
    public static final String COST = "cost";
    public static final String CHARGEUP = "chargeup";
    public static final String PROGRESSION = "progression";

    private final Map<String, Float> multiplierMap;
    private final Map<String, Float> syncedMultiplierMap;

    public SpellModifiers(){
        multiplierMap = new HashMap<>();
        syncedMultiplierMap = new HashMap<>();
    }

    private SpellModifiers(Map<String, Float> multiplierMap, Map<String, Float> syncedMultiplierMap){
        this.multiplierMap = multiplierMap;
        this.syncedMultiplierMap = syncedMultiplierMap;
    }

    public SpellModifiers copy(){
        return new SpellModifiers(new HashMap<>(this.multiplierMap), new HashMap<>(this.syncedMultiplierMap));
    }

    public SpellModifiers combine(SpellModifiers modifiers){
        for(String key : Sets.union(this.multiplierMap.keySet(), modifiers.multiplierMap.keySet())){
            float newValue = this.get(key) * modifiers.get(key);
            boolean sync = this.syncedMultiplierMap.containsKey(key) || modifiers.syncedMultiplierMap.containsKey(key);
            this.set(key, newValue, sync);
        }
        return this;
    }

    public SpellModifiers set(Item upgrade, float multiplier, boolean needsSyncing){
        this.set(WandUpgrades.getIdentifier(upgrade), multiplier, needsSyncing);
        return this;
    }

    public SpellModifiers set(String key, float multiplier, boolean needsSyncing){
        multiplierMap.put(key, multiplier);
        if(needsSyncing) syncedMultiplierMap.put(key, multiplier);
        return this;
    }

    public float get(Item upgrade){
        return get(WandUpgrades.getIdentifier(upgrade));
    }

    public float get(String key){
        Float value = multiplierMap.get(key);
        return value == null ? 1 : value;
    }

    public float amplified(String key, float scalar){
        return (get(key) - 1) * scalar + 1;
    }

    public Map<String, Float> getModifiers(){
        return Collections.unmodifiableMap(this.multiplierMap);
    }

    public void reset(){
        this.multiplierMap.clear();
        this.syncedMultiplierMap.clear();
    }

    public void read(ByteBuf buf){
        int entryCount = buf.readInt();
        for(int i = 0; i < entryCount; i++){
            this.set(ByteBufUtils.readUTF8String(buf), buf.readFloat(), false);
        }
    }

    public void write(ByteBuf buf){
        buf.writeInt(syncedMultiplierMap.size());
        for(Map.Entry<String, Float> entry : syncedMultiplierMap.entrySet()){
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeFloat(entry.getValue());
        }
    }


    public static SpellModifiers fromNBT(CompoundTag nbt){
        SpellModifiers modifiers = new SpellModifiers();
        for(String key : nbt.getAllKeys()){
            modifiers.set(key, nbt.getFloat(key), true);
        }
        return modifiers;
    }

    public CompoundTag toNBT(){
        CompoundTag nbt = new CompoundTag();
        for(Map.Entry<String, Float> entry : multiplierMap.entrySet()){
            nbt.putFloat(entry.getKey(), entry.getValue());
        }
        return nbt;
    }
}
