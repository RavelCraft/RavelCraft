package com.connexal.ravelcraft.mod.client.capes;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CapeProvider {
    /**public static final CapeProvider.Companion Companion = new CapeProvider.Companion();
    private GameProfile profile;
    private final UUID uuid;
    private int lastFrame;
    private int maxFrames;
    private long lastFrameTime;
    private boolean hasCape;
    private boolean hasElytraTexture;
    private boolean hasAnimatedCape;
    private CapeType capeType;
    private static final Map<UUID, CapeProvider> instances = new HashMap<>();
    private static final ExecutorService capeExecutor = Executors.newFixedThreadPool(2);

    public CapeProvider(GameProfile profile) {
        this.profile = profile;
        this.uuid = this.profile.getId();
        this.hasElytraTexture = true;
        instances.put(this.uuid, this);
    }

    public final GameProfile getProfile() {
        return this.profile;
    }

    public final void setProfile(GameProfile profile) {
        this.profile = profile;
    }

    public final UUID getUuid() {
        return this.uuid;
    }

    public final int getLastFrame() {
        return this.lastFrame;
    }

    public final void setLastFrame(int frame) {
        this.lastFrame = frame;
    }

    public final int getMaxFrames() {
        return this.maxFrames;
    }

    public final void setMaxFrames(int frames) {
        this.maxFrames = frames;
    }

    public final long getLastFrameTime() {
        return this.lastFrameTime;
    }

    public final void setLastFrameTime(long frame) {
        this.lastFrameTime = frame;
    }

    public final boolean getHasCape() {
        return this.hasCape;
    }

    public final void setHasCape(boolean hasCape) {
        this.hasCape = hasCape;
    }

    public final boolean getHasElytraTexture() {
        return this.hasElytraTexture;
    }

    public final void setHasElytraTexture(boolean hasElytraTexture) {
        this.hasElytraTexture = hasElytraTexture;
    }

    public final boolean getHasAnimatedCape() {
        return this.hasAnimatedCape;
    }

    public final void setHasAnimatedCape(boolean hasAnimatedCape) {
        this.hasAnimatedCape = hasAnimatedCape;
    }

    public final CapeType getCapeType() {
        return this.capeType;
    }

    public final void setCapeType(CapeType capeType) {
        this.capeType = capeType;
    }

    public final Identifier getCape() {
        if (!this.hasAnimatedCape) {
            return Identifier.of("ravelcape", this.uuid.toString());
        } else {
            long time = System.currentTimeMillis();

            if (time > this.lastFrameTime + 100L) {
                int thisFrame = (this.lastFrame + 1) % this.maxFrames;
                this.lastFrame = thisFrame;
                this.lastFrameTime = time;
                return Identifier.of("ravelcape", this.uuid + "/" + thisFrame);
            } else {
                return Identifier.of("ravelcape", this.uuid + "/" + this.lastFrame);
            }
        }
    }

    public final boolean setCape(CapeType capeType) {
        String capeURL = capeType.getURL(this.profile);
        HttpURLConnection connection = Companion.connection(capeURL);

        connection.connect();
        if (connection.getResponseCode() == 200) {
            this.capeType = capeType;
            return setCapeTexture$default(this, connection.getInputStream(), false, 2, null);
        } else {
            return false;
        }
    }

    public final boolean setCapeTexture(InputStream image, boolean animated) {
        boolean var3;
        try {
            NativeImage cape = NativeImage.read(image);
            MinecraftClient.getInstance().submit(CapeProvider::setCapeTexture$lambda$2);
            var3 = true;
        } catch (IOException var5) {
            var3 = false;
        }

        return var3;
    }

    // $FF: synthetic method
    public static boolean setCapeTexture$default(CapeProvider var0, InputStream var1, boolean var2, int var3, Object var4) {
        if ((var3 & 2) != 0) {
            var2 = false;
        }

        return var0.setCapeTexture(var1, var2);
    }

    private NativeImage parseCape(NativeImage img) {
        int imageWidth = 64;
        int imageHeight = 32;
        int srcWidth = img.method_4307();

        int srcHeight;
        for(srcHeight = img.method_4323(); imageWidth < srcWidth || imageHeight < srcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);

        for(int x = 0; x < srcWidth; ++x) {
            for(int y = 0; y < srcHeight; ++y) {
                imgNew.method_4305(x, y, img.method_4315(x, y));
            }
        }

        img.close();
        return imgNew;
    }

    private Int2ObjectMap<NativeImage> parseAnimatedCape(NativeImage img) {
        Int2ObjectMap<NativeImage> animatedCape = new Int2ObjectOpenHashMap<>();
        int totalFrames = img.method_4323() / (img.method_4307() / 2);

        for(int currentFrame = 0; currentFrame < totalFrames; ++currentFrame) {
            NativeImage frame = new NativeImage(img.method_4307(), img.method_4307() / 2, true);
            int x = 0;

            for(int var7 = frame.method_4307(); x < var7; ++x) {
                int y = 0;

                for(int var9 = frame.method_4323(); y < var9; ++y) {
                    frame.method_4305(x, y, img.method_4315(x, y + currentFrame * (img.method_4307() / 2)));
                }
            }

            Integer var10 = currentFrame;
            ((Map)animatedCape).put(var10, frame);
        }

        return animatedCape;
    }

    private static void setCapeTexture$lambda$2(boolean $animated, CapeProvider this$0, NativeImage $cape) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if ($animated) {
            Intrinsics.checkNotNull($cape);
            Int2ObjectOpenHashMap animatedCapeFrames = this$0.parseAnimatedCape($cape);
            Map $this$forEach$iv = (Map)animatedCapeFrames;
            int $i$f$forEach = false;
            Iterator var6 = $this$forEach$iv.entrySet().iterator();

            while(var6.hasNext()) {
                Entry element$iv = (Entry)var6.next();
                int var9 = false;
                Integer frame = (Integer)element$iv.getKey();
                NativeImage texture = (NativeImage)element$iv.getValue();
                class_310.method_1551().method_1531().method_4616(Capes.INSTANCE.identifier(this$0.uuid + "/" + frame), (class_1044)(new class_1043(texture)));
            }

            this$0.maxFrames = animatedCapeFrames.size();
            this$0.hasCape = true;
            this$0.hasAnimatedCape = true;
        } else {
            int var12 = $cape.method_4307();
            int var13 = $cape.method_4323();
            int var14 = var12 / var13;
            if ((var12 ^ var13) < 0 && var14 * var13 != var12) {
                --var14;
            }

            this$0.hasElytraTexture = var14 == 2;
            class_1060 var10000 = class_310.method_1551().method_1531();
            Capes var10001 = Capes.INSTANCE;
            String var10002 = this$0.uuid.toString();
            Intrinsics.checkNotNullExpressionValue(var10002, "toString(...)");
            Identifier var15 = var10001.identifier(var10002);
            Intrinsics.checkNotNull($cape);
            var10000.method_4616(var15, (class_1044)(new class_1043(this$0.parseCape($cape))));
            this$0.hasCape = true;
        }

    }

    public static final class Companion {
        private Companion() {
        }

        public Map<UUID, CapeProvider> getInstances() {
            return CapeProvider.instances;
        }

        public ExecutorService getCapeExecutor() {
            return CapeProvider.capeExecutor;
        }

        public CapeProvider fromProfile(GameProfile profile) {
            CapeProvider provider = this.getInstances().get(profile.getId());
            if (provider == null) {
                provider = new CapeProvider(profile);
            }

            return provider;
        }

        public void onLoadTexture(GameProfile profile) {
            CapeProvider CapeProvider = this.fromProfile(profile);

            if (Intrinsics.areEqual(profile, class_310.method_1551().method_53462())) {
                CapeProvider.setHasCape(false);
                CapeProvider.setHasAnimatedCape(false);
                CapeConfig config = Capes.INSTANCE.getCONFIG();
                this.getCapeExecutor().submit(CapeProvider.Companion::onLoadTexture$lambda$0);
            } else {
                this.getCapeExecutor().submit(CapeProvider.Companion::onLoadTexture$lambda$1);
            }
        }

        public HttpURLConnection connection(String url) {
            URLConnection var10000 = (new URL(url)).openConnection(class_310.method_1551().method_1487());
            HttpURLConnection connection = (HttpURLConnection)var10000;
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            return connection;
        }

        private static void onLoadTexture$lambda$0(CapeProvider $CapeProvider, CapeConfig $config) {
            $CapeProvider.setCape($config.getClientCapeType());
        }

        private static void onLoadTexture$lambda$1(GameProfile $profile, CapeProvider $CapeProvider) {
            CapeType[] var2 = CapeType.values();
            int var3 = 0;

            for(int var4 = var2.length; var3 < var4; ++var3) {
                CapeType capeType = var2[var3];
                if ($CapeProvider.setCape(capeType)) {
                    break;
                }
            }
        }
    }*/
}
