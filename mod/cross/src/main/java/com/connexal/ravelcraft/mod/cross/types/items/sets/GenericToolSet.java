package com.connexal.ravelcraft.mod.cross.types.items.sets;

import com.connexal.ravelcraft.mod.cross.types.items.MiningLevel;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class GenericToolSet extends GenericSet implements ToolMaterial {
    private final int durability;
    private final float miningSpeedMultiplier;
    private final float attackDamage;
    private final int miningLevel;
    private final int enchantability;
    private final Ingredient repairIngredient;

    public GenericToolSet(String identifier, Builder builder, boolean axe, boolean hoe, boolean pickaxe, boolean shovel, boolean sword) {
        super(identifier);

        if (builder.durability == null || builder.miningSpeedMultiplier == null || builder.attackDamage == null || builder.miningLevel == null || builder.repairIngredient == null) {
            throw new IllegalStateException("Builder missing required parameters");
        }

        this.durability = builder.durability;
        this.miningSpeedMultiplier = builder.miningSpeedMultiplier;
        this.attackDamage = builder.attackDamage;
        this.miningLevel = builder.miningLevel.getLevel();
        this.enchantability = builder.enchantability;
        this.repairIngredient = Ingredient.ofItems(builder.repairIngredient);

        Map<String, Item> items = new HashMap<>();

        //TODO: Set attack damage and speed
        if (axe) {
            items.put("axe", new AxeItem(this, 0, 0, new FabricItemSettings()));
        }
        if (hoe) {
            items.put("hoe", new HoeItem(this, 0, 0, new FabricItemSettings()));
        }
        if (pickaxe) {
            items.put("pickaxe", new PickaxeItem(this, 0, 0, new FabricItemSettings()));
        }
        if (shovel) {
            items.put("shovel", new ShovelItem(this, 0, 0, new FabricItemSettings()));
        }
        if (sword) {
            items.put("sword", new SwordItem(this, 0, 0, new FabricItemSettings()));
        }

        this.setItems(items);
    }

    public GenericToolSet(String identifier, Builder builder) {
        this(identifier, builder, true, true, true, true, true);
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
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer durability = null;
        private Float miningSpeedMultiplier = null;
        private Float attackDamage = null;
        private MiningLevel miningLevel = null;
        private int enchantability = 0;
        private Item repairIngredient = null;

        private Builder() {
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

        public Builder miningLevel(MiningLevel miningLevel) {
            this.miningLevel = miningLevel;
            return this;
        }

        public Builder enchantability(int enchantability) {
            this.enchantability = enchantability;
            return this;
        }

        public Builder repairIngredient(Item repairIngredient) {
            this.repairIngredient = repairIngredient;
            return this;
        }
    }
}
