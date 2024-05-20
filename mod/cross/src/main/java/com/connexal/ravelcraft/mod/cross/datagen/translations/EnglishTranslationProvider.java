package com.connexal.ravelcraft.mod.cross.datagen.translations;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.connexal.ravelcraft.mod.cross.registry.RavelBlockRegistry;
import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
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

    private void processIdentifier(String type, Identifier identifier, TranslationBuilder translationBuilder) {
        StringBuilder builder = new StringBuilder();
        boolean capitalize = true;
        for (char c : identifier.getPath().toCharArray()) {
            if (capitalize) {
                builder.append(Character.toUpperCase(c));
                capitalize = false;
            } else {
                if (c == '_') {
                    builder.append(' ');
                    capitalize = true;
                } else {
                    builder.append(c);
                }
            }
        }

        translationBuilder.add(type + "." + identifier.toString().replace(':', '.'), builder.toString());
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        for (ItemDescriptor descriptor : RavelItemRegistry.ITEM_LIST) {
            this.processIdentifier("item", descriptor.identifier(), translationBuilder);
        }
        for (BlockDescriptor blockDescriptor : RavelBlockRegistry.BLOCK_LIST) {
            this.processIdentifier("block", blockDescriptor.identifier(), translationBuilder);
        }

        try {
            Optional<Path> path = dataOutput.getModContainer().findPath("assets/" + BuildConstants.ID + "/lang/en_us.unmerged.json");
            translationBuilder.add(path.get());
        } catch (Exception e) {
            LOGGER.info("Failed to merge language file: " + e);
        }
    }
}
