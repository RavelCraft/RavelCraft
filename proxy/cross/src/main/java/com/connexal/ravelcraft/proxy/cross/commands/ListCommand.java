package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.all.util.ChatColor;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

import java.util.*;

@AutoService(RavelCommand.class)
public class ListCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[0];
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length != 0) {
            return false;
        }

        this.completeAsync(() -> {
            Set<RavelPlayer> players = RavelInstance.getPlayerManager().getConnectedPlayers();
            if (players.isEmpty()) {
                sender.sendMessage(RavelText.COMMAND_LIST_NO_PLAYERS);
                return;
            }

            Map<String, List<String>> serverToPlayers = new HashMap<>();
            for (RavelPlayer player : players) {
                String server = player.getServer().getName();
                if (!serverToPlayers.containsKey(server)) {
                    serverToPlayers.put(server, new ArrayList<>());
                }

                serverToPlayers.get(server).add(player.getName());
            }

            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : serverToPlayers.entrySet()) {
                builder.append("\n");
                builder.append(ChatColor.BLUE).append("[").append(entry.getKey()).append("] ");
                builder.append(ChatColor.YELLOW).append("(").append(entry.getValue().size()).append("): ").append(ChatColor.RESET);
                builder.append(String.join(" ", entry.getValue()));
            }

            sender.sendMessage(RavelText.COMMAND_LIST_PLAYERS, builder.toString());
        });

        return true;
    }
}
