package com.cit.repositories;

import com.cit.models.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository  extends CrudRepository<Event, Long> {
    Event findDistinctFirstByCardId(String cardId);
    List<Event> findAllByCardId(String cardId);
}

