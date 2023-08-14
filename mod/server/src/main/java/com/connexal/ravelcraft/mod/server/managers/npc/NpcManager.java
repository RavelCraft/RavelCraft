package com.connexal.ravelcraft.mod.server.managers.npc;

import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.mod.server.util.registry.RegistrySyncUtils;
import com.connexal.ravelcraft.shared.BuildConstants;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Base64;
import java.util.function.Supplier;

public class NpcManager {
    public static final Identifier NPC_ID = new Identifier(BuildConstants.ID, "npc");
    public static Supplier<EntityType<NpcEntity>> NPC_TYPE;

    public NpcManager() {
        //Register the entity type
        final EntityType<NpcEntity> type = Registry.register(
                Registries.ENTITY_TYPE,
                NPC_ID,
                FabricEntityTypeBuilder.<NpcEntity>create(SpawnGroup.MISC, NpcEntity::new)
                        .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
                        .build()
        );
        NPC_TYPE = () -> type;

        //Register it with the server
        RegistrySyncUtils.setServerEntry(Registries.ENTITY_TYPE, NPC_TYPE.get());
        FabricDefaultAttributeRegistry.register(NPC_TYPE.get(), NpcEntity.createNpcAttributes());
    }

    public static NpcEntity createNpc(ServerWorld world, String name, Location location, byte[] skinData) {
        NpcEntity npc = new NpcEntity(world);

        npc.teleport(location.getX(), location.getY(), location.getZ());
        npc.setPitch(location.getPitch());
        npc.setHeadYaw(location.getYaw());
        npc.setCustomName(Text.of(name));
        npc.applySkin(new String(skinData), null);

        world.tryLoadEntity(npc);

        return npc;
    }

    public static NpcEntity createNpc(ServerWorld world, String name, Location locaton, String skinTexture) {
        String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + skinTexture + "\"}}}";
        byte[] encodedData = Base64.getEncoder().encode(json.getBytes());

        return createNpc(world, name, locaton, encodedData);
    }
}
