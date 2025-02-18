package com.connexal.ravelcraft.proxy.java.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.java.JeProxy;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandSubOption;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class VelocityCommandRegistrar extends CommandRegistrar {
    public VelocityCommandRegistrar() {
        super(RavelProxyInstance.class.getClassLoader());
    }

    private RavelCommandSender getSender(CommandSource source) {
        if (source instanceof ConsoleCommandSource) {
            return new ServerCommandSender(source);
        } else {
            return RavelInstance.getPlayerManager().getPlayer(((Player) source).getUniqueId());
        }
    }

    @Override
    protected void register(RavelCommand command) {
        CommandManager commandManager = JeProxy.getServer().getCommandManager();
        commandManager.unregister(command.getName());
        for (String alias : command.getAliases()) {
            commandManager.unregister(alias);
        }

        LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.<CommandSource>literal(command.getName())
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

        CommandMeta commandMeta = commandManager.metaBuilder(command.getName())
                .aliases(command.getAliases())
                .build();

        commandManager.register(commandMeta, new BrigadierCommand(builder.build()));
    }

    private void processOption(RavelCommand command, CommandOption option, ArgumentBuilder<CommandSource, ?> builder) {
        ArgumentBuilder<CommandSource, ?> newOption;

        switch (option.getType()) {
            case LITERAL -> newOption = LiteralArgumentBuilder.literal(option.getName());
            case WORD -> newOption = RequiredArgumentBuilder.argument(option.getName(), word());
            case GREEDY_STRING -> newOption = RequiredArgumentBuilder.argument(option.getName(), greedyString());
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
            String[] args = argsUnparsed.split(" ");

            command.execute(sender, args);
            return Command.SINGLE_SUCCESS;
        });

        builder.then(newOption);
    }
}
