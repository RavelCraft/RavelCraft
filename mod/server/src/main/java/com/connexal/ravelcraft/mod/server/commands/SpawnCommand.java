package com.connexal.ravelcraft.mod.server.commands;

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
        RavelConfig config = RavelInstance.getConfig();

        if (args.length == 0) { //Teleport
            this.completeAsync(() -> {
                if (!config.contains("spawn.world")) {
                    player.sendMessage(Text.COMMAND_SPAWN_NOT_SET);
                    return;
                }

                double x = config.getDouble("spawn.x");
                double y = config.getDouble("spawn.y");
                double z = config.getDouble("spawn.z");
                float pitch = config.getFloat("spawn.pitch");
                float yaw = config.getFloat("spawn.yaw");
                RegistryKey<World> world = RegistryKey.of(RegistryKeys.WORLD, new Identifier(config.getString("spawn.world")));

                player.teleport(new Location(x, y, z, pitch, yaw, world));
                player.sendMessage(Text.COMMAND_SPAWN_TELEPORT);
            });
        } else if (args.length == 1 && args[0].equalsIgnoreCase("set")) { //Set
            this.completeAsync(() -> {
                if (!player.isOp()) {
                    player.sendMessage(Text.COMMAND_REQUIRES_OP);
                    return;
                }

                Location location = player.getLocation();

                config.set("spawn.world", location.getWorld().getValue().toString());
                config.set("spawn.x", location.getX());
                config.set("spawn.y", location.getY());
                config.set("spawn.z", location.getZ());
                config.set("spawn.pitch", location.getPitch());
                config.set("spawn.yaw", location.getYaw());

                config.save();
                player.sendMessage(Text.COMMAND_SPAWN_SET);
            });
        } else {
            return false;
        }

        return true;
    }
}
