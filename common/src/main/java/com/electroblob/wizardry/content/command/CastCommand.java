package com.electroblob.wizardry.content.command;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

// TODO: Continue with cast command, used to test the player data related to player data with cast command
public final class CastCommand {
    public static final int DEFAULT_CASTING_DURATION = 100;  // 5s (100 ticks)
    public static final int MIN_CASTING_DURATION = 0;
    public static final int MAX_CASTING_DURATION = 1000000;

    private static final SuggestionProvider<CommandSourceStack> SPELL_SUGGESTIONS = (context, builder) -> SharedSuggestionProvider.suggest(
            SpellUtil.getSpellNames(), builder,
            value -> value,
            Component::literal
    );
    private CastCommand(){}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cast")
                .requires((p) -> p.hasPermission(2))
                        .then(Commands.argument("spell", ResourceLocationArgument.id()).suggests(SPELL_SUGGESTIONS)
                                .executes(context -> executePlayer(context, ResourceLocationArgument.getId(context,"spell")))
                        )

        );
    }

    private static int executePlayer(CommandContext<CommandSourceStack> context, ResourceLocation location) {
        CommandSourceStack source = context.getSource();
        if(!source.isPlayer()){
            context.getSource().sendFailure(Component.literal("You need to be a player to execute this!!"));
            return 0;
        }

        Spell spell = Services.REGISTRY_UTIL.getSpell(location);
        if(spell == null){
            context.getSource().sendFailure(Component.literal("Spell not found!"));
            return 0;
        }

        int duration = DEFAULT_CASTING_DURATION + 300;

        ServerPlayer player = source.getPlayer();
        SpellModifiers modifiers = new SpellModifiers();
        // If anything stops the spell working at this point, nothing else happens.
        if(WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.COMMAND, spell, player, modifiers))){
            displayFailure(source, spell);
            return 0;
        }

        if(!spell.isInstantCast()){
            PlayerWizardData data = Services.WIZARD_DATA.getWizardData(player, player.level());
            if(data.isCommandCasting()){
                data.stopCastingContinuousSpell(player); // I think on balance this is quite a nice feature to leave in
            }else{
                data.startCastingContinuousSpell(player, spell, modifiers, duration);
                source.sendSystemMessage(Component.translatable("commands." + WizardryMainMod.MOD_ID + ":cast.success_continuous",
                        spell.getDescriptionId().toString()));
            }
            return 1;
        }else{
            if(spell.cast(new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, modifiers))){
                WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.COMMAND, spell, player, modifiers));

                // TODO SPELL WITH PACKET
//                if(spell.requiresPacket()){
//                    // Sends a packet to all players in dimension to tell them to spawn particles.
//                    // Only sent if the spell succeeded, because if the spell failed, you wouldn't
//                    // need to spawn any particles!
//                    IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), null, spell, modifiers);
//                    WizardryPacketHandler.net.sendToDimension(msg, caster.world.provider.getDimension());
//                }

                source.sendSystemMessage(Component.translatable("commands." + WizardryMainMod.MOD_ID + ":cast.success",
                        spell.getLocation().toString()));
            }
        }

        return 0;
    }

    private static void displayFailure(CommandSourceStack source, Spell spell) {
        source.sendFailure(Component.literal("commands." + WizardryMainMod.MOD_ID + ":cast.failure" + spell.getDescriptionId().toString()));
    }
}
