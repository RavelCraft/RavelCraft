package com.connexal.ravelcraft.mod.server.managers.npc;

import com.connexal.ravelcraft.mod.server.mixin.accessors.EntityTrackerAccessor;
import com.connexal.ravelcraft.mod.server.mixin.accessors.PlayerEntityAccessor;
import com.connexal.ravelcraft.mod.server.mixin.accessors.ServerChunkLoadingManagerAccessor;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class NpcEntity extends MobEntity implements PolymerEntity {
    private GameProfile gameProfile = null;
    private byte skinLayers = 127;

    private int interactionType = -1;
    private String interactionData = null;

    public NpcEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);

        this.setCanPickUpLoot(false);
        this.setCustomNameVisible(true);
        this.setCustomName(Text.of(this.getName())); //No idea why this is needed
        this.setInvulnerable(true);
        this.setPersistent();
        this.experiencePoints = 0;

        this.gameProfile = new GameProfile(this.getUuid(), "ravel.npc");
    }

    public NpcEntity(World world) {
        this(NpcManager.NPC_TYPE, world);
    }

    public static DefaultAttributeContainer.Builder createNpcAttributes() {
        return NpcEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35);
    }

    private void sendProfileUpdates() {
        ServerChunkManager manager = (ServerChunkManager) this.getWorld().getChunkManager();
        ServerChunkLoadingManager storage = manager.chunkLoadingManager;
        EntityTrackerAccessor trackerEntry = ((ServerChunkLoadingManagerAccessor) storage).getEntityTrackers().get(this.getId());
        if (trackerEntry != null) {
            trackerEntry.getListeners().forEach(tracking -> {
                trackerEntry.getEntry().stopTracking(tracking.getPlayer());
                trackerEntry.getEntry().startTracking(tracking.getPlayer());
            });
        }
    }

    private NbtCompound writeSkinToTag(GameProfile profile) {
        NbtCompound skinTag = new NbtCompound();
        try {
            PropertyMap propertyMap = profile.getProperties();
            Property skin = propertyMap.get("textures").iterator().next();

            skinTag.putString("value", skin.value());
            if (skin.hasSignature()) {
                skinTag.putString("signature", skin.signature());
            }
        } catch (NoSuchElementException ignored) { }

        return skinTag;
    }

    private void setSkinFromBase64(String value, String signature) {
        try {
            if (!value.isEmpty()) {
                PropertyMap propertyMap = this.gameProfile.getProperties();
                if (signature == null) {
                    propertyMap.put("textures", new Property("textures", value));
                } else {
                    propertyMap.put("textures", new Property("textures", value, signature));
                }
            }
        } catch (Error ignored) { }
    }

    private void setSkinFromTag(NbtCompound tag) {
        // Clearing current skin
        try {
            PropertyMap map = this.gameProfile.getProperties();
            Property skin = map.get("textures").iterator().next();
            map.remove("textures", skin);
        } catch (NoSuchElementException ignored) { }

        // Setting the skin
        try {
            String value = tag.getString("value");
            String signature = null;
            if (tag.contains("signature")) {
                signature = tag.getString("signature");
            }

            this.setSkinFromBase64(value, signature);
        } catch (Error ignored) { }
    }

    public void applySkin(String value, String signature) {
        if (this.gameProfile == null) {
            return;
        }

        this.setSkinFromBase64(value, signature);
        this.sendProfileUpdates();
    }

    public void applySkin(GameProfile texturesProfile) {
        if (this.gameProfile == null) {
            return;
        }

        this.setSkinFromTag(this.writeSkinToTag(texturesProfile));
        this.sendProfileUpdates();
    }

    public void setSkinLayers(Byte skinLayers) {
        this.skinLayers = skinLayers;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    @Override
    public int getPermissionLevel() {
        return 4; //Operator
    }

    //Entity data
    @Override
    public void setCustomName(Text name) {
        super.setCustomName(name);
        String profileName = "RavelCraft";

        if (name != null) {
            profileName = name.getString();
            if (name.getString().length() > 16) {
                // Minecraft kicks you if player has name longer than 16 chars in GameProfile
                profileName = name.getString().substring(0, 16);
            }
        }

        NbtCompound skin = null;
        if (this.gameProfile != null) {
            skin = this.writeSkinToTag(this.gameProfile);
        }

        this.gameProfile = new GameProfile(this.getUuid(), profileName);
        if (skin != null) {
            this.setSkinFromTag(skin);
            this.sendProfileUpdates();
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        NbtCompound npcTag = nbt.getCompound("RavelNPCTag");

        this.setSkinLayers(npcTag.getByte("SkinLayers"));

        String profileName = this.getName().getString();
        if (profileName.length() > 16) {
            profileName = profileName.substring(0, 16);
        }
        this.gameProfile = new GameProfile(this.getUuid(), profileName);

        this.setSkinFromTag(npcTag.getCompound("Skin"));

        if (npcTag.contains("Pose")) {
            this.setPose(EntityPose.valueOf(npcTag.getString("Pose")));
        } else {
            this.setPose(EntityPose.STANDING);
        }

        if (npcTag.contains("InteractionType")) {
            this.interactionType = npcTag.getInt("InteractionType");
            this.interactionData = npcTag.getString("InteractionData");
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound innbt) {
        NbtCompound nbt = super.writeNbt(innbt);
        NbtCompound npcTag = new NbtCompound();

        super.setCustomNameVisible(innbt.contains("CustomNameVisible"));

        npcTag.putByte("SkinLayers", this.skinLayers);

        npcTag.put("Skin", this.writeSkinToTag(this.gameProfile));

        npcTag.putString("Pose", this.getPose().name());

        if (this.interactionType != -1 && this.interactionData != null) {
            npcTag.putInt("InteractionType", this.interactionType);
            npcTag.putString("InteractionData", this.interactionData);
        }

        nbt.put("RavelNPCTag", npcTag);
        return nbt;
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if (attacker instanceof ServerPlayerEntity player) {
            if (player.hasPermissionLevel(4)) {
                NpcManager.openEditMenu(this, player);
            } else {
                NpcManager.interact(this, player);
            }
        }

        return true;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        NpcManager.interact(this, (ServerPlayerEntity) player);
        return ActionResult.PASS;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        //Disable pushing
    }

    @Override
    public void pushAway(Entity entity) {
        //Disable pushing
    }

    public void setInteraction(int type, String data) {
        this.interactionType = type;
        this.interactionData = data;
    }

    public void runInteraction(ServerPlayerEntity player) {
        if (this.interactionType == -1 || this.interactionData == null) {
            return;
        }

        switch (this.interactionType) {
            case 0: { //Message
                player.sendMessage(Text.of("[NPC]: " + this.interactionData));
                break;
            }
            case 1: {
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), this.interactionData);
                break;
            }
        }
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.PLAYER;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        // Skin layer settings
        data.removeIf(value -> value.id() == PlayerEntityAccessor.getPLAYER_MODEL_PARTS().id());

        DataTracker.SerializedEntry<Byte> skinLayerTag = DataTracker.SerializedEntry.of(PlayerEntityAccessor.getPLAYER_MODEL_PARTS(), this.skinLayers);
        data.add(skinLayerTag);
    }

    @Override
    public void onBeforeSpawnPacket(ServerPlayerEntity player, Consumer<Packet<?>> packetConsumer) {
        var packet = PolymerEntityUtils.createMutablePlayerListPacket(EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.UPDATE_LISTED));
        packet.getEntries().add(new PlayerListS2CPacket.Entry(this.uuid, this.gameProfile, false, 0, GameMode.SURVIVAL, null, null));
        packetConsumer.accept(packet);
    }
}
