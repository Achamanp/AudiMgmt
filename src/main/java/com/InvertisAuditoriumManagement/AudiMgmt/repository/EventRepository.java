package com.InvertisAuditoriumManagement.AudiMgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.Event;
@Repository
public interface EventRepository extends JpaRepository<Event, Long>{

	Event findByEventName(String eventName);


}