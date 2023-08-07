package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class MotdCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "setmotd";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "motd" };
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("message"),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Text.COMMAND_MOTD_GET, RavelProxyInstance.getMotdManager().getMotd());
            return true;
        }

        String message = String.join(" ", args);
        this.completeAsync(() -> RavelProxyInstance.getMotdManager().setMotd(message));

        sender.sendMessage(Text.COMMAND_MOTD_SET, message);
        return true;
    }
}
