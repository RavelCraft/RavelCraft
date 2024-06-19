package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class InfoCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[0];
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length != 0) {
            return false;
        }

        sender.sendMessage(RavelText.COMMAND_INFO, Ravel.SERVER_IP, Ravel.VERSION);
        return true;
    }
}
