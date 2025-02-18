package com.connexal.ravelcraft.mod.server.commands.home;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class HomeCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("number")
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage(RavelText.COMMAND_MUST_BE_PLAYER);
            return true;
        }
        if (args.length != 1) {
            return false;
        }

        this.completeAsync(() -> {
            int number;
            try {
                number = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(RavelText.COMMAND_HOME_INVALID_NUMBER);
                return;
            }

            int max = RavelModServer.getHomeManager().getMaxHomes();
            if (number <= 0 || number > max) {
                sender.sendMessage(RavelText.COMMAND_HOME_OUT_OF_BOUNDS, Integer.toString(max));
                return;
            }

            FabricRavelPlayer player = (FabricRavelPlayer) sender;
            Location location = RavelModServer.getHomeManager().getHome(player.getUniqueID(), number);

            if (location == null) {
                player.sendMessage(RavelText.COMMAND_HOME_NOT_SET);
                return;
            }

            player.teleport(location);
            player.sendMessage(RavelText.COMMAND_HOME_TELEPORTED);
        });

        return true;
    }
}
