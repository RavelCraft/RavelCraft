package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class FabricTestCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "fabric-test";
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
        sender.sendMessage(Text.COMMAND_INFO_MESSAGE);
        return true;
    }
}
