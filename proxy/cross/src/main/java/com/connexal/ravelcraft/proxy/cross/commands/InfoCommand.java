package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
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
        return new String[] { "i" };
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("player", CommandOption.word("magic")),
                CommandOption.literal("hello")
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        sender.sendMessage(Text.COMMAND_INFO);
        return true;
    }
}
