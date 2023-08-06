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
            sender.sendMessage(Text.COMMAND_MUST_BE_PLAYER);
            return true;
        }

        if (args.length > 0) {
            return false;
        }

        RavelPlayer player = sender.asPlayer();
        if (player.getServer() == RavelServer.DEFAULT_SERVER) {
            sender.sendMessage(Text.COMMAND_SERVER_ALREADY);
            return true;
        }

        this.completeAsync(() -> {
            boolean success = RavelInstance.getPlayerManager().transferPlayerToServer(player, RavelServer.DEFAULT_SERVER);
            if (!success) {
                sender.sendMessage(Text.COMMAND_SERVER_FAIL_SLEF, RavelServer.DEFAULT_SERVER.getName());
            }
        });

        return true;
    }
}
