package com.connexal.ravelcraft.mod.server.util.gui;

import com.connexal.ravelcraft.mod.server.libs.sgui.api.elements.GuiElement;
import com.connexal.ravelcraft.mod.server.libs.sgui.api.elements.GuiElementBuilder;
import com.connexal.ravelcraft.mod.server.libs.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Consumer;

public class MenuGui extends SimpleGui {
    private final ItemStack[] displayItems;
    private final String title;

    private Consumer<ItemStack> itemClicked = null;

    private int currentPage;
    private final int totalPages;
    private static final int maxItemsPerPage = 36; // 9 * 4

    public MenuGui(ServerPlayerEntity player, ItemStack[] displayItems, String title) {
        super(ScreenHandlerType.GENERIC_9X5, player, false);

        this.displayItems = displayItems;
        this.title = title;

        this.currentPage = 0;
        this.totalPages = (int) Math.ceil((double) (this.displayItems.length / maxItemsPerPage));

        this.updateDisplay();
    }

    public void setClick(Consumer<ItemStack> itemClicked) {
        this.itemClicked = itemClicked;
    }

    private void updateDisplay() {
        //Empty the gui
        for (int i = 0; i < 45; i++) {
            this.clearSlot(i);
        }

        //Fill with the page's items - make sure to always copy the item stacks
        for (int i = 0; i < maxItemsPerPage; i++) {
            int index = this.currentPage * maxItemsPerPage + i;

            if (index >= this.displayItems.length) {
                this.setSlot(i, ItemStack.EMPTY);
            } else {
                this.setSlot(i, new GuiElement(this.displayItems[index].copy(), (itemIndex, type, action) -> {
                    int indexInArray = this.currentPage * maxItemsPerPage + itemIndex;

                    if (indexInArray < this.displayItems.length) {
                        this.itemClicked.accept(this.displayItems[indexInArray].copy());
                        this.close();
                    }
                }));
            }
        }

        //Add navigation buttons
        if (this.currentPage > 0) {
            this.setSlot(37, new GuiElementBuilder(Items.ARROW)
                    .setName(net.minecraft.text.Text.literal("Previous"))
                    .setCallback((index, type, action) -> {
                        this.previousPage();
                    })
                    .setCount(this.currentPage) //We don't need to add 1 because we start at 0
            );
        }
        this.setSlot(40, new GuiElementBuilder(Items.BARRIER)
                .setName(net.minecraft.text.Text.literal("Close"))
                .setCallback((index, type, action) -> {
                    this.close();
                })
        );
        if (this.currentPage < this.totalPages) {
            this.setSlot(43, new GuiElementBuilder(Items.ARROW)
                    .setName(net.minecraft.text.Text.literal("Next"))
                    .setCallback((index, type, action) -> {
                        this.nextPage();
                    })
                    .setCount(this.currentPage + 2) //We need to add 2 because we start at 0
            );
        }

        //Set title
        this.setTitle(net.minecraft.text.Text.literal(this.title + " - " + (this.currentPage + 1) + "/" + (this.totalPages + 1)));
    }

    private void nextPage() {
        this.currentPage++;
        this.updateDisplay();
    }

    private void previousPage() {
        this.currentPage--;
        this.updateDisplay();
    }
}
