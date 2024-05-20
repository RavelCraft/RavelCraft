package com.connexal.ravelcraft.mod.cross.types.items.sets;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorSet {
    private final ImmutableMap<ArmorItem.Type, ItemDescriptor> items;

    public ArmorSet(Identifier identifier, Map<ArmorItem.Type, Integer> durability, int durabilityMultiplier, Map<ArmorItem.Type, Integer> protection, int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient, List<ArmorMaterial.Layer> layers, float toughness, float knockbackResistance, boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
        //TODO: set durability

        ArmorMaterial armorMaterial = new ArmorMaterial(protection, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance);
        RegistryEntry<ArmorMaterial> armorMaterialRef = Registry.registerReference(Registries.ARMOR_MATERIAL, identifier, armorMaterial);

        Map<ArmorItem.Type, ItemDescriptor> items = new HashMap<>();
        if (helmet) {
            ArmorItem item = new ArmorItem(armorMaterialRef, ArmorItem.Type.HELMET, new Item.Settings());
            Identifier helmetId = new Identifier(identifier.getNamespace(), identifier.getPath() + "_helmet");
            items.put(ArmorItem.Type.HELMET, ItemDescriptor.builder(helmetId).item(item).register());
        }
        if (chestplate) {
            ArmorItem item = new ArmorItem(armorMaterialRef, ArmorItem.Type.CHESTPLATE, new Item.Settings());
            Identifier chestplateId = new Identifier(identifier.getNamespace(), identifier.getPath() + "_chestplate");
            items.put(ArmorItem.Type.CHESTPLATE, ItemDescriptor.builder(chestplateId).item(item).register());
        }
        if (leggings) {
            ArmorItem item = new ArmorItem(armorMaterialRef, ArmorItem.Type.LEGGINGS, new Item.Settings());
            Identifier leggingsId = new Identifier(identifier.getNamespace(), identifier.getPath() + "_leggings");
            items.put(ArmorItem.Type.LEGGINGS, ItemDescriptor.builder(leggingsId).item(item).register());
        }
        if (boots) {
            ArmorItem item = new ArmorItem(armorMaterialRef, ArmorItem.Type.BOOTS, new Item.Settings());
            Identifier bootsId = new Identifier(identifier.getNamespace(), identifier.getPath() + "_boots");
            items.put(ArmorItem.Type.BOOTS, ItemDescriptor.builder(bootsId).item(item).register());
        }

        this.items = ImmutableMap.copyOf(items);
    }

    public ItemDescriptor get(ArmorItem.Type type) {
        return this.items.get(type);
    }

    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static Builder builder(String identifier) {
        return new Builder(new Identifier(BuildConstants.ID, identifier));
    }

    public static class Builder {
        private final Identifier identifier;
        private Integer[] durability =  null;
        private Integer durabilityMultiplier = null;
        private Integer[] protection = null;
        private int enchantability = 0;
        private RegistryEntry<SoundEvent> equipSound = null;
        private Supplier<Ingredient> repairIngredient = null;
        private final List<String> layerSuffixes = new ArrayList<>();
        private final List<Boolean> layerDyeable = new ArrayList<>();
        private float toughness = 0;
        private float knockbackResistance = 0;

        private Builder(Identifier identifier) {
            this.identifier = identifier;
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

        public Builder equipSound(RegistryEntry<SoundEvent> equipSound) {
            this.equipSound = equipSound;
            return this;
        }

        public Builder repairIngredient(Supplier<Ingredient> repairIngredient) {
            this.repairIngredient = repairIngredient;
            return this;
        }

        public Builder repairIngredient(Item repairIngredient) {
            this.repairIngredient = () -> Ingredient.ofItems(repairIngredient);
            return this;
        }

        public Builder addLayer(String suffix, boolean dyeable) {
            this.layerSuffixes.add(suffix);
            this.layerDyeable.add(dyeable);
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

        public ArmorSet register(boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
            if (this.durability == null || this.durabilityMultiplier == null || this.protection == null || this.equipSound == null || this.repairIngredient == null || this.layerSuffixes.isEmpty() || this.layerDyeable.size() != this.layerSuffixes.size()) {
                throw new IllegalStateException("Missing required parameters");
            }

            Map<ArmorItem.Type, Integer> defense = new HashMap<>();
            defense.put(ArmorItem.Type.HELMET, this.protection[3]);
            defense.put(ArmorItem.Type.CHESTPLATE, this.protection[2]);
            defense.put(ArmorItem.Type.LEGGINGS, this.protection[1]);
            defense.put(ArmorItem.Type.BOOTS, this.protection[0]);

            List<ArmorMaterial.Layer> layers = new ArrayList<>();
            for (int i = 0; i < this.layerSuffixes.size(); i++) {
                layers.add(new ArmorMaterial.Layer(this.identifier, this.layerSuffixes.get(i), this.layerDyeable.get(i)));
            }

            return new ArmorSet(this.identifier, defense, this.durabilityMultiplier, defense, this.enchantability, this.equipSound, this.repairIngredient, layers, this.toughness, this.knockbackResistance, helmet, chestplate, leggings, boots);
        }

        public ArmorSet register() {
            return this.register(true, true, true, true);
        }
    }
}
