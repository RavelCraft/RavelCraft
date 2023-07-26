package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

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
        return new String[] { "ranks" };
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.literal("get", CommandOption.word("player")),
                CommandOption.literal("set", CommandOption.word("player", CommandOption.word("rank"))),
                CommandOption.literal("list"),
                CommandOption.literal("reload")
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

            UUID uuid = RavelInstance.getUUIDTools().getUUID(args[1]);
            if (uuid == null) {
                sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
                return true;
            }

            //TODO
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 3) {
                return false;
            }


        } else if (args[0].equalsIgnoreCase("list")) {
            if (args.length != 1) {
                return false;
            }


        } else if (args[0].equalsIgnoreCase("reload")) {
            if (args.length != 1) {
                return false;
            }


        } else {
            return false;
        }

        return true;
    }
}
