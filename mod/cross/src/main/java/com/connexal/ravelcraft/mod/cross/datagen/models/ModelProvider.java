package com.connexal.ravelcraft.mod.cross.datagen.models;

import com.connexal.ravelcraft.mod.cross.registry.RavelBlockRegistry;
import com.connexal.ravelcraft.mod.cross.registry.RavelItemRegistry;
import com.connexal.ravelcraft.mod.cross.types.blocks.BlockDescriptor;
import com.connexal.ravelcraft.mod.cross.types.items.ItemDescriptor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.BlockItem;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        for (BlockDescriptor descriptor : RavelBlockRegistry.BLOCK_LIST) {
            blockStateModelGenerator.registerSimpleCubeAll(descriptor.block());
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (ItemDescriptor descriptor : RavelItemRegistry.ITEM_LIST) {
            if (descriptor.item() instanceof BlockItem) {
                continue;
            }

            itemModelGenerator.register(descriptor.item(), descriptor.displayModel());
        }
    }
}
