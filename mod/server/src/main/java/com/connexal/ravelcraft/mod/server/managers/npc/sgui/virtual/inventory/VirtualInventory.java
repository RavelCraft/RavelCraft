package com.connexal.ravelcraft.mod.server.managers.npc.sgui.virtual.inventory;

import com.connexal.ravelcraft.mod.server.managers.npc.sgui.api.elements.GuiElementInterface;
import com.connexal.ravelcraft.mod.server.managers.npc.sgui.api.gui.SlotGuiInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public record VirtualInventory() implements Inventory {
    public static final VirtualInventory INSTANCE = new VirtualInventory();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getStack(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int index) {
        return ItemStack.EMPTY;
    }


    @Override
    public void setStack(int index, ItemStack stack) {
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
    }
}
