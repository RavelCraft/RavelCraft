package com.connexal.ravelcraft.proxy.cross.commands;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.commands.RavelCommand;
import com.connexal.ravelcraft.shared.commands.RavelCommandSender;
import com.connexal.ravelcraft.shared.commands.arguments.CommandOption;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;
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
            sender.sendMessage(Text.COMMAND_MUST_BE_PLAYER);
            return true;
        }

        if (args.length == 0) { //List the available languages
            StringBuilder builder = new StringBuilder();
            for (Language language : Language.values()) {
                builder.append("\n - ").append(language.getCode().toUpperCase(Locale.ROOT));
            }

            sender.sendMessage(Text.COMMAND_LANGUAGE_LIST, builder.toString());
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
                sender.sendMessage(Text.COMMAND_LANGUAGE_INVALID);
                return true;
            }

            RavelInstance.getPlayerManager().languageUpdate(sender.asPlayer().getUniqueID(), language);
            sender.sendMessage(Text.COMMAND_LANGUAGE_SET, language.name());
        }

        return true;
    }
}
