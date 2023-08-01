package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class BackendServerCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "backendserver";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("server"),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        RavelServer server;
        try {
            server = RavelServer.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Text.COMMAND_SERVER_INVALID);
            return true;
        }

        boolean success = RavelInstance.getPlayerManager().transferPlayerToServer(sender.asPlayer(), server);
        if (!success) {
            sender.sendMessage(Text.COMMAND_SERVER_FAIL_SLEF, server.getName());
        }
        return true;
    }
}
