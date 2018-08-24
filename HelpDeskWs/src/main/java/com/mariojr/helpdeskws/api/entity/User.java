package com.mariojr.helpdeskws.api.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mariojr.helpdeskws.api.enums.EnumProfile;

@Document
public class User {

	@Id
	private String id;
	
	@Indexed(unique = true)
	@Email(message="E-mail inválido")
	private String email;
	
	@Size(min=6)
	private String password;
	
	private EnumProfile profile;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public EnumProfile getProfile() {
		return profile;
	}

	public void setProfile(EnumProfile profile) {
		this.profile = profile;
	}
	
}
