package com.connexal.ravelcraft.proxy.bedrock.commands;

import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.all.text.Language;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import dev.waterdog.waterdogpe.command.CommandSender;

public class ServerCommandSender implements RavelCommandSender {
    private final CommandSender sender;

    public ServerCommandSender(CommandSender sender) {
        if (sender.isPlayer()) {
            throw new IllegalArgumentException("Cannot create a RavelCommandSender from a player");
        }

        this.sender = sender;
    }

    @Override
    public void sendMessage(RavelText message, String... values) {
        String messageString = message.getMessage(Language.DEFAULT, values);
        this.sender.sendMessage(messageString);
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }
}
