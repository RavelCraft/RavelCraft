package com.connexal.ravelcraft.mod.server.commands.home;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

import java.util.UUID;

@AutoService(RavelCommand.class)
public class GetHomeCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "gethome";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("player", CommandOption.word("number"))
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length != 2) {
            return false;
        }

        this.completeAsync(() -> {
            UUID uuid = RavelInstance.getUUIDTools().getUUID(args[0]);
            if (uuid == null) {
                sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
                return;
            }

            int number;
            try {
                number = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Text.COMMAND_HOME_INVALID_NUMBER);
                return;
            }

            int max = RavelModServer.getHomeManager().getMaxHomes();
            if (number <= 0 || number > max) {
                sender.sendMessage(Text.COMMAND_HOME_OUT_OF_BOUNDS, Integer.toString(max));
                return;
            }

            Location location = RavelModServer.getHomeManager().getHome(uuid, number);
            if (location == null) {
                sender.sendMessage(Text.COMMAND_HOME_NOT_SET);
                return;
            }

            sender.sendMessage(Text.COMMAND_HOME_GET, args[0], Integer.toString(number), location.chatFormat());
        });

        return true;
    }
}
