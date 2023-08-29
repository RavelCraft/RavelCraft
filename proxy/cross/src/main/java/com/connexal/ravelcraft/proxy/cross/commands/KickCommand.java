package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class KickCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("player",
                        CommandOption.literal("server", CommandOption.greedyString("reason")),
                        CommandOption.literal("network", CommandOption.greedyString("reason"))),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length < 3) {
            return false;
        }

        RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
            return true;
        }

        String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
        this.completeAsync(() -> {
            boolean success = RavelInstance.getPlayerManager().kick(player, reason, args[1].equalsIgnoreCase("network"));
            if (success) {
                sender.sendMessage(Text.COMMAND_KICK_SUCCESS, player.getName());
            } else {
                sender.sendMessage(Text.COMMAND_KICK_FAIL, player.getName());
            }
        });

        return true;
    }
}
