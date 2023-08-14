package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.managers.npc.NpcManager;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;
import net.minecraft.server.world.ServerWorld;

@AutoService(RavelCommand.class)
public class NpcCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "npc";
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
        if (!sender.isPlayer()) {
            sender.sendMessage(Text.COMMAND_MUST_BE_PLAYER);
            return true;
        }
        if (args.length != 0) {
            return false;
        }

        this.completeAsync(() -> {
            FabricRavelPlayer player = (FabricRavelPlayer) sender;
            NpcManager.createNpc((ServerWorld) player.getPlayer().getWorld(), "MOGUS", player.getLocation(), "http://textures.minecraft.net/texture/f489ad230d8ca79f3ded9f3a72c48143da46c9e8eb1ff581029d2444139bf908");
        });

        return true;
    }
}
