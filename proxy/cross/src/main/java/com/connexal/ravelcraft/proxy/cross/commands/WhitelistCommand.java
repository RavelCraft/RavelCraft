package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Text;
import com.google.auto.service.AutoService;

import java.util.Locale;
import java.util.UUID;

@AutoService(RavelCommand.class)
public class WhitelistCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return true;
    }

    @Override
    public String getName() {
        return "whitelist";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.literal("add", CommandOption.word("player"), CommandOption.word("player", CommandOption.word("server"))),
                CommandOption.literal("remove", CommandOption.word("player"), CommandOption.word("player", CommandOption.word("server"))),
                CommandOption.literal("enable", CommandOption.word("server", CommandOption.literal("true"), CommandOption.literal("false"))),
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2 && args.length != 3) {
                return false;
            }

            this.completeAsync(() -> {
                UUID uuid = RavelInstance.getUUIDTools().getUUID(args[1]);
                if (uuid == null) {
                    sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
                    return;
                }

                RavelServer server = null;
                if (args.length == 3) {
                    try {
                        server = RavelServer.valueOf(args[2].toUpperCase(Locale.ROOT));
                    } catch (Exception e) {
                        sender.sendMessage(Text.COMMAND_WHITELIST_INVALID_SERVER);
                        return;
                    }
                }

                RavelProxyInstance.getWhitelistManager().setWhitelisted(uuid, true, server);
                if (server == null) {
                    sender.sendMessage(Text.COMMAND_WHITELIST_GLOBAL_ADD, args[1]);
                } else {
                    sender.sendMessage(Text.COMMAND_WHITELIST_BACKEND_ADD, args[1], server.getName());
                }
            });
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2 && args.length != 3) {
                return false;
            }

            this.completeAsync(() -> {
                UUID uuid = RavelInstance.getUUIDTools().getUUID(args[1]);
                if (uuid == null) {
                    sender.sendMessage(Text.COMMAND_PLAYER_NOT_FOUND);
                    return;
                }

                RavelServer server = null;
                if (args.length == 3) {
                    try {
                        server = RavelServer.valueOf(args[2].toUpperCase(Locale.ROOT));
                    } catch (Exception e) {
                        sender.sendMessage(Text.COMMAND_WHITELIST_INVALID_SERVER);
                        return;
                    }
                }

                RavelProxyInstance.getWhitelistManager().setWhitelisted(uuid, false, server);
                if (server == null) {
                    sender.sendMessage(Text.COMMAND_WHITELIST_GLOBAL_REMOVE, args[1]);
                } else {
                    sender.sendMessage(Text.COMMAND_WHITELIST_BACKEND_REMOVE, args[1], server.getName());
                }
            });
        } else if (args[0].equalsIgnoreCase("enable")) {
            if (args.length != 3) {
                return false;
            }

            this.completeAsync(() -> {
                RavelServer server;
                try {
                    server = RavelServer.valueOf(args[1].toUpperCase(Locale.ROOT));
                } catch (Exception e) {
                    sender.sendMessage(Text.COMMAND_WHITELIST_INVALID_SERVER);
                    return;
                }

                boolean enable;
                try {
                    enable = Boolean.parseBoolean(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(Text.COMMAND_WHITELIST_ENABLE_BOOL_INVALID);
                    return;
                }

                RavelProxyInstance.getWhitelistManager().setEnabled(server, enable);
                if (enable) {
                    sender.sendMessage(Text.COMMAND_WHITELIST_ENABLE_TRUE, server.getName());
                } else {
                    sender.sendMessage(Text.COMMAND_WHITELIST_ENABLE_FALSE, server.getName());
                }
            });
        } else {
            return false;
        }

        return true;
    }
}
