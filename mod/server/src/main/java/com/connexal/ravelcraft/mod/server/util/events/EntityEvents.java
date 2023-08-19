package com.connexal.ravelcraft.mod.server.util.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class EntityEvents {
    @FunctionalInterface
    public interface EntityDamageEvent {
        boolean onEntityDamage(LivingEntity entity, DamageSource source, float amount);
    }
    public static final Event<EntityDamageEvent> DAMAGE = EventFactory.createArrayBacked(EntityDamageEvent.class, listeners -> (entity, source, amount) -> {
        for (EntityDamageEvent listener : listeners) {
            boolean out = listener.onEntityDamage(entity, source, amount);
            if (!out) {
                return false;
            }
        }

        return true;
    });

    @FunctionalInterface
    public interface EntityDeathEvent {
        void onEntityDeath(LivingEntity entity, DamageSource source);
    }
    public static final Event<EntityDeathEvent> DEATH = EventFactory.createArrayBacked(EntityDeathEvent.class, listeners -> (entity, source) -> {
        for (EntityDeathEvent listener : listeners) {
            listener.onEntityDeath(entity, source);
        }
    });

    @FunctionalInterface
    public interface EntityDropLootEvent {
        boolean onEntityDropLoot(LivingEntity entity, DamageSource source);
    }
    public static final Event<EntityDropLootEvent> DROP_LOOT = EventFactory.createArrayBacked(EntityDropLootEvent.class, listeners -> (entity, source) -> {
        for (EntityDropLootEvent listener : listeners) {
            boolean out = listener.onEntityDropLoot(entity, source);
            if (!out) {
                return false;
            }
        }

        return true;
    });
}
