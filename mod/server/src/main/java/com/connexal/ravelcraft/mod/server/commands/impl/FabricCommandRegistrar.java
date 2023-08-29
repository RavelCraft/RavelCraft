package com.connexal.ravelcraft.mod.server.commands.impl;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.commands.arguments.CommandSubOption;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.reflect.Field;
import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FabricCommandRegistrar extends CommandRegistrar {
    public FabricCommandRegistrar() {
        super(RavelModServer.class.getClassLoader());

        //Unregister overridden commands
        // Thanks https://gist.github.com/Chocohead/f818e045e825484c241802f62b6a14fb
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            final Field CHILDREN, LITERALS, ARGUMENTS;
            try {
                CHILDREN = CommandNode.class.getDeclaredField("children");
                CHILDREN.setAccessible(true);
                LITERALS = CommandNode.class.getDeclaredField("literals");
                LITERALS.setAccessible(true);
                ARGUMENTS = CommandNode.class.getDeclaredField("arguments");
                ARGUMENTS.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Unable to get CommandNode fields", e);
            }

            CommandNode<?> rootNode = dispatcher.getRoot();

            for (String command : RavelModServer.OVERRIDDEN_COMMANDS) {
                Object child = rootNode.getChild(command);

                if (child != null) {
                    try {
                        if (child instanceof LiteralCommandNode<?>) {
                            ((Map<String, ?>) LITERALS.get(rootNode)).remove(command, child);
                        } else if (child instanceof ArgumentCommandNode<?, ?>) {
                            ((Map<String, ?>) ARGUMENTS.get(rootNode)).remove(command, child);
                        }

                        ((Map<String, ?>) CHILDREN.get(rootNode)).remove(command, child);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("Error removing command: " + command, e);
                    }
                }
            }
        });
    }

    private RavelCommandSender getSender(ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            return RavelInstance.getPlayerManager().getPlayer(source.getPlayer().getUuid());
        } else {
            return new ServerCommandSender(source);
        }
    }

    @Override
    protected void register(RavelCommand command) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<ServerCommandSource> builder = literal(command.getName());

            for (CommandOption option : command.getOptions()) {
                this.processOption(command, option, builder);
            }

            builder.executes(context -> {
                        RavelCommandSender sender = this.getSender(context.getSource());
                        command.execute(sender, new String[0]);
                        return Command.SINGLE_SUCCESS;
                    });

            LiteralCommandNode<ServerCommandSource> node = dispatcher.register(builder);

            for (String alias : command.getAliases()) {
                dispatcher.register(literal(alias)
                        .executes(context -> {
                            RavelCommandSender sender = this.getSender(context.getSource());
                            command.execute(sender, new String[0]);
                            return Command.SINGLE_SUCCESS;
                        })
                        .redirect(node));
            }
        });
    }

    private void processOption(RavelCommand command, CommandOption option, ArgumentBuilder<ServerCommandSource, ?> builder) {
        ArgumentBuilder<ServerCommandSource, ?> newOption;

        switch (option.getType()) {
            case LITERAL -> newOption = literal(option.getName());
            case WORD -> newOption = argument(option.getName(), word());
            case GREEDY_STRING -> newOption = argument(option.getName(), greedyString());
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
