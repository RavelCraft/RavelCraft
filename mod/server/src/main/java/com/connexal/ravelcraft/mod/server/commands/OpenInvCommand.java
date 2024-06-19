package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.gui.PlayerInvGui;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;
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
            sender.sendMessage(RavelText.COMMAND_MUST_BE_PLAYER);
            return true;
        }
        if (args.length != 1) {
            return false;
        }

        this.completeAsync(() -> {
            UUID uuid = RavelInstance.getUUIDTools().getUUID(args[0]);
            if (uuid == null) {
                sender.sendMessage(RavelText.COMMAND_PLAYER_NOT_FOUND);
                return;
            }

            ServerPlayerEntity player = ((FabricRavelPlayer) sender).getPlayer();

            PlayerInvGui gui = new PlayerInvGui(player, PlayerInvGui.Type.PLAYER, uuid);
            gui.open();
        });

        return true;
    }
}
