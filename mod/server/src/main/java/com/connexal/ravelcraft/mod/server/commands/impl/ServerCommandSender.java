package com.connexal.ravelcraft.mod.server.commands.impl;

import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.all.text.Language;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ServerCommandSender implements RavelCommandSender {
    private final ServerCommandSource source;

    public ServerCommandSender(ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            throw new IllegalArgumentException("Cannot create a RavelCommandSender from a player");
        }

        this.source = source;
    }

    @Override
    public void sendMessage(RavelText message, String... values) {
        String messageString = message.getMessage(Language.DEFAULT, values);
        this.source.sendMessage(Text.literal(messageString));
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
