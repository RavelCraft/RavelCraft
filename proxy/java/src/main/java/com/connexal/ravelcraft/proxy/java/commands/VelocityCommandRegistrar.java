package com.connexal.ravelcraft.proxy.java.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.java.JeProxy;
import com.connexal.ravelcraft.proxy.java.players.VelocityJavaRavelPlayer;
import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.commands.arguments.CommandSubOption;
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

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class VelocityCommandRegistrar extends CommandRegistrar {
    public VelocityCommandRegistrar() {
        super(RavelProxyInstance.class.getClassLoader());
    }

    private RavelCommandSender getSender(CommandSource source) {
        if (source instanceof ConsoleCommandSource) {
            return new ServerCommandSender(source);
        } else {
            return new VelocityJavaRavelPlayer((Player) source);
        }
    }

    @Override
    protected void register(RavelCommand command) {
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

        CommandManager commandManager = JeProxy.getServer().getCommandManager();
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
