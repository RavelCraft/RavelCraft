package com.connexal.ravelcraft.mod.server.util.events;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.Location;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemEvents {
    @FunctionalInterface
    public interface BucketFillEvent {
        void onBucketFill(FabricRavelPlayer player, BlockPos blockPos, ItemStack itemStack);
    }
    public static final Event<BucketFillEvent> BUCKET_FILL = EventFactory.createArrayBacked(BucketFillEvent.class, listeners -> (player, blockPos, itemStack) -> {
        for (BucketFillEvent listener : listeners) {
            listener.onBucketFill(player, blockPos, itemStack);
        }
    });

    @FunctionalInterface
    public interface BucketEmptyEvent {
        void onBucketEmpty(FabricRavelPlayer player, BlockPos blockPos, ItemStack itemStack);
    }
    public static final Event<BucketEmptyEvent> BUCKET_EMPTY = EventFactory.createArrayBacked(BucketEmptyEvent.class, listeners -> (player, blockPos, itemStack) -> {
        for (BucketEmptyEvent listener : listeners) {
            listener.onBucketEmpty(player, blockPos, itemStack);
        }
    });

    @FunctionalInterface
    public interface ItemPickupEvent {
        void onItemPickup(FabricRavelPlayer player, ItemStack itemStack, Location location);
    }
    public static final Event<ItemPickupEvent> ITEM_PICKUP = EventFactory.createArrayBacked(ItemPickupEvent.class, listeners -> (player, itemStack, location) -> {
        for (ItemPickupEvent listener : listeners) {
            listener.onItemPickup(player, itemStack, location);
        }
    });

    @FunctionalInterface
    public interface ItemDropEvent {
        void onItemDrop(FabricRavelPlayer player, ItemStack itemStack, Location location);
    }
    public static final Event<ItemDropEvent> ITEM_DROP = EventFactory.createArrayBacked(ItemDropEvent.class, listeners -> (player, itemStack, location) -> {
        for (ItemDropEvent listener : listeners) {
            listener.onItemDrop(player, itemStack, location);
        }
    });

    @FunctionalInterface
    public interface ItemContainerMove {
        void onItemMove(FabricRavelPlayer player, ItemStack srcItemStack, Inventory srcInventory, ItemStack destItemStack, Inventory destInventory);
    }
    public static final Event<ItemContainerMove> CONTAINER_MOVE = EventFactory.createArrayBacked(ItemContainerMove.class, listeners -> (player, srcItemStack, srcInventory, destItemStack, destInventory) -> {
        for (ItemContainerMove listener : listeners) {
            listener.onItemMove(player, srcItemStack, srcInventory, destItemStack, destInventory);
        }
    });
}
