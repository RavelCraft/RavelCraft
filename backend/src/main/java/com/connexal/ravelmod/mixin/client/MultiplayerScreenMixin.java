package com.connexal.ravelmod.mixin.client;

import com.connexal.ravelmod.cracked.CrackedLoginScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    public void init(CallbackInfo callback) {
        if (!this.client.getSession().getProfile().getName().startsWith("*")) {
            return;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Login to RavelCraft"), (buttonWidget) -> {
            this.client.setScreen(new CrackedLoginScreen(this));
        }).dimensions(5, 5, 150, 20).build());
    }
}