package com.connexal.ravelcraft.mod.server.managers.npc;

import com.mojang.authlib.GameProfile;
import net.minecraft.text.Text;

public record NpcPlayerUpdate(GameProfile profile, Text displayName, long removeAt) {
}
