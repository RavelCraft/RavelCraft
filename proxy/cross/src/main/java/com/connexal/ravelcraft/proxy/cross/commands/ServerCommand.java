package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

import java.util.Locale;

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

        this.completeAsync(() -> {
            RavelPlayer player;
            RavelServer server;

            if (args.length == 1) { //If we don't specify a player, then we are transferring ourselves
                if (!sender.isPlayer()) {
                    sender.sendMessage(RavelText.COMMAND_MUST_BE_PLAYER);
                    return;
                }

                player = sender.asPlayer();
            } else {
                player = RavelInstance.getPlayerManager().getPlayer(args[1]);
            }

            try {
                server = RavelServer.valueOf(args[0].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                sender.sendMessage(RavelText.COMMAND_SERVER_INVALID);
                return;
            }

            if (player == null) {
                sender.sendMessage(RavelText.COMMAND_PLAYER_NOT_FOUND);
                return;
            }

            if (player.getServer() == server) {
                sender.sendMessage(RavelText.COMMAND_SERVER_ALREADY);
                return;
            }

            boolean success = RavelInstance.getPlayerManager().transferPlayerToServer(player, server);
            if (success) {
                if (args.length == 2) {
                    sender.sendMessage(RavelText.COMMAND_SERVER_SUCCESS_OTHER, server.getName());
                }
            } else {
                if (args.length == 1) {
                    sender.sendMessage(RavelText.COMMAND_SERVER_FAIL_SLEF, server.getName());
                } else {
                    sender.sendMessage(RavelText.COMMAND_SERVER_FAIL_OTHER, server.getName());
                }
            }
        });

        return true;
    }
}
