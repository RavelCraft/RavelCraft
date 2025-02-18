package com.connexal.ravelcraft.mod.cross.types.items.sets;

import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.MiningLevel;
import com.connexal.ravelcraft.mod.cross.types.items.polymer.tools.*;
import com.connexal.ravelcraft.shared.all.Ravel;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ToolSetDescriptor implements ToolMaterial, ItemSetDescriptor<ToolSetDescriptor.Type> {
    private final ImmutableMap<Type, ItemDescriptor> items;

    private final int durability;
    private final float miningSpeedMultiplier;
    private final float attackDamage;
    private final int enchantability;
    private final Ingredient repairIngredient;
    private final TagKey<Block> inverseTag;

    public ToolSetDescriptor(Identifier identifier, int durability, float miningSpeedMultiplier, float attackDamage, int enchantability, Ingredient repairIngredient, TagKey<Block> inverseTag, boolean axe, boolean hoe, boolean pickaxe, boolean shovel, boolean sword) {
        this.durability = durability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
        this.inverseTag = inverseTag;

        Map<Type, ItemDescriptor> items = new HashMap<>();
        if (axe) {
            PolymerAxeItem item = new PolymerAxeItem(this, Items.WOODEN_AXE, new Item.Settings());
            Identifier axeId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_axe");
            items.put(Type.AXE, ItemDescriptor.builder(axeId).item(item).build());
        }
        if (hoe) {
            PolymerHoeItem item = new PolymerHoeItem(this, Items.WOODEN_HOE, new Item.Settings());
            Identifier hoeId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_hoe");
            items.put(Type.HOE, ItemDescriptor.builder(hoeId).item(item).build());
        }
        if (pickaxe) {
            PolymerPickaxeItem item = new PolymerPickaxeItem(this, Items.WOODEN_PICKAXE, new Item.Settings());
            Identifier pickaxeId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_pickaxe");
            items.put(Type.PICKAXE, ItemDescriptor.builder(pickaxeId).item(item).build());
        }
        if (shovel) {
            PolymerShovelItem item = new PolymerShovelItem(this, Items.WOODEN_SHOVEL, new Item.Settings());
            Identifier shovelId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_shovel");
            items.put(Type.SHOVEL, ItemDescriptor.builder(shovelId).item(item).build());
        }
        if (sword) {
            PolymerSwordItem item = new PolymerSwordItem(this, Items.WOODEN_SWORD, new Item.Settings());
            Identifier swordId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_sword");
            items.put(Type.SWORD, ItemDescriptor.builder(swordId).item(item).build());
        }

        this.items = ImmutableMap.copyOf(items);
    }

    @Override
    public int getDurability() {
        return this.durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeedMultiplier;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return this.inverseTag;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }

    @Override
    public ItemDescriptor get(Type type) {
        return this.items.get(type);
    }

    @Override
    public ImmutableMap<Type, ItemDescriptor> items() {
        return ImmutableMap.copyOf(this.items);
    }

    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static Builder builder(String identifier) {
        return new Builder(Identifier.of(Ravel.ID, identifier));
    }

    public static class Builder {
        private final Identifier identifier;
        private Integer durability = null;
        private Float miningSpeedMultiplier = null;
        private Float attackDamage = null;
        private TagKey<Block> inverseTag = null;
        private int enchantability = 0;
        private Item repairIngredient = null;

        private Builder(Identifier identifier) {
            this.identifier = identifier;
        }

        public Builder durability(int durability) {
            this.durability = durability;
            return this;
        }

        public Builder miningSpeedMultiplier(float miningSpeedMultiplier) {
            this.miningSpeedMultiplier = miningSpeedMultiplier;
            return this;
        }

        public Builder attackDamage(float attackDamage) {
            this.attackDamage = attackDamage;
            return this;
        }

        /**
         * This is the tag that contains blocks that the tool is not effective against.
         * It would be in <code>data/ID/tags/blocks/incorrect_for_THING_tool.json</code>:
         * <pre>
         * {
         *   "values": [
         *     "#minecraft:needs_diamond_tool",
         *     "#minecraft:needs_iron_tool",
         *     "#minecraft:needs_stone_tool"
         *   ]
         * }
         * </pre>
         */
        public Builder inverseTag(TagKey<Block> inverseTag) {
            this.inverseTag = inverseTag;
            return this;
        }

        /**
         * This is a convenience method to set the inverse tag based on the identifier of the tag.
         */
        public Builder inverseTag(Identifier identifier) {
            return this.inverseTag(TagKey.of(RegistryKeys.BLOCK, identifier));
        }

        /**
         * This is a convenience method to set the inverse tag based on the mining level.
         */
        public Builder miningLevel(MiningLevel miningLevel) {
            return this.inverseTag(switch (miningLevel.getLevel()) {
                case 0 -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
                case 1 -> BlockTags.INCORRECT_FOR_STONE_TOOL;
                case 2 -> BlockTags.INCORRECT_FOR_IRON_TOOL;
                case 3 -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
                case 4 -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
                default -> throw new IllegalStateException("Invalid mining level");
            });
        }

        public Builder enchantability(int enchantability) {
            this.enchantability = enchantability;
            return this;
        }

        public Builder repairIngredient(Item repairIngredient) {
            this.repairIngredient = repairIngredient;
            return this;
        }

        public ToolSetDescriptor build(boolean axe, boolean hoe, boolean pickaxe, boolean shovel, boolean sword) {
            if (this.durability == null || this.miningSpeedMultiplier == null || this.attackDamage == null || this.inverseTag == null || this.repairIngredient == null) {
                throw new IllegalStateException("Missing required parameters");
            }

            return new ToolSetDescriptor(this.identifier, this.durability, this.miningSpeedMultiplier, this.attackDamage, this.enchantability, Ingredient.ofItems(this.repairIngredient), this.inverseTag, axe, hoe, pickaxe, shovel, sword);
        }

        public ToolSetDescriptor build() {
            return this.build(true, true, true, true, true);
        }
    }

    public enum Type {
        AXE,
        HOE,
        PICKAXE,
        SHOVEL,
        SWORD
    }
}
