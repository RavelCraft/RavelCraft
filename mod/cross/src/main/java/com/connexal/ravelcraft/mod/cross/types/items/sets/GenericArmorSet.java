package com.connexal.ravelcraft.mod.cross.types.items.sets;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

import java.util.HashMap;
import java.util.Map;

public class GenericArmorSet extends GenericSet implements ArmorMaterial {
    private final int[] durability;
    private final int durabilityMultiplier;
    private final int[] protection;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final Ingredient repairIngredient;
    private final float toughness;
    private final float knockbackResistance;

    public GenericArmorSet(String identifier, Builder builder, boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
        super(identifier);

        if (builder.durability == null || builder.protection == null || builder.equipSound == null || builder.repairIngredient == null || builder.toughness == null) {
            throw new IllegalStateException("Builder missing required parameters");
        }

        this.durability = new int[] {builder.durability[0], builder.durability[1], builder.durability[2], builder.durability[3]};
        this.durabilityMultiplier = builder.durabilityMultiplier;
        this.protection = new int[] {builder.protection[0], builder.protection[1], builder.protection[2], builder.protection[3]};
        this.enchantability = builder.enchantability;
        this.equipSound = builder.equipSound;
        this.repairIngredient = Ingredient.ofItems(builder.repairIngredient);
        this.toughness = builder.toughness;
        this.knockbackResistance = builder.knockbackResistance;

        Map<String, Item> items = new HashMap<>();

        if (helmet) {
            items.put("helmet", new ArmorItem(this, ArmorItem.Type.HELMET, new FabricItemSettings()));
        }
        if (chestplate) {
            items.put("chestplate", new ArmorItem(this, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
        }
        if (leggings) {
            items.put("leggings", new ArmorItem(this, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
        }
        if (boots) {
            items.put("boots", new ArmorItem(this, ArmorItem.Type.BOOTS, new FabricItemSettings()));
        }

        this.setItems(items);
    }

    public GenericArmorSet(String identifier, Builder builder) {
        this(identifier, builder, true, true, true, true);
    }

    @Override
    public int getDurability(ArmorItem.Type type) {
        return this.durability[type.getEquipmentSlot().getEntitySlotId()] * this.durabilityMultiplier;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return this.protection[type.getEquipmentSlot().getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }

    @Override
    public String getName() {
        return this.getIdentifier();
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer[] durability =  null;
        private Integer durabilityMultiplier = null;
        private Integer[] protection = null;
        private int enchantability = 0;
        private SoundEvent equipSound = null;
        private Item repairIngredient = null;
        private Float toughness = null;
        private float knockbackResistance = 0;

        private Builder() {
        }

        public Builder durability(int boots, int leggings, int chestplate, int helmet) {
            this.durability = new Integer[] {boots, leggings, chestplate, helmet};
            return this;
        }

        public Builder durabilityMultiplier(int durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
            return this;
        }

        public Builder protection(int boots, int leggings, int chestplate, int helmet) {
            this.protection = new Integer[] {boots, leggings, chestplate, helmet};
            return this;
        }

        public Builder enchantability(int enchantability) {
            this.enchantability = enchantability;
            return this;
        }

        public Builder equipSound(SoundEvent equipSound) {
            this.equipSound = equipSound;
            return this;
        }

        public Builder repairIngredient(Item repairIngredient) {
            this.repairIngredient = repairIngredient;
            return this;
        }

        public Builder toughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        public Builder knockbackResistance(float knockbackResistance) {
            this.knockbackResistance = knockbackResistance;
            return this;
        }
    }
}
