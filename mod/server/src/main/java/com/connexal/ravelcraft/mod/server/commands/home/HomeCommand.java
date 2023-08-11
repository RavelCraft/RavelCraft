package com.connexal.ravelcraft.mod.server.commands.home;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@AutoService(RavelCommand.class)
public class HomeCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("number")
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage(Text.COMMAND_MUST_BE_PLAYER);
            return true;
        }
        if (args.length != 1) {
            return false;
        }

        this.completeAsync(() -> {
            int number;
            try {
                number = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Text.COMMAND_HOME_INVALID_NUMBER);
                return;
            }

            int max = RavelModServer.getHomeManager().getMaxHomes();
            if (number <= 0 || number > max) {
                sender.sendMessage(Text.COMMAND_HOME_OUT_OF_BOUNDS, Integer.toString(max));
                return;
            }

            FabricRavelPlayer player = (FabricRavelPlayer) sender;
            Location location = RavelModServer.getHomeManager().getHome(player.getUniqueID(), number);

            if (location == null) {
                player.sendMessage(Text.COMMAND_HOME_NOT_SET);
                return;
            }

            player.teleport(location);
            player.sendMessage(Text.COMMAND_HOME_TELEPORTED);
        });

        return true;
    }
}
