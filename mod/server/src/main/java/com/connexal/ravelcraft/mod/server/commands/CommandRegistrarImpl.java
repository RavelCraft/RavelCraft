package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.players.RavelPlayerImpl;
import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class CommandRegistrarImpl extends CommandRegistrar {
    private RavelCommandSender getSender(ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            return new RavelPlayerImpl(source.getPlayer());
        } else {
            return new ServerCommandSender(source);
        }
    }

    @Override
    protected void register(RavelCommand command) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal(command.getName())
                    .requires(source -> {
                        if (command.requiresOp()) {
                            return this.getSender(source).isOp();
                        } else {
                            return true;
                        }
                    })
                    .then(argument("arguments", greedyString()))
                    .executes(context -> {
                        String[] args = getString(context, "arguments").split(" ");
                        RavelCommandSender sender = this.getSender(context.getSource());

                        command.execute(sender, args);

                        return Command.SINGLE_SUCCESS;
                    }));
        });
    }
}
