package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class MessageCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "msg";
    }

    @Override
    public String[] getAliases() {
        return new String[] {
                "tell"
        };
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("player", CommandOption.greedyString("message"))
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        this.completeAsync(() -> {
            RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(RavelText.COMMAND_PLAYER_NOT_ONLINE);
                return;
            }

            String name;
            if (sender.isPlayer()) {
                name = sender.asPlayer().buildDisplayName();
            } else {
                name = "§6Server";
            }

            String message = String.join(" ", args).substring(args[0].length() + 1);

            player.sendMessage(RavelText.COMMAND_MSG_RECEIVER, name, message);
            sender.sendMessage(RavelText.COMMAND_MSG_SENDER, player.buildDisplayName(), message);
        });

        return true;
    }
}
