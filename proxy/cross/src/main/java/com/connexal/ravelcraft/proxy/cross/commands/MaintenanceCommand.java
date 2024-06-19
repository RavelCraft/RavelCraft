package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.RavelText;
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
                    sender.sendMessage(RavelText.COMMAND_MAINTENANCE_INVALID_SERVER);
                    return;
                }
            }

            RavelProxyInstance.getMaintenanceManager().setEnabled(server, enabled);
            if (enabled) {
                if (server == null) {
                    sender.sendMessage(RavelText.COMMAND_MAINTENANCE_GLOBAL_ENABLED);
                } else {
                    sender.sendMessage(RavelText.COMMAND_MAINTENANCE_BACKEND_ENABLED, server.getName());
                }
            } else {
                if (server == null) {
                    sender.sendMessage(RavelText.COMMAND_MAINTENANCE_GLOBAL_DISABLED);
                } else {
                    sender.sendMessage(RavelText.COMMAND_MAINTENANCE_BACKEND_DISABLED, server.getName());
                }
            }
        });

        return true;
    }
}
