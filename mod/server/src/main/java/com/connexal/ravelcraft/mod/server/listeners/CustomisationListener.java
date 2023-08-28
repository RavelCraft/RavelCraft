package com.connexal.ravelcraft.mod.server.listeners;

import com.connexal.ravelcraft.mod.server.util.events.EntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CustomisationListener {
    public static void register() {
        EntityEvents.DROP_LOOT.register(CustomisationListener::doubleShulkerShells);
    }

    private static boolean doubleShulkerShells(LivingEntity livingEntity, DamageSource source) {
        if (livingEntity instanceof ShulkerEntity) {
            livingEntity.dropStack(new ItemStack(Items.SHULKER_SHELL, 2));
            return false;
        }

        return true;
    }

    //FarmlandBlockMixin: remove trampling

    //ItemFrameEntityMixin: invisible item frames
}
