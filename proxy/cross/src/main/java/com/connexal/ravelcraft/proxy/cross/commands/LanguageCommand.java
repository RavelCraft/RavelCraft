package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.commands.RavelCommand;
import com.connexal.ravelcraft.shared.server.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.server.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.all.text.Language;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.google.auto.service.AutoService;

import java.util.Locale;

@AutoService(RavelCommand.class)
public class LanguageCommand extends RavelCommand {
    @Override
    public boolean requiresOp() {
        return false;
    }

    @Override
    public String getName() {
        return "language";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public CommandOption[] getOptions() {
        return new CommandOption[] {
                CommandOption.word("code")
        };
    }

    @Override
    protected boolean run(RavelCommandSender sender, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage(RavelText.COMMAND_MUST_BE_PLAYER);
            return true;
        }

        this.completeAsync(() -> {
            if (args.length == 0) { //List the available languages
                StringBuilder builder = new StringBuilder();
                for (Language language : Language.values()) {
                    builder.append("\n - ").append(language.getCode().toUpperCase(Locale.ROOT));
                }

                sender.sendMessage(RavelText.COMMAND_LANGUAGE_LIST, builder.toString());
            } else { //Set the language
                String code = args[0];
                Language language = null;
                for (Language lang : Language.values()) {
                    if (lang.getCode().equalsIgnoreCase(code)) {
                        language = lang;
                        break;
                    }
                }
                if (language == null) {
                    sender.sendMessage(RavelText.COMMAND_LANGUAGE_INVALID);
                    return;
                }

                RavelInstance.getPlayerManager().languageUpdate(sender.asPlayer().getUniqueID(), language);
                sender.sendMessage(RavelText.COMMAND_LANGUAGE_SET, language.name());
            }
        });

        return true;
    }
}
