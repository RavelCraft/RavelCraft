package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class BroadcastCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "broadcast";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.greedyString("message"),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String message = String.join(" ", args);
        this.completeAsync(() -> {
            RavelInstance.getPlayerManager().broadcast(RavelText.COMMAND_BROADCAST, message);

            if (!sender.isPlayer()) {
                sender.sendMessage(RavelText.COMMAND_BROADCAST, message);
            }
        });

        return true;
    }
}
