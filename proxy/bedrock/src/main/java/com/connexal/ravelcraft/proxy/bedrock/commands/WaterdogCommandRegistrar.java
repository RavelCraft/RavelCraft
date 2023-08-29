package com.connexal.ravelcraft.proxy.bedrock.commands;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.commands.arguments.CommandSubOption;
import com.connexal.ravelcraft.shared.util.uuid.UUIDTools;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import org.cloudburstmc.protocol.bedrock.data.command.*;

import java.util.*;

public class WaterdogCommandRegistrar extends CommandRegistrar {
    public WaterdogCommandRegistrar() {
        super(RavelProxyInstance.class.getClassLoader());
    }

    private RavelCommandSender getSender(CommandSender sender) {
        if (sender.isPlayer()) {
            UUID uuid = UUIDTools.getJavaUUIDFromXUID(((ProxiedPlayer) sender).getXuid());
            return RavelInstance.getPlayerManager().getPlayer(uuid);
        } else {
            return new ServerCommandSender(sender);
        }
    }

    private void processOption(CommandOption option, List<CommandOverloadData> params, CommandParamData[] current) {
        CommandParamData data = new CommandParamData();
        data.setName(option.getName());
        data.setOptional(true);

        switch (option.getType()) {
            case LITERAL -> {
                Map<String, Set<CommandEnumConstraint>> map = new HashMap<>();
                map.put(option.getName(), Set.of());

                CommandEnumData enumData = new CommandEnumData(option.getName(), map, false);
                data.setEnumData(enumData);
            }
            case WORD -> data.setType(CommandParam.TEXT);
            case GREEDY_STRING -> data.setType(CommandParam.TEXT); //Shouldn't be an issue
            default -> throw new IllegalStateException("Command option not understood");
        }

        if (option instanceof CommandSubOption subOption) {
            for (CommandOption sub : subOption.getOptions()) {
                CommandParamData[] newCurrent = new CommandParamData[current.length + 1];
                System.arraycopy(current, 0, newCurrent, 0, current.length);
                newCurrent[current.length] = data;

                processOption(sub, params, newCurrent);
            }
        } else {
            params.add(new CommandOverloadData(false, current));
        }
    }

    @Override
    protected void register(RavelCommand command) {
        BeProxy.getServer().getCommandMap().unregisterCommand(command.getName());
        for (String alias : command.getAliases()) {
            BeProxy.getServer().getCommandMap().unregisterCommand(alias);
        }

        CommandSettings settings = CommandSettings.builder()
                .setAliases(command.getAliases())
                .setQuoteAware(false)
                .build();

        BeProxy.getServer().getCommandMap().registerCommand(new Command(command.getName(), settings) {
            @Override
            public boolean onExecute(CommandSender sender, String alias, String[] args) {
                RavelCommandSender ravelSender = getSender(sender);
                command.execute(ravelSender, args);
                return true;
            }

            @Override
            protected CommandOverloadData[] buildCommandOverloads() {
                List<CommandOverloadData> params = new ArrayList<>();

                for (CommandOption option : command.getOptions()) {
                    processOption(option, params, new CommandParamData[0]);
                }

                return params.toArray(new CommandOverloadData[0]);
            }
        });
    }
}
