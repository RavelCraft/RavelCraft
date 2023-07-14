package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class RankCommand extends RavelCommand {
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
                CommandOption.word("get", CommandOption.literal("player")),
                CommandOption.word("set", CommandOption.literal("player", CommandOption.literal("rank"))),
                CommandOption.word("list"),
                CommandOption.word("reload")
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

            
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 3) {
                return false;
            }


        } else if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 3) {
                return false;
            }


        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
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
