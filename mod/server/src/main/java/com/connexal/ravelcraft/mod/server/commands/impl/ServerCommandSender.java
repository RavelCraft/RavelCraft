package com.connexal.ravelcraft.mod.server.commands.impl;

import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;
import net.minecraft.server.command.ServerCommandSource;

public class ServerCommandSender implements RavelCommandSender {
    private final ServerCommandSource source;

    public ServerCommandSender(ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            throw new IllegalArgumentException("Cannot create a RavelCommandSender from a player");
        }

        this.source = source;
    }

    @Override
    public void sendMessage(Text message, String... values) {
        String messageString = message.getMessage(Language.DEFAULT, values);
        this.source.sendMessage(net.minecraft.text.Text.literal(messageString));
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
