package com.connexal.ravelcraft.proxy.bedrock.commands;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.proxy.bedrock.players.RavelPlayerImpl;
import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public class CommandRegistrarImpl extends CommandRegistrar {
    private RavelCommandSender getSender(CommandSender sender) {
        if (sender.isPlayer()) {
            return new RavelPlayerImpl((ProxiedPlayer) sender);
        } else {
            return new ServerCommandSender(sender);
        }
    }

    @Override
    protected void register(RavelCommand command) {
        CommandSettings settings = CommandSettings.builder()
                .setAliases(command.getAliases())
                .build();

        BeProxy.getServer().getCommandMap().registerCommand(new Command(command.getName(), settings) {
            @Override
            public boolean onExecute(CommandSender sender, String alias, String[] args) {
                RavelCommandSender ravelSender = getSender(sender);
                command.execute(ravelSender, args);
                return true;
            }
        });
    }
}
