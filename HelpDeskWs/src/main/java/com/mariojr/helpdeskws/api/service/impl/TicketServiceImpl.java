package com.mariojr.helpdeskws.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mariojr.helpdeskws.api.entity.ChangeStatus;
import com.mariojr.helpdeskws.api.entity.Ticket;
import com.mariojr.helpdeskws.api.repository.ChangeStatusRepository;
import com.mariojr.helpdeskws.api.repository.TicketRepository;
import com.mariojr.helpdeskws.api.service.TicketService;

@Service
public class TicketServiceImpl implements TicketService{

	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private ChangeStatusRepository changeStatusRepository;
	
	@Override
	public Ticket createOrUpdate(Ticket ticket) {
		return this.ticketRepository.save(ticket);
	}

	@Override
	public Ticket findById(String id) {
		return this.ticketRepository.findById(id).get();
	}

	@Override
	public void delete(String id) {
		this.ticketRepository.deleteById(id);
	}

	@Override
	public Page<Ticket> listTicket(int page, int count) {
		return this.ticketRepository.findAll(PageRequest.of(page, count));
	}

	@Override
	public ChangeStatus createChangeStatus(ChangeStatus changeStatus) {
		return this.changeStatusRepository.save(changeStatus);
	}

	@Override
	public Iterable<ChangeStatus> listChangeStatus(String ticketId) {
		return this.changeStatusRepository.findByTicketIdOrderByDateChangeDesc(ticketId);
	}

	@Override
	public Page<Ticket> findByCurrentUser(int page, int count, String userId) {
		return this.ticketRepository.findByUserIdOrderByDateDesc(PageRequest.of(page, count), userId);
	}

	@Override
	public Page<Ticket> findByParameters(int page, int count, String title, String status, String priority) {
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(title, status, priority, PageRequest.of(page, count));
	}

	@Override
	public Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status,
			String priority, String userId) {
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(title, status, priority, 
				userId, PageRequest.of(page, count));
	}

	@Override
	public Page<Ticket> findByNumber(int page, int count, Integer number) {
		return this.ticketRepository.findByNumber(number, PageRequest.of(page, count));
	}

	@Override
	public Iterable<Ticket> findAll() {
		return this.ticketRepository.findAll();
	}

	@Override
	public Page<Ticket> findByParameterAndAssignedUser(int page, int count, String title, String status,
			String priority, String assignedUser) {
		return this.ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDesc(
				title, status, priority, assignedUser, PageRequest.of(page, count));
	}

}
