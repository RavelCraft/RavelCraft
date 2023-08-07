package com.connexal.ravelcraft.mod.cross.types.items.tools;

import com.connexal.ravelcraft.mod.cross.types.items.GenericSet;
import com.connexal.ravelcraft.mod.cross.types.items.MiningLevel;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.List;

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

        List<Item> items = new ArrayList<>();

        //TODO: Set attack damage and speed
        if (axe) {
            items.add(new GenericAxe(this, 0, 0));
        }
        if (hoe) {
            items.add(new GenericHoe(this, 0, 0));
        }
        if (pickaxe) {
            items.add(new GenericPickaxe(this, 0, 0));
        }
        if (shovel) {
            items.add(new GenericShovel(this, 0, 0));
        }
        if (sword) {
            items.add(new GenericSword(this, 0, 0));
        }

        this.setItems(items.toArray(new Item[0]));
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
