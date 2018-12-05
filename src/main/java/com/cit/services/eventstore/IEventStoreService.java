package com.cit.services.eventstore;

import com.cit.models.Event;

public interface IEventStoreService {

    Event getLastEventForCardId(String cardId);
    void storeEvent(Event event);

}
