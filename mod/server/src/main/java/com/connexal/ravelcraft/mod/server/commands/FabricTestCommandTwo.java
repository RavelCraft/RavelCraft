package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class FabricTestCommandTwo extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "fabric-test-two";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "ft2" };
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.literal("test", CommandOption.word("woo")),
                CommandOption.word("magic"),
                CommandOption.literal("test2")
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        sender.sendMessage(Text.COMMAND_INFO_MESSAGE);
        return false;
    }
}
