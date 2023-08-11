package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.gui.PlayerInvGui;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

@AutoService(RavelCommand.class)
public class OpenInvCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "openinv";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "inv" };
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("player")
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
            UUID uuid = RavelInstance.getUUIDTools().getUUID(args[0]);
            if (uuid == null) {
                sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
                return;
            }

            ServerPlayerEntity player = ((FabricRavelPlayer) sender).getPlayer();

            PlayerInvGui gui = new PlayerInvGui(ScreenHandlerType.GENERIC_9X5, player, uuid);
            gui.open();
        });

        return true;
    }
}
