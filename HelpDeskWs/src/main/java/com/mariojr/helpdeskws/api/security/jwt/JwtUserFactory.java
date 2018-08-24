package com.mariojr.helpdeskws.api.security.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mariojr.helpdeskws.api.entity.User;
import com.mariojr.helpdeskws.api.enums.EnumProfile;

public class JwtUserFactory {

	private JwtUserFactory() {
	}
	
	public static JwtUser create(User user) {
		return new JwtUser(user.getId(), user.getEmail(), 
				user.getPassword(), mapToGrantedAuthorities(user.getProfile()));
	}

	private static Collection<? extends GrantedAuthority> mapToGrantedAuthorities(EnumProfile profile) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(profile.toString()));
		return authorities;
	}
}
