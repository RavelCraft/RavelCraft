package com.connexal.ravelcraft.mod.server.managers.npc;

import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.mod.server.util.gui.NpcGui;
import com.connexal.ravelcraft.shared.all.Ravel;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NpcManager {
    public static final Identifier NPC_ID = Identifier.of(Ravel.ID, "npc");
    public static EntityType<NpcEntity> NPC_TYPE;

    private static final Map<UUID, Long> lastInteract = new HashMap<>();

    public static void setup() {
        //Register the entity type
        NPC_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                NPC_ID,
                EntityType.Builder.<NpcEntity>create(NpcEntity::new, SpawnGroup.MISC)
                        .dimensions(0.6F, 1.8F)
                        .build()
        );

        //Register it with the server
        PolymerEntityUtils.registerType(NPC_TYPE);
        FabricDefaultAttributeRegistry.register(NPC_TYPE, NpcEntity.createNpcAttributes());
    }

    public static NpcEntity createNpc(ServerWorld world, Location location) {
        NpcEntity npc = new NpcEntity(world);

        npc.teleport(location.getX(), location.getY(), location.getZ(), false);
        npc.setPitch(location.getPitch());
        npc.setHeadYaw(location.getYaw());

        world.spawnEntity(npc);

        return npc;
    }

    public static void openEditMenu(NpcEntity npc, ServerPlayerEntity player) {
        NpcGui gui = new NpcGui(player, npc);
        gui.open();
    }

    public static void interact(NpcEntity npc, ServerPlayerEntity player) {
        //Believe it or not, this event is triggered twice. Thanks Mojang.
        long lastInteract = player.getLastActionTime();
        Long lastRecordedInteract = NpcManager.lastInteract.remove(player.getUuid());
        if (lastRecordedInteract != null && lastInteract - lastRecordedInteract < 50) {
            return;
        }
        NpcManager.lastInteract.put(player.getUuid(), lastInteract);

        npc.runInteraction(player);
    }
}
