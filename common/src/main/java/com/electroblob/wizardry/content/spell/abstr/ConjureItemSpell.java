package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ConjureItemSpell extends Spell {
    public static Set<Item> SUPPORTED_ITEMS = new HashSet<>();
    private final Item item;

    public ConjureItemSpell(Item item) {
        this.item = item;
        //ConjureItemData.addApplyItem(item);
        registerSupportedItem(item);
    }

    public static boolean isSupportedItem(Item item){
        return SUPPORTED_ITEMS.contains(item);
    }

    public static boolean isSupportedItem(ItemStack stack){
        return isSupportedItem(stack.getItem());
    }

    public static void registerSupportedItem(Item item){
        SUPPORTED_ITEMS.add(item);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (conjureItem(ctx)) {
            if (ctx.world().isClientSide) spawnParticles(ctx);
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }

        return false;
    }

    protected void spawnParticles(PlayerCastContext ctx) {
        for (int i = 0; i < 10; i++) {
            double x = ctx.caster().xo + ctx.world().random.nextDouble() * 2 - 1;
            double y = ctx.caster().yo + ctx.caster().getEyeHeight() - 0.5 + ctx.world().random.nextDouble();
            double z = ctx.caster().zo + ctx.world().random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0)
                    .color(0.7f, 0.9f, 1).spawn(ctx.world());
        }
    }

    protected boolean conjureItem(PlayerCastContext ctx) {
        ItemStack stack = new ItemStack(item);
        stack = addItemExtras(ctx, stack);

        ConjureData data = Services.WIZARD_DATA.getConjureData(stack);
        data.setLifetime(property(DefaultProperties.ITEM_LIFETIME));
        data.setMaxLifetime(property(DefaultProperties.ITEM_LIFETIME));
        data.setSummoned(true);
        setConjuredName(stack);
        if(!ctx.caster().addItem(stack)){
            if (!ctx.world().isClientSide) {
                ctx.caster().sendSystemMessage(Component.translatable("spell.wizardry:conjure_item.no_space"));
            }
            return false;
        }
        return true;


//        ConjureItemData data = Services.WIZARD_DATA.getConjureItemData(stack);
//        if(data == null){
//            EBLogger.error("ConjureItemData is null for item: " + item.getDescriptionId());
//            return false;
//        }
//        data.setLifetime(property(DefaultProperties.ITEM_LIFETIME));
//        data.setMaxLifetime(property(DefaultProperties.ITEM_LIFETIME));
//        data.summoned(true);
//        setConjuredName(stack);
//        Services.WIZARD_DATA.onConjureItemDataUpdate(data, stack);
//
//        if(!ctx.caster().addItem(stack)){
//            if (!ctx.world().isClientSide) {
//                ctx.caster().sendSystemMessage(Component.translatable("spell.wizardry:conjure_item.no_space"));
//            }
//            return false;
//        }
//
//        return true;
    }


    protected ItemStack addItemExtras(PlayerCastContext ctx, ItemStack stack) {
        return stack;
    }

    public void setConjuredName(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement("display");
        tag.putString("Name", "Bound " + stack.getDisplayName().getString());
        stack.getOrCreateTag().put("display", tag);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
