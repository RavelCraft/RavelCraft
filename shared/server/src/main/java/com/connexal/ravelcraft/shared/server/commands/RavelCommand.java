package com.connexal.ravelcraft.shared.server.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandSubOption;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.all.text.RavelText;

public abstract class RavelCommand {
    public void execute(RavelCommandSender sender, String[] args) {
        if (this.requiresOp() && sender.isPlayer()) {
            RavelPlayer player = sender.asPlayer();
            if (!player.isOp()) {
                player.sendMessage(RavelText.COMMAND_REQUIRES_OP);
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
            sender.sendMessage(RavelText.COMMAND_HELP, "\n /" + this.getName());
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (CommandOption option : this.getOptions()) {
            this.buildUsageNode(builder, "\n -", option);
        }

        sender.sendMessage(RavelText.COMMAND_HELP, builder.toString());
    }

    private String buildName(CommandOption option) {
        return switch (option.getType()) {
            case LITERAL -> option.getName();
            case WORD -> "<" + option.getName() + ">";
            case GREEDY_STRING -> "<" + option.getName() + "...>";
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
        RavelInstance.scheduleTask(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                RavelInstance.getLogger().error("Error running command", e);
            }
        });
    }
}
