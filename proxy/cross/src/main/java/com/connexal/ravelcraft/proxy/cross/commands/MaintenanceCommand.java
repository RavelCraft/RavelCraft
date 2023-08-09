package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

import java.util.Locale;

@AutoService(RavelCommand.class)
public class MaintenanceCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "maintenance";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.literal("on"),
                CommandOption.literal("on", CommandOption.word("server")),
                CommandOption.literal("off"),
                CommandOption.literal("off", CommandOption.word("server")),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        this.completeAsync(() -> {
            boolean enabled = args[0].equalsIgnoreCase("on");
            RavelServer server = null;

            if (args.length == 2) {
                try {
                    server = RavelServer.valueOf(args[1].toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Text.COMMAND_MAINTENANCE_INVALID_SERVER);
                    return;
                }
            }

            RavelProxyInstance.getMaintenanceManager().setEnabled(server, enabled);
            if (enabled) {
                if (server == null) {
                    sender.sendMessage(Text.COMMAND_MAINTENANCE_GLOBAL_ENABLED);
                } else {
                    sender.sendMessage(Text.COMMAND_MAINTENANCE_BACKEND_ENABLED);
                }
            } else {
                if (server == null) {
                    sender.sendMessage(Text.COMMAND_MAINTENANCE_GLOBAL_DISABLED);
                } else {
                    sender.sendMessage(Text.COMMAND_MAINTENANCE_BACKEND_DISABLED);
                }
            }
        });

        return true;
    }
}
