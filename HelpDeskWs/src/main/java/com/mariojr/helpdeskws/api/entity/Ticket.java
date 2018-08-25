package com.mariojr.helpdeskws.api.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mariojr.helpdeskws.api.enums.EnumPriority;
import com.mariojr.helpdeskws.api.enums.EnumStatus;

@Document
public class Ticket {

	@Id
	private String id;
	
	@DBRef(lazy = true)
	private User user;
	
	private Date date;
	
	private String title;
	
	private Integer number;
	
	private EnumPriority priority;
	
	private EnumStatus status;
	
	@DBRef(lazy = true)
	private User assignedUser;
	
	private String description;
	
	private String image;
	
	@Transient
	private List<ChangeStatus> changes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public EnumPriority getPriority() {
		return priority;
	}

	public void setPriority(EnumPriority priority) {
		this.priority = priority;
	}

	public EnumStatus getStatus() {
		return status;
	}

	public void setStatus(EnumStatus status) {
		this.status = status;
	}

	public User getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(User assignedUser) {
		this.assignedUser = assignedUser;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<ChangeStatus> getChanges() {
		if(changes == null) {
			changes = new ArrayList<ChangeStatus>();
		}
		return changes;
	}

	public void setChanges(List<ChangeStatus> changes) {
		this.changes = changes;
	}
	
}
