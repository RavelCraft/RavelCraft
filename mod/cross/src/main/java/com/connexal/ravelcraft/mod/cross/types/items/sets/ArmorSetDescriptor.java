package com.connexal.ravelcraft.mod.cross.types.items.sets;

import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.polymer.PolymerArmorItem;
import com.connexal.ravelcraft.shared.all.Ravel;
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

public class ArmorSetDescriptor implements ItemSetDescriptor<ArmorItem.Type> {
    private final ImmutableMap<ArmorItem.Type, ItemDescriptor> items;

    public ArmorSetDescriptor(Identifier identifier, int color, Map<ArmorItem.Type, Integer> durability, int durabilityMultiplier, Map<ArmorItem.Type, Integer> protection, int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient, List<ArmorMaterial.Layer> layers, float toughness, float knockbackResistance, boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
        ArmorMaterial armorMaterial = new ArmorMaterial(protection, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance);
        RegistryEntry<ArmorMaterial> armorMaterialRef = Registry.registerReference(Registries.ARMOR_MATERIAL, identifier, armorMaterial);

        Map<ArmorItem.Type, ItemDescriptor> items = new HashMap<>();
        if (helmet) {
            PolymerArmorItem item = new PolymerArmorItem(armorMaterialRef, ArmorItem.Type.HELMET, color, new Item.Settings().maxDamage(durability.get(ArmorItem.Type.HELMET) * durabilityMultiplier));
            Identifier helmetId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_helmet");
            items.put(ArmorItem.Type.HELMET, ItemDescriptor.builder(helmetId).item(item).build());
        }
        if (chestplate) {
            PolymerArmorItem item = new PolymerArmorItem(armorMaterialRef, ArmorItem.Type.CHESTPLATE, color, new Item.Settings().maxDamage(durability.get(ArmorItem.Type.CHESTPLATE) * durabilityMultiplier));
            Identifier chestplateId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_chestplate");
            items.put(ArmorItem.Type.CHESTPLATE, ItemDescriptor.builder(chestplateId).item(item).build());
        }
        if (leggings) {
            PolymerArmorItem item = new PolymerArmorItem(armorMaterialRef, ArmorItem.Type.LEGGINGS, color, new Item.Settings().maxDamage(durability.get(ArmorItem.Type.LEGGINGS) * durabilityMultiplier));
            Identifier leggingsId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_leggings");
            items.put(ArmorItem.Type.LEGGINGS, ItemDescriptor.builder(leggingsId).item(item).build());
        }
        if (boots) {
            PolymerArmorItem item = new PolymerArmorItem(armorMaterialRef, ArmorItem.Type.BOOTS, color, new Item.Settings().maxDamage(durability.get(ArmorItem.Type.BOOTS) * durabilityMultiplier));
            Identifier bootsId = Identifier.of(identifier.getNamespace(), identifier.getPath() + "_boots");
            items.put(ArmorItem.Type.BOOTS, ItemDescriptor.builder(bootsId).item(item).build());
        }

        this.items = ImmutableMap.copyOf(items);
    }

    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static Builder builder(String identifier) {
        return new Builder(Identifier.of(Ravel.ID, identifier));
    }

    @Override
    public ItemDescriptor get(ArmorItem.Type type) {
        return this.items.get(type);
    }

    @Override
    public ImmutableMap<ArmorItem.Type, ItemDescriptor> items() {
        return this.items;
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
        private int color = -1;

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

        public Builder color(int r, int g, int b) {
            //Clamp values
            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            this.color = (r << 16) + (g << 8) + b;
            return this;
        }

        public Builder color(int hex) {
            this.color = hex;
            return this;
        }

        public ArmorSetDescriptor build(boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
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

            return new ArmorSetDescriptor(this.identifier, this.color, defense, this.durabilityMultiplier, defense, this.enchantability, this.equipSound, this.repairIngredient, layers, this.toughness, this.knockbackResistance, helmet, chestplate, leggings, boots);
        }

        public ArmorSetDescriptor build() {
            return this.build(true, true, true, true);
        }
    }
}
