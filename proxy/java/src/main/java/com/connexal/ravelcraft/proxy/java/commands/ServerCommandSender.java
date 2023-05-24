package com.connexal.ravelcraft.proxy.java.commands;

import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public class ServerCommandSender implements RavelCommandSender {
    private final CommandSource source;

    public ServerCommandSender(CommandSource source) {
        if (source instanceof Player) {
            throw new IllegalArgumentException("Cannot create a RavelCommandSender from a player");
        }

        this.source = source;
    }

    @Override
    public void sendMessage(Text message, String... values) {
        String messageString = message.getMessage(Language.DEFAULT, values);
        this.source.sendMessage(Component.text(messageString));
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
