package com.connexal.ravelcraft.mod.server.util.gui;

import com.connexal.ravelcraft.mod.server.listeners.ChatMessageCatcher;
import com.connexal.ravelcraft.mod.server.managers.npc.NpcEntity;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.ChatColor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class NpcGui extends MenuGui {
    private static final ItemStack[] displayItems;
    static {
        ItemStack runAction = Items.COMMAND_BLOCK.getDefaultStack();
        runAction.set(DataComponentTypes.CUSTOM_NAME, Text.of(ChatColor.WHITE + "Run Action"));

        ItemStack remove = Items.BARRIER.getDefaultStack();
        remove.set(DataComponentTypes.CUSTOM_NAME, Text.of(ChatColor.WHITE + "Remove NPC"));

        ItemStack rename = Items.OAK_SIGN.getDefaultStack();
        rename.set(DataComponentTypes.CUSTOM_NAME, Text.of(ChatColor.WHITE + "Rename NPC"));

        ItemStack setCommand = Items.DEBUG_STICK.getDefaultStack();
        setCommand.set(DataComponentTypes.CUSTOM_NAME, Text.of(ChatColor.WHITE + "Set Command"));

        ItemStack setMessage = Items.WRITABLE_BOOK.getDefaultStack();
        setMessage.set(DataComponentTypes.CUSTOM_NAME, Text.of(ChatColor.WHITE + "Set Message"));

        ItemStack setSkin = Items.PLAYER_HEAD.getDefaultStack();
        setSkin.set(DataComponentTypes.CUSTOM_NAME, Text.of(ChatColor.WHITE + "Set Skin"));

        displayItems = new ItemStack[] {
                runAction,
                remove,
                rename,
                setCommand,
                setMessage,
                setSkin
        };
    }

    private final NpcEntity npc;

    public NpcGui(ServerPlayerEntity player, NpcEntity npc) {
        super(player, displayItems, "Edit NPC");
        this.setClick(this::itemClicked);

        this.npc = npc;
    }

    private String catchChat() {
        try {
            CompletableFuture<String> catcher = ChatMessageCatcher.registerCatch(this.player.getUuid());
            return catcher.join();
        } catch (Exception e) {
            this.player.sendMessage(Text.of(ChatColor.RED + "You already have something to answer!"));
            return null;
        }
    }

    private void itemClicked(ItemStack item) {
        String itemName = item.getName().getString();

        RavelInstance.scheduleTask(() -> {
            switch (itemName) {
                case ChatColor.WHITE + "Run Action": {
                    this.npc.runInteraction(this.player);
                    break;
                }
                case ChatColor.WHITE + "Remove NPC": {
                    this.npc.remove(Entity.RemovalReason.KILLED);
                    break;
                }
                case ChatColor.WHITE + "Rename NPC": {
                    this.player.sendMessage(Text.of("Enter a new name for the NPC in chat:"));
                    String newName = this.catchChat().replace("&", "§");

                    this.npc.setCustomName(Text.of(newName));
                    this.player.sendMessage(Text.of("NPC name changed! You may need to rejoin."));
                    break;
                }
                case ChatColor.WHITE + "Set Command": {
                    this.player.sendMessage(Text.of("Enter a new command for the NPC in chat (without the /):"));
                    this.npc.setInteraction(1, this.catchChat());
                    this.player.sendMessage(Text.of("NPC command changed!"));
                    break;
                }
                case ChatColor.WHITE + "Set Message": {
                    this.player.sendMessage(Text.of("Enter a new message for the NPC in chat:"));
                    String message = this.catchChat().replace("&", "§");

                    this.npc.setInteraction(0, message);
                    this.player.sendMessage(Text.of("NPC message changed!"));
                    break;
                }
                case ChatColor.WHITE + "Set Skin": {
                    this.player.sendMessage(Text.of("Enter the name of a player in chat to use their skin:"));
                    String playerName = this.catchChat();
                    this.player.sendMessage(Text.of("You chose to use " + playerName + "'s skin. We don't support this yet."));
                    break;
                }
            }
        });
    }
}
