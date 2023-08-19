package com.connexal.ravelcraft.mod.server.util.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class EntityEvents {
    @FunctionalInterface
    public interface PlayerDamageEvent {
        boolean onEntityDamage(LivingEntity entity, DamageSource source, float amount);
    }
    public static final Event<PlayerDamageEvent> DAMAGE = EventFactory.createArrayBacked(PlayerDamageEvent.class, listeners -> (entity, source, amount) -> {
        for (PlayerDamageEvent listener : listeners) {
            boolean out = listener.onEntityDamage(entity, source, amount);
            if (!out) {
                return false;
            }
        }

        return true;
    });
}
