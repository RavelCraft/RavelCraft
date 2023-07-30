package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class ServerCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "server";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("server"),
                CommandOption.word("server", CommandOption.word("player")),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            return false;
        }

        RavelPlayer player;
        RavelServer server;

        if (args.length == 1) { //If we don't specify a player, then we are transferring ourselves
            if (!sender.isPlayer()) {
                sender.sendMessage(Text.COMMAND_MUST_BE_PLAYER);
                return true;
            }

            player = sender.asPlayer();
        } else {
            player = RavelInstance.getPlayerManager().getPlayer(args[1]);
        }

        try {
            server = RavelServer.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Text.COMMAND_SERVER_INVALID);
            return true;
        }

        if (player == null) {
            sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
            return true;
        }

        if (player.getServer() == server) {
            sender.sendMessage(Text.COMMAND_SERVER_ALREADY);
            return true;
        }

        boolean success = RavelInstance.getPlayerManager().transferPlayerToServer(player, server);
        if (success) {
            if (args.length == 2) {
                sender.sendMessage(Text.COMMAND_SERVER_SUCCESS_OTHER, server.getName());
            }
        } else {
            if (args.length == 1) {
                sender.sendMessage(Text.COMMAND_SERVER_FAIL_SLEF, server.getName());
            } else {
                sender.sendMessage(Text.COMMAND_SERVER_FAIL_OTHER, server.getName());
            }
        }
        return true;
    }
}
