package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class LobbyCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "lobby";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "hub", "l" };
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[0];
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage(RavelText.COMMAND_MUST_BE_PLAYER);
            return true;
        }

        if (args.length > 0) {
            return false;
        }

        RavelPlayer player = sender.asPlayer();
        if (player.getServer() == RavelServer.DEFAULT_SERVER) {
            sender.sendMessage(RavelText.COMMAND_SERVER_ALREADY);
            return true;
        }

        this.completeAsync(() -> {
            boolean success = RavelInstance.getPlayerManager().transferPlayerToServer(player, RavelServer.DEFAULT_SERVER);
            if (!success) {
                sender.sendMessage(RavelText.COMMAND_SERVER_FAIL_SLEF, RavelServer.DEFAULT_SERVER.getName());
            }
        });

        return true;
    }
}
