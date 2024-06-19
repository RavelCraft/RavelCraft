package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class SpawnCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.literal("set")
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage(RavelText.COMMAND_MUST_BE_PLAYER);
            return true;
        }

        FabricRavelPlayer player = (FabricRavelPlayer) sender;

        if (args.length == 0) { //Teleport
            this.completeAsync(() -> {
                Location spawn = RavelModServer.getSpawnManager().getSpawn();

                if (spawn == null) {
                    player.sendMessage(RavelText.COMMAND_SPAWN_NOT_SET);
                    return;
                }

                player.teleport(spawn);
                player.sendMessage(RavelText.COMMAND_SPAWN_TELEPORT);
            });
        } else if (args.length == 1 && args[0].equalsIgnoreCase("set")) { //Set
            this.completeAsync(() -> {
                if (!player.isOp()) {
                    player.sendMessage(RavelText.COMMAND_REQUIRES_OP);
                    return;
                }

                RavelModServer.getSpawnManager().setSpawn(player.getLocation());
                player.sendMessage(RavelText.COMMAND_SPAWN_SET);
            });
        } else {
            return false;
        }

        return true;
    }
}
