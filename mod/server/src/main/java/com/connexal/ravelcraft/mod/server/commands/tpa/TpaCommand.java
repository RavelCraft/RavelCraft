package com.connexal.ravelcraft.mod.server.commands.tpa;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.managers.TpaManager;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

@AutoService(RavelCommand.class)
public class TpaCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "tpa";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
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
            FabricRavelPlayer source = (FabricRavelPlayer) sender.asPlayer();
            RavelPlayer target = RavelInstance.getPlayerManager().getPlayer(args[0]);

            if (target == null) {
                source.sendMessage(RavelText.COMMAND_PLAYER_NOT_ONLINE);
                return;
            }

            RavelModServer.getTpaManager().queueRequest(source, (FabricRavelPlayer) target, TpaManager.TpaType.SENDER_TO_RECEIVER);
        });

        return true;
    }
}
