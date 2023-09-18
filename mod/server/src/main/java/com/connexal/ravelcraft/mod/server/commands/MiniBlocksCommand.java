package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.gui.MenuGui;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@AutoService(RavelCommand.class)
public class MiniBlocksCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "miniblocks";
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

        ServerPlayerEntity player = ((FabricRavelPlayer) sender).getPlayer();

        MenuGui gui = new MenuGui(player, RavelModServer.getMiniBlockManager().getMiniBlocks().toArray(new ItemStack[0]), "Mini Blocks");
        gui.setClick(item -> {
            player.getInventory().offerOrDrop(item);
        });
        gui.open();

        return true;
    }
}
