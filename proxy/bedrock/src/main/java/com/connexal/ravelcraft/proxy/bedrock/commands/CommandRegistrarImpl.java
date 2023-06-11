package com.connexal.ravelcraft.proxy.bedrock.commands;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.proxy.bedrock.players.BedrockRavelPlayerImpl;
import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.commands.arguments.CommandSubOption;
import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import org.cloudburstmc.protocol.bedrock.data.command.CommandEnumConstraint;
import org.cloudburstmc.protocol.bedrock.data.command.CommandEnumData;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParam;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamData;

import java.util.*;

public class CommandRegistrarImpl extends CommandRegistrar {
    public CommandRegistrarImpl() {
        super(RavelProxyInstance.class.getClassLoader());
    }

    private RavelCommandSender getSender(CommandSender sender) {
        if (sender.isPlayer()) {
            return new BedrockRavelPlayerImpl((ProxiedPlayer) sender);
        } else {
            return new ServerCommandSender(sender);
        }
    }

    private void processOption(CommandOption option, List<List<CommandParamData>> params, CommandParamData[] current) {
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
            List<CommandParamData> newParams = new ArrayList<>(List.of(current));
            newParams.add(data);
            params.add(newParams);
        }
    }

    @Override
    protected void register(RavelCommand command) {
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
            protected CommandParamData[][] buildCommandOverloads() {
                List<List<CommandParamData>> params = new ArrayList<>();

                for (CommandOption option : command.getOptions()) {
                    processOption(option, params, new CommandParamData[0]);
                }

                CommandParamData[][] out = new CommandParamData[params.size()][];
                for (int i = 0; i < params.size(); i++) {
                    out[i] = params.get(i).toArray(new CommandParamData[0]);
                }
                return out;
            }
        });
    }
}
