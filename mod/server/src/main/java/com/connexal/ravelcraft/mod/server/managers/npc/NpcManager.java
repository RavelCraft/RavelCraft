package com.connexal.ravelcraft.mod.server.managers.npc;

import com.connexal.ravelcraft.mod.server.util.Location;
import com.connexal.ravelcraft.mod.server.util.gui.NpcGui;
import com.connexal.ravelcraft.mod.server.util.registry.RegistrySyncUtils;
import com.connexal.ravelcraft.shared.BuildConstants;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class NpcManager {
    public static final Identifier NPC_ID = new Identifier(BuildConstants.ID, "npc");
    public static Supplier<EntityType<NpcEntity>> NPC_TYPE;

    private static final Map<UUID, Long> lastInteract = new HashMap<>();

    public static void setup() {
        //Register the entity type
        final EntityType<NpcEntity> type = Registry.register(
                Registries.ENTITY_TYPE,
                NPC_ID,
                EntityType.Builder.<NpcEntity>create(NpcEntity::new, SpawnGroup.MISC)
                        .dimensions(0.6F, 1.8F)
                        .build()
        );
        NPC_TYPE = () -> type;

        //Register it with the server
        RegistrySyncUtils.setServerEntry(Registries.ENTITY_TYPE, NPC_TYPE.get());
        FabricDefaultAttributeRegistry.register(NPC_TYPE.get(), NpcEntity.createNpcAttributes());
    }

    public static NpcEntity createNpc(ServerWorld world, Location location) {
        NpcEntity npc = new NpcEntity(world);

        npc.teleport(location.getX(), location.getY(), location.getZ());
        npc.setPitch(location.getPitch());
        npc.setHeadYaw(location.getYaw());
        npc.sendRotationUpdate();

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
