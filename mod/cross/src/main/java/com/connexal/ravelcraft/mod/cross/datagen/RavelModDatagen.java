package com.connexal.ravelcraft.mod.cross.datagen;

import com.connexal.ravelcraft.mod.cross.datagen.models.ModelProvider;
import com.connexal.ravelcraft.mod.cross.datagen.translations.EnglishTranslationProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class RavelModDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        final FabricDataGenerator.Pack pack = dataGenerator.createPack();

        //Languages
        pack.addProvider(EnglishTranslationProvider::new);

        //Models
        pack.addProvider(ModelProvider::new);
    }
}
