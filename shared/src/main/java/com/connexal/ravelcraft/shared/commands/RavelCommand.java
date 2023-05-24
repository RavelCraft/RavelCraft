package com.connexal.ravelcraft.shared.commands;

import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;

public abstract class RavelCommand {
    private final boolean requiresOp;
    private final String name;
    private final String[] aliases;

    public RavelCommand(String name, boolean requiresOp, String... aliases) {
        this.name = name;
        this.requiresOp = requiresOp;
        this.aliases = aliases;
    }

    public RavelCommand(String name, boolean requiresOp) {
        this(name, requiresOp, new String[0]);
    }

    public RavelCommand(String name) {
        this(name, false, new String[0]);
    }

    public void execute(RavelCommandSender sender, String[] args) {
        if (this.requiresOp && sender.isPlayer()) {
            RavelPlayer player = sender.asPlayer();
            if (!player.isOp()) {
                player.sendMessage(Text.COMMAND_REQUIRES_OP);
                return;
            }
        }

        if (!this.run(sender, args)) {
            this.sendUsage(sender);
        }
    }

    protected abstract boolean run(RavelCommandSender sender, String[] args);

    protected abstract void sendUsage(RavelCommandSender sender);

    public boolean requiresOp() {
        return this.requiresOp;
    }

    public String getName() {
        return this.name;
    }

    public String[] getAliases() {
        return this.aliases;
    }
}
