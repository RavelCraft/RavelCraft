package com.connexal.ravelcraft.proxy.java.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.java.JeProxy;
import com.connexal.ravelcraft.proxy.java.players.JavaRavelPlayerImpl;
import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;

public class CommandRegistrarImpl extends CommandRegistrar {
    public CommandRegistrarImpl() {
        super(RavelProxyInstance.class.getClassLoader());
    }

    private RavelCommandSender getSender(CommandSource source) {
        if (source instanceof ConsoleCommandSource) {
            return new ServerCommandSender(source);
        } else {
            return new JavaRavelPlayerImpl((Player) source);
        }
    }

    @Override
    protected void register(RavelCommand command) {
        CommandManager commandManager = JeProxy.getServer().getCommandManager();

        CommandMeta.Builder meta = commandManager.metaBuilder(command.getName());
        if (command.getAliases().length > 0) {
            meta.aliases(command.getAliases());
        }

        commandManager.register(meta.build(), new SimpleCommand() {
            @Override
            public void execute(Invocation invocation) {
                command.execute(getSender(invocation.source()), invocation.arguments());
            }

            @Override
            public boolean hasPermission(final Invocation invocation) {
                if (command.requiresOp()) {
                    return getSender(invocation.source()).isOp();
                } else {
                    return true;
                }
            }
        });
    }
}
