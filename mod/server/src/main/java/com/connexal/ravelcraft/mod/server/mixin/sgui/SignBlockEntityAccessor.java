package com.connexal.ravelcraft.mod.server.mixin.sgui;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
public interface SignBlockEntityAccessor {

    @Accessor
    void setFrontText(SignText frontText);

    @Accessor
    void setBackText(SignText backText);

}
