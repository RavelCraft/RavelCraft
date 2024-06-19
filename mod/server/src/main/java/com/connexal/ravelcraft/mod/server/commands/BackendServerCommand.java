package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

import java.util.Locale;

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
            server = RavelServer.valueOf(args[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(RavelText.COMMAND_SERVER_INVALID);
            return true;
        }

        boolean success = RavelInstance.getPlayerManager().transferPlayerToServer(sender.asPlayer(), server);
        if (!success) {
            sender.sendMessage(RavelText.COMMAND_SERVER_FAIL_SLEF, server.getName());
        }
        return true;
    }
}
