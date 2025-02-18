package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.map.MapUtils;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

@AutoService(RavelCommand.class)
public class MapCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.literal("create", CommandOption.greedyString("url")),
                CommandOption.literal("get", CommandOption.word("id"))
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage(RavelText.COMMAND_MUST_BE_PLAYER);
            return true;
        }

        if (args.length < 2) {
            return false;
        }
        boolean create = args[0].equalsIgnoreCase("create");
        if (!create && !args[0].equalsIgnoreCase("get")) {
            return false;
        }

        this.completeAsync(() -> {
            MapIdComponent mapIdComponent;
            if (create) {
                String mapUrl = String.join(" ", args);
                mapUrl = mapUrl.substring(7); //Remove "create" from the string

                //Download image from server
                BufferedImage image;
                try {
                    image = ImageIO.read(new URL(mapUrl));
                } catch (Exception e) {
                    sender.sendMessage(RavelText.COMMAND_MAP_INVALID_URL);
                    return;
                }

                //Create map
                ServerWorld world = RavelModServer.getServer().getOverworld();
                mapIdComponent = world.increaseAndGetMapId();
                MapState mapState = MapState.of(0, 0, (byte) 0, false, false, RegistryKey.of(RegistryKeys.WORLD, Identifier.of(Ravel.ID, "custom_map")));

                MapUtils.applyImageToMap(mapState, image, true);
                world.putMapState(mapIdComponent, mapState);
            } else {
                try {
                    mapIdComponent = new MapIdComponent(Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(RavelText.COMMAND_MAP_NAN);
                    return;
                }

                //Check if map exists
                ServerWorld world = RavelModServer.getServer().getOverworld();
                if (world.getMapState(mapIdComponent) == null) {
                    sender.sendMessage(RavelText.COMMAND_MAP_INVALID_ID);
                    return;
                }
            }

            //Give map to player
            ItemStack map = new ItemStack(Items.FILLED_MAP);
            map.set(DataComponentTypes.MAP_ID, mapIdComponent);

            ((FabricRavelPlayer) sender).getPlayer().giveItemStack(map);
            sender.sendMessage(RavelText.COMMAND_MAP_DONE);
        });

        return true;
    }
}
