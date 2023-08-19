package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

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
            sender.sendMessage(Text.COMMAND_MUST_BE_PLAYER);
            return true;
        }

        FabricRavelPlayer player = (FabricRavelPlayer) sender;

        if (args.length == 0) { //Teleport
            this.completeAsync(() -> {
                Location spawn = RavelModServer.getSpawnManager().getSpawn();

                if (spawn == null) {
                    player.sendMessage(Text.COMMAND_SPAWN_NOT_SET);
                    return;
                }

                player.teleport(spawn);
                player.sendMessage(Text.COMMAND_SPAWN_TELEPORT);
            });
        } else if (args.length == 1 && args[0].equalsIgnoreCase("set")) { //Set
            this.completeAsync(() -> {
                if (!player.isOp()) {
                    player.sendMessage(Text.COMMAND_REQUIRES_OP);
                    return;
                }

                RavelModServer.getSpawnManager().setSpawn(player.getLocation());
                player.sendMessage(Text.COMMAND_SPAWN_SET);
            });
        } else {
            return false;
        }

        return true;
    }
}
