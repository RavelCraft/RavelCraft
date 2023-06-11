package com.connexal.ravelcraft.mod.client.cracked;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class CrackedLoginScreen extends Screen {
    private ButtonWidget joinServerButton;
    private TextFieldWidget usernameField;
    private TextFieldWidget passwordField;
    private final Screen parent;

    public CrackedLoginScreen(Screen parent) {
        super(Text.literal("Cracked Login"));
        this.parent = parent;
    }

    @Override
    public void init() {
        this.joinServerButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Submit"), (button) -> {
            String out = CrackedManager.login(this.usernameField.getText(), this.passwordField.getText(), this.client.getSession());
            if (out != null) {
                this.client.setScreen(new DisconnectedScreen(this.parent, Text.literal("Failed to authenticate"), Text.literal(out)));
            } else {
                this.client.setScreen(this.parent);
            }
        }).dimensions(this.width / 2 - 102, this.height / 4 + 100 + 12, 204, 20).build());

        this.usernameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, (this.height / 4) + 16, 200, 20, Text.literal("Enter username"));
        this.usernameField.setFocused(true);
        this.usernameField.setMaxLength(16);
        this.addDrawableChild(this.usernameField);

        this.passwordField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, (this.height / 4) + 46, 200, 20, Text.literal("Enter password"));
        this.passwordField.setFocused(false);
        this.addDrawableChild(this.passwordField);

        this.setInitialFocus(this.usernameField);
    }

    @Override
    public void tick() {
        this.usernameField.tick();
        this.passwordField.tick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Enter username and password"), this.width / 2 - 100, this.height / 4, 10526880);
        this.usernameField.render(context, mouseX, mouseY, delta);
        this.passwordField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String username = this.usernameField.getText();
        String password = this.passwordField.getText();
        this.init(client, width, height);
        this.usernameField.setText(username);
        this.passwordField.setText(password);
    }
}
