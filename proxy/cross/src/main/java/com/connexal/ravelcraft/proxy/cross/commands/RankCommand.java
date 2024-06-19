package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.players.PlayerManager;
import com.connexal.ravelcraft.shared.server.players.RavelRank;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

import java.util.Locale;
import java.util.UUID;

@AutoService(RavelCommand.class)
public class RankCommand extends RavelCommand {
    private final PlayerManager playerManager;

    public RankCommand() {
        this.playerManager = RavelInstance.getPlayerManager();
    }

    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.literal("get", CommandOption.word("player")),
                CommandOption.literal("set", CommandOption.word("player", CommandOption.word("rank"))),
                CommandOption.literal("list")
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("get")) {
            if (args.length != 2) {
                return false;
            }

            this.completeAsync(() -> {
                UUID uuid = RavelInstance.getUUIDTools().getUUID(args[1]);
                if (uuid == null) {
                    sender.sendMessage(RavelText.COMMAND_PLAYER_NOT_FOUND);
                    return;
                }

                PlayerManager.PlayerSettings settings = this.playerManager.getPlayerSettings(uuid, false);
                sender.sendMessage(RavelText.COMMAND_RANK_GET, args[1], settings.rank().getName());
            });
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 3) {
                return false;
            }

            this.completeAsync(() -> {
                UUID uuid = RavelInstance.getUUIDTools().getUUID(args[1]);
                if (uuid == null) {
                    sender.sendMessage(RavelText.COMMAND_PLAYER_NOT_FOUND);
                    return;
                }

                RavelRank rank;
                try {
                    rank = RavelRank.valueOf(args[2].toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(RavelText.COMMAND_RANK_INVALID);
                    return;
                }

                RavelInstance.getPlayerManager().rankUpdate(uuid, rank);
                sender.sendMessage(RavelText.COMMAND_RANK_SET, args[1], rank.getName());
            });
        } else if (args[0].equalsIgnoreCase("list")) {
            if (args.length != 1) {
                return false;
            }

            this.completeAsync(() -> {
                StringBuilder builder = new StringBuilder();
                for (RavelRank rank : RavelRank.values()) {
                    if (rank == RavelRank.NONE) {
                        continue;
                    }

                    builder.append("\n - ").append(rank.getName());
                    if (rank.isOperator()) {
                        builder.append(" (OP)");
                    }
                }

                sender.sendMessage(RavelText.COMMAND_RANK_LIST, builder.toString());
            });
        } else {
            return false;
        }

        return true;
    }
}
