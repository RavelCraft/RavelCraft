package com.connexal.ravelcraft.mod.server.commands.impl;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.RavelPlayerImpl;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.commands.arguments.CommandSubOption;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandRegistrarImpl extends CommandRegistrar {
    public CommandRegistrarImpl() {
        super(RavelModServer.class.getClassLoader());
    }

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
            LiteralArgumentBuilder<ServerCommandSource> builder = literal(command.getName())
                    .requires(source -> {
                        if (command.requiresOp()) {
                            return this.getSender(source).isOp();
                        } else {
                            return true;
                        }
                    });

            for (CommandOption option : command.getOptions()) {
                this.processOption(command, option, builder);
            }

            builder.executes(context -> {
                        RavelCommandSender sender = this.getSender(context.getSource());
                        command.execute(sender, new String[0]);
                        return Command.SINGLE_SUCCESS;
                    });

            dispatcher.register(builder);
        });
    }

    private void processOption(RavelCommand command, CommandOption option, ArgumentBuilder<ServerCommandSource, ?> builder) {
        ArgumentBuilder<ServerCommandSource, ?> newOption;

        switch (option.getType()) {
            case LITERAL -> newOption = literal(option.getName());
            case WORD -> newOption = argument(option.getName(), word());
            default -> throw new IllegalStateException("Command option not understood");
        }

        if (option instanceof CommandSubOption subOption) {
            for (CommandOption tmpOption : subOption.getOptions()) {
                this.processOption(command, tmpOption, newOption);
            }
        }

        newOption.executes(context -> {
            RavelCommandSender sender = this.getSender(context.getSource());

            String argsUnparsed = context.getInput().substring(context.getInput().indexOf(' ') + 1); // Remove the command name from the input
            String[] args;
            if (argsUnparsed.contains(" ")) {
                args = argsUnparsed.split(" ");
            } else {
                args = new String[]{argsUnparsed};
            }

            command.execute(sender, args);
            return Command.SINGLE_SUCCESS;
        });

        builder.then(newOption);
    }
}
