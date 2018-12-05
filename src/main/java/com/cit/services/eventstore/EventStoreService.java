package com.cit.services.eventstore;

import com.cit.models.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class EventStoreService implements IEventStoreService {

    // cache of the cardId holders Last Event
    private Map<UUID, Event> cardLastEventCache = new HashMap<>();

    @Override
    public Event getLastEventForCardId(String cardId) {
        if (log.isDebugEnabled()) {
            log.debug("EventStoreService.getLastEventForCardId(cardId={}",cardId);
        }

        return cardLastEventCache.get(UUID.fromString(cardId));
    }

    @Override
    public void storeEvent(Event event) {

        if (event == null) {
            throw new IllegalArgumentException("Event parameter should not be null");
        }

        if (log.isDebugEnabled()) {
            log.debug("EventStoreService.storeEvent(Event={}" + event);
        }

        cardLastEventCache.put(UUID.fromString(event.getCardId()),event);
    }
}
