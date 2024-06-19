package com.connexal.ravelcraft.mod.cross.datagen.translations;

import com.connexal.ravelcraft.mod.cross.registry.RavelBlockRegistry;
import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import com.connexal.ravelcraft.shared.all.Ravel;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EnglishTranslationProvider extends FabricLanguageProvider {
    public EnglishTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        for (ItemDescriptor descriptor : RavelItemRegistry.ITEM_LIST) {
            translationBuilder.add("item." + descriptor.identifier().toString().replace(':', '.'), descriptor.displayName());
        }
        for (BlockDescriptor descriptor : RavelBlockRegistry.BLOCK_LIST) {
            translationBuilder.add("block." + descriptor.identifier().toString().replace(':', '.'), descriptor.displayName());
        }

        try {
            Optional<Path> path = dataOutput.getModContainer().findPath("assets/" + Ravel.ID + "/lang/en_us.unmerged.json");
            translationBuilder.add(path.get());
        } catch (Exception e) {
            LOGGER.info("Failed to merge language file: " + e);
        }
    }
}
