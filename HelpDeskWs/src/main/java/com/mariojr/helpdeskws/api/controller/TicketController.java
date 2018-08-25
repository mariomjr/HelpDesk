package com.mariojr.helpdeskws.api.controller;

import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mariojr.helpdeskws.api.dto.Summary;
import com.mariojr.helpdeskws.api.entity.ChangeStatus;
import com.mariojr.helpdeskws.api.entity.Ticket;
import com.mariojr.helpdeskws.api.entity.User;
import com.mariojr.helpdeskws.api.enums.EnumProfile;
import com.mariojr.helpdeskws.api.enums.EnumStatus;
import com.mariojr.helpdeskws.api.response.Response;
import com.mariojr.helpdeskws.api.security.jwt.JwtTokenUtil;
import com.mariojr.helpdeskws.api.service.TicketService;
import com.mariojr.helpdeskws.api.service.UserService;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins="*")
public class TicketController {
 
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> createOrUpdate(HttpServletRequest request, @RequestBody Ticket ticket,
			BindingResult result){
		Response<Ticket> response = new Response<Ticket>();
		
		try {
			validadeCreateTicket(ticket, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(err -> response.getErrors().add(err.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			ticket.setStatus(EnumStatus.NEW);
			ticket.setUser(userFromRequest(request));
			ticket.setDate(new Date());
			ticket.setNumber(generateNumber());
			Ticket ticketCreate = ticketService.createOrUpdate(ticket);
			response.setData(ticketCreate);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@PutMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket,
			BindingResult result){
		Response<Ticket> response = new Response<Ticket>();
		
		try {
			validadeUpdateTicket(ticket, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(err -> response.getErrors().add(err.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			Ticket ticketCurrent = ticketService.findById(ticket.getId());
			ticket.setStatus(ticketCurrent.getStatus());
			ticket.setUser(ticketCurrent.getUser());
			ticket.setDate(ticketCurrent.getDate());
			ticket.setNumber(ticketCurrent.getNumber());
			if(ticketCurrent.getAssignedUser()!= null) {
				ticket.setAssignedUser(ticketCurrent.getAssignedUser());
			}
			Ticket ticketUpdate = ticketService.createOrUpdate(ticket);
			response.setData(ticketUpdate);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICAL')")
	public ResponseEntity<Response<Ticket>> findById(@PathVariable("id") String id){
		Response<Ticket> response = new Response<Ticket>();
		Ticket ticket = ticketService.findById(id);
		if(ticket == null) {
			response.getErrors().add("Ticket não encontrado! id:"+id);
			return ResponseEntity.badRequest().body(response);
		}
		Iterable<ChangeStatus> changesStatus = ticketService.listChangeStatus(ticket.getId());
		changesStatus.forEach(a-> {
				a.setTicket(null);
				ticket.getChanges().add(a);
			}
		);
		response.setData(ticket);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") String id){
		Response<String> response = new Response<String>();
		Ticket ticket = ticketService.findById(id);
		if(ticket == null) {
			response.getErrors().add("Ticket não encontrado! id:"+id);
			return ResponseEntity.badRequest().body(response);
		}
		ticketService.delete(id);
		return ResponseEntity.ok(new Response<String>());
	}
	
	@GetMapping(value ="{page}/{count}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICAL')")
	public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("count") int count){
		Response<Page<Ticket>> response = new Response<Page<Ticket>>();
		User userRequest = userFromRequest(request);
		if(userRequest.getProfile().equals(EnumProfile.ROLE_TECHNICAL)) {
			response.setData(ticketService.listTicket(page, count));
		}else if(userRequest.getProfile().equals(EnumProfile.ROLE_CUSTOMER)) {
			response.setData(ticketService.findByCurrentUser(page, count, userRequest.getId()));
		}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value ="{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICAL')")
	public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request, 
			@PathVariable("page") int page, 		@PathVariable("count") int count,	   @PathVariable("number") Integer number,
			@PathVariable("title") String title, 	@PathVariable("status") String status, @PathVariable("priority") String priority,
			@PathVariable("assigned") boolean assigned){
		
		title = title.equals("uninformed") ?"":title;
		status = status.equals("uninformed") ?"":status;
		priority = priority.equals("uninformed") ?"":priority;
		
		Response<Page<Ticket>> response = new Response<Page<Ticket>>();
		if(number>0){
			response.setData(ticketService.findByNumber(page, count, number));
		}else {
			User userRequest = userFromRequest(request);
			if(userRequest.getProfile().equals(EnumProfile.ROLE_TECHNICAL)) {
				if(assigned) {
					response.setData(ticketService.findByParameterAndAssignedUser(page, count, title, status, priority, userRequest.getId()));
				}else {
					response.setData(ticketService.findByParameters(page, count, title, status, priority));
				}
			}else if(userRequest.getProfile().equals(EnumProfile.ROLE_CUSTOMER)) {
				response.setData(ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId()));
			}
		}
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(value = "{id}/{status}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICAL')")
	public ResponseEntity<Response<Ticket>> changeStatus(HttpServletRequest request, @PathVariable("id") String id,
													@PathVariable("status") String status, @RequestBody Ticket ticket, BindingResult result){
		Response<Ticket> response = new Response<Ticket>();
		try {
			validadeChangeStatus(id, status, result);
			if(result.hasErrors()) {
				result.getAllErrors().forEach(err -> response.getErrors().add(err.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			Ticket ticketCurrent = ticketService.findById(id);
			ticketCurrent.setStatus(EnumStatus.getStatus(status));
			if(status.equals("ASSIGNED")) {
				ticketCurrent.setAssignedUser(userFromRequest(request));
			}
			Ticket ticketUpdate = ticketService.createOrUpdate(ticketCurrent);
			ChangeStatus changeStatus = new ChangeStatus();
			changeStatus.setDateChange(new Date());
			changeStatus.setTicket(ticketUpdate);
			changeStatus.setUserChange(userFromRequest(request));
			changeStatus.setStatus(EnumStatus.getStatus(status));
			ticketService.createChangeStatus(changeStatus);
			response.setData(ticketUpdate);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value="/summary")
	public ResponseEntity<Response<Summary>> findSummary(){
		Response<Summary> response = new Response<Summary>();
		Summary summary = new Summary();
		int amountNew = 0;
		int amountResolved = 0;
		int amountApproved  = 0;
		int amountDisapproved = 0;
		int amountAssigned = 0;
		int amountClosed = 0;
		
		Iterable<Ticket> tickets = ticketService.findAll();
		if(tickets != null) {
			for (Ticket ticket : tickets) {
				if(ticket.getStatus().equals(EnumStatus.NEW)) {
					amountNew++;
				}
				if(ticket.getStatus().equals(EnumStatus.RESOLVED)) {
					amountResolved++;
				}
				if(ticket.getStatus().equals(EnumStatus.APPROVED)) {
					amountApproved++;
				}
				if(ticket.getStatus().equals(EnumStatus.DISAPPROVED)) {
					amountDisapproved++;
				}
				if(ticket.getStatus().equals(EnumStatus.ASSIGNED)) {
					amountAssigned++;
				}
				if(ticket.getStatus().equals(EnumStatus.CLOSED)) {
					amountClosed++;
				}
			}
		}
		
		summary.setAmountNew(amountNew);
		summary.setAmountResolved(amountResolved);
		summary.setAmountApproved(amountApproved);
		summary.setAmountDisapproved(amountDisapproved);
		summary.setAmountAssigned(amountAssigned);
		summary.setAmountClosed(amountClosed);
		response.setData(summary);
		return ResponseEntity.ok(response);
	}
	
	
	private Integer generateNumber() {
		Random random = new Random();
		return random.nextInt(9999);
	}

	private User userFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		String email = jwtTokenUtil.gertUsernameFromToken(token);
		return userService.findByEmail(email);
	}

	private void validadeCreateTicket(Ticket ticket, BindingResult result) {
		if(ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Título obrigatório!"));
		}
	}
	
	private void validadeUpdateTicket(Ticket ticket, BindingResult result) {
		if(ticket.getId() == null) {
			result.addError(new ObjectError("Ticket", "ID obrigatório!"));
		}
		if(ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Título obrigatório!"));
		}
	}
	
	private void validadeChangeStatus(String id, String status, BindingResult result) {
		if(id == null || id.equals("")) {
			result.addError(new ObjectError("Ticket", "ID não informado!"));
		}
		if(status == null || status.equals("")) {
			result.addError(new ObjectError("Ticket", "Status não informado!"));
		}
	}
	
}
