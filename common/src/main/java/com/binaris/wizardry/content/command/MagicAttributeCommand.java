package com.binaris.wizardry.content.command;

import com.binaris.wizardry.api.content.spell.SpellCondition;
import com.binaris.wizardry.content.WizardryAttributeModifier;
import com.binaris.wizardry.content.command.argument.SpellConditionArgument;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import java.util.UUID;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class MagicAttributeCommand {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((p_212443_) -> Component.translatable("commands.attribute.failed.entity", new Object[]{p_212443_}));
    private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType((p_212445_, p_212446_) -> Component.translatable("commands.attribute.failed.no_attribute", new Object[]{p_212445_, p_212446_}));
    private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType((p_136497_, p_136498_, p_136499_) -> Component.translatable("commands.attribute.failed.modifier_already_present", new Object[]{p_136499_, p_136498_, p_136497_}));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(Commands.literal("magic_attribute")
                .requires((r) -> r.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.argument("attribute", ResourceArgument.resource(commandBuildContext, Registries.ATTRIBUTE))
                                .then(Commands.literal("get")
                                        .then(Commands.argument("condition", SpellConditionArgument.spellCondition())
                                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                        .executes(ctx -> getModifiers(ctx, EntityArgument.getEntity(ctx, "target"),
                                                                ResourceArgument.getAttribute(ctx, "attribute"), SpellConditionArgument.getSpellCondition(ctx, "condition"), DoubleArgumentType.getDouble(ctx, "scale")))
                                                )
                                        )
                                )

                                .then(Commands.literal("add")
                                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("condition", SpellConditionArgument.spellCondition())
                                                                        .executes(ctx -> setModifier(ctx, EntityArgument.getEntity(ctx, "target"),
                                                                                ResourceArgument.getAttribute(ctx, "attribute"), UuidArgument.getUuid(ctx, "uuid"), StringArgumentType.getString(ctx, "name"),
                                                                                DoubleArgumentType.getDouble(ctx, "value"), AttributeModifier.Operation.ADDITION, SpellConditionArgument.getSpellCondition(ctx, "condition")))
                                                                )
                                                        )
                                                )
                                        )
                                )

                                .then(Commands.literal("multiply_base")
                                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("condition", SpellConditionArgument.spellCondition())
                                                                        .executes(ctx -> setModifier(ctx, EntityArgument.getEntity(ctx, "target"),
                                                                                ResourceArgument.getAttribute(ctx, "attribute"), UuidArgument.getUuid(ctx, "uuid"), StringArgumentType.getString(ctx, "name"),
                                                                                DoubleArgumentType.getDouble(ctx, "value"), AttributeModifier.Operation.MULTIPLY_BASE, SpellConditionArgument.getSpellCondition(ctx, "condition")))
                                                                )
                                                        )
                                                )
                                        )
                                )

                                .then(Commands.literal("multiply_total")
                                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("condition", SpellConditionArgument.spellCondition())
                                                                        .executes(ctx -> setModifier(ctx, EntityArgument.getEntity(ctx, "target"),
                                                                                ResourceArgument.getAttribute(ctx, "attribute"), UuidArgument.getUuid(ctx, "uuid"), StringArgumentType.getString(ctx, "name"),
                                                                                DoubleArgumentType.getDouble(ctx, "value"), AttributeModifier.Operation.MULTIPLY_TOTAL, SpellConditionArgument.getSpellCondition(ctx, "condition")))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int setModifier(CommandContext<CommandSourceStack> ctx, Entity target, Holder.Reference<Attribute> attribute, UUID uuid, String name, double value, AttributeModifier.Operation operation, SpellCondition condition) throws CommandSyntaxException {
        LivingEntity livingentity = getEntityWithAttribute(target, attribute);
        AttributeInstance instance = livingentity.getAttribute(attribute.value());
        WizardryAttributeModifier attributeModifier = new WizardryAttributeModifier(uuid, name, value, operation, condition);

        if (instance.hasModifier(attributeModifier)) {
            throw ERROR_MODIFIER_ALREADY_PRESENT.create(target.getName(), getAttributeDescription(attribute), uuid);
        } else {
            instance.addPermanentModifier(attributeModifier);
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.attribute.modifier.add.success", uuid, getAttributeDescription(attribute), target.getName()), false);
            return 1;
        }
    }

    private static int getModifiers(CommandContext<CommandSourceStack> ctx, Entity target, Holder.Reference<Attribute> attribute, SpellCondition condition, double scale) throws CommandSyntaxException {
        LivingEntity livingentity = getEntityWithAttribute(target, attribute);
        double value = WizardryAttributeModifier.calculateModifiers(livingentity, condition, attribute.value());
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.attribute.value.get.success", getAttributeDescription(attribute), target.getName(), value), false);
        return (int)(value * scale);
    }

    private static LivingEntity getLivingEntity(Entity target) throws CommandSyntaxException {
        if (!(target instanceof LivingEntity)) {
            throw ERROR_NOT_LIVING_ENTITY.create(target.getName());
        } else {
            return (LivingEntity)target;
        }
    }

    private static LivingEntity getEntityWithAttribute(Entity entity, Holder<Attribute> attribute) throws CommandSyntaxException {
        LivingEntity livingentity = getLivingEntity(entity);
        if (!livingentity.getAttributes().hasAttribute(attribute)) {
            throw ERROR_NO_SUCH_ATTRIBUTE.create(entity.getName(), getAttributeDescription(attribute));
        } else {
            return livingentity;
        }
    }

    private static Component getAttributeDescription(Holder<Attribute> attribute) {
        return Component.translatable(attribute.value().getDescriptionId());
    }

    private MagicAttributeCommand() {
    }
}