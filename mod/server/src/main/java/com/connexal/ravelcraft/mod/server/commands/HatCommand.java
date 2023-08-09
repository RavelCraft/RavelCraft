package com.connexal.ravelcraft.mod.server.commands;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
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
public class HatCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "hat";
    }

    @Override
    public String[] getAliases() {
        return new String[] { "headitem" };
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
        ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
        player.equipStack(EquipmentSlot.HEAD, player.getStackInHand(Hand.MAIN_HAND));
        player.setStackInHand(Hand.MAIN_HAND, head);

        return true;
    }
}
