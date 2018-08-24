package com.mariojr.helpdeskws.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mariojr.helpdeskws.api.entity.ChangeStatus;

public interface ChangeStatusRepository extends MongoRepository<ChangeStatus, String>{

	Iterable<ChangeStatus> findByTicketIdOrderByDateChangeDesc(String ticketId);
}
