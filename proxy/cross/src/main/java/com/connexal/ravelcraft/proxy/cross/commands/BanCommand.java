package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.proxy.cross.servers.ban.BanManager;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

import java.util.UUID;

@AutoService(RavelCommand.class)
public class BanCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("player", CommandOption.word("days", CommandOption.greedyString("reason"))),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length < 3) {
            return false;
        }

        this.completeAsync(() -> {
            UUID uuid = RavelInstance.getUUIDTools().getUUID(args[0]);
            if (uuid == null) {
                sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
                return;
            }

            BanManager.BanData alreadyBanned = RavelProxyInstance.getBanManager().isBanned(uuid);
            if (alreadyBanned != null) {
                long diff = alreadyBanned.end() - System.currentTimeMillis();
                int days = (int) Math.ceil((double) diff / (24 * 60 * 60 * 1000));

                sender.sendMessage(Text.COMMAND_BAN_ALREADY_BANNED, args[0], Integer.toString(days));
                return;
            }

            long days;
            try {
                days = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Text.COMMAND_BAN_INVALID_DAYS);
                return;
            }
            if (days < 1) {
                sender.sendMessage(Text.COMMAND_BAN_INVALID_DAYS);
                return;
            }
            long end = System.currentTimeMillis() + days * 24 * 60 * 60 * 1000;

            String reason = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);

            RavelProxyInstance.getBanManager().addBan(uuid, end, reason);

            RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                RavelInstance.getPlayerManager().kick(player, BanManager.generateBanString(end, reason), true);
            }

            sender.sendMessage(Text.COMMAND_BAN_SUCCESS, args[0], Long.toString(days));
        });

        return true;
    }
}
