package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.util.text.Text;

public class InfoCommand extends RavelCommand {
    public InfoCommand(String name) {
        super("info");
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        sender.sendMessage(Text.COMMAND_INFO_MESSAGE);
        return true;
    }

    @Override
    protected void sendUsage(RavelCommandSender sender) {
    }
}
