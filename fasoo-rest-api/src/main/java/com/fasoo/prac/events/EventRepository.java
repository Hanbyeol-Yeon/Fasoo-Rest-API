package com.fasoo.prac.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
	Page<Event> findByBasePriceBetween(int startBasePrice, int endBasePrice, Pageable pagealbe);
}
