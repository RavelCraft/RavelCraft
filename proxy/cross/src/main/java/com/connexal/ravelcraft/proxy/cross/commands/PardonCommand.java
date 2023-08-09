package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.cross.servers.ban.BanManager;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

import java.util.UUID;

@AutoService(RavelCommand.class)
public class PardonCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "pardon";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("player"),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }

        this.completeAsync(() -> {
            UUID uuid = RavelInstance.getUUIDTools().getUUID(args[0]);
            if (uuid == null) {
                sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
                return;
            }

            BanManager.BanData data = RavelProxyInstance.getBanManager().isBanned(uuid);
            if (data == null) {
                sender.sendMessage(Text.COMMAND_PARDON_NOT_BANNED);
                return;
            }

            RavelProxyInstance.getBanManager().removeBan(uuid);
            sender.sendMessage(Text.COMMAND_PARDON_SUCCESS, args[0]);
        });

        return true;
    }
}
