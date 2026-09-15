package com.twitchspamdetector.moderationengine.service.impl;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lleva la cuenta de repeticiones consecutivas del mismo texto por usuario+canal,
 * para la regla "repetido_3_veces".
 *
 * PLACEHOLDER: esto vive en memoria del propio proceso, asi que solo funciona
 * bien con una sola instancia del servicio. Si el proyecto crece a mas de una
 * instancia (o quieres persistir el estado), este es el punto para reemplazarlo
 * por Redis (ya lo tenias en el radar para v1).
 */
@Component
public class RecentMessagesTracker {

    private record Key(String channelId, String userId) {
    }

    private record LastMessage(String text, AtomicInteger repeatCount) {
    }

    private final Map<Key, LastMessage> lastMessageByUser = new ConcurrentHashMap<>();

    /**
     * Registra el mensaje actual y devuelve cuantas veces seguidas se ha repetido
     * el mismo texto (incluyendo este), para ese usuario en ese canal.
     */
    public int registerAndCountRepeats(String channelId, String userId, String text) {
        Key key = new Key(channelId, userId);
        LastMessage updated = lastMessageByUser.compute(key, (k, previous) -> {
            if (previous != null && previous.text().equals(text)) {
                previous.repeatCount().incrementAndGet();
                return previous;
            }
            return new LastMessage(text, new AtomicInteger(1));
        });
        return updated.repeatCount().get();
    }
}
