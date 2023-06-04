package com.connexal.ravelcraft.shared.commands.arguments;

public class CommandSubOption extends CommandOption {
    private final CommandOption[] options;

    protected CommandSubOption(Type type, String name, CommandOption... options) {
        super(type, name);
        this.options = options;
    }

    public CommandOption[] getOptions() {
        return this.options;
    }
}
