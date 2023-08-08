package com.connexal.ravelcraft.mod.cross;

import net.minecraft.util.Identifier;

//Remember to mirror this file in shared!!!!
public class BuildConstants {
    public static final String NAME = "RavelCraft Network";
    public static final String ID = "ravelcraft";
    public static final String VERSION = "2.0";
    public static final String DESCRIPTION = "The Maurice Ravel Minecraft server network";

    public static final String SERVER_IP = "ravelcraft.fr";
    public static final String[] TEST_IPS = new String[] { "localhost", "minecraft.alnet", "ravelcraft.connexal.com" };
    public static final int MAX_PLAYERS = 100;

    //Specific to the mod
    public static final Identifier RAVEL_PACKET = new Identifier(ID, "ravel_packet");
}
