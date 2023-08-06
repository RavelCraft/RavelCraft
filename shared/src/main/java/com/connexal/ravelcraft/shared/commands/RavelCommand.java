package com.connexal.ravelcraft.shared.commands;

import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.commands.arguments.CommandSubOption;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;

public abstract class RavelCommand {
    public void execute(RavelCommandSender sender, String[] args) {
        if (this.requiresOp() && sender.isPlayer()) {
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

    public abstract boolean requiresOp();

    public abstract String getName();

    public abstract String[] getAliases();

    public abstract CommandOption[] getOptions();

    protected abstract boolean run(RavelCommandSender sender, String[] args);

    protected void sendUsage(RavelCommandSender sender) {
        if (this.getOptions().length == 0) {
            sender.sendMessage(Text.COMMAND_HELP, "\n /" + this.getName());
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (CommandOption option : this.getOptions()) {
            this.buildUsageNode(builder, "\n -", option);
        }

        sender.sendMessage(Text.COMMAND_HELP, builder.toString());
    }

    private String buildName(CommandOption option) {
        return switch (option.getType()) {
            case LITERAL -> option.getName();
            case WORD -> "<" + option.getName() + ">";
        };
    }

    private void buildUsageNode(StringBuilder builder, String current, CommandOption option) {
        if (option instanceof CommandSubOption subOption) {
            for (CommandOption sub : subOption.getOptions()) {
                this.buildUsageNode(builder, current + " " + this.buildName(subOption), sub);
            }
        } else {
            builder.append(current).append(" ").append(this.buildName(option));
        }
    }

    protected void completeAsync(Runnable runnable) {
        new Thread(runnable).start();
    }
}
