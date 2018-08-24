package com.mariojr.helpdeskws;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mariojr.helpdeskws.api.entity.User;
import com.mariojr.helpdeskws.api.enums.EnumProfile;
import com.mariojr.helpdeskws.api.repository.UserRepository;

@SpringBootApplication
public class HelpDeskWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelpDeskWsApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args->{
			initUsers(userRepository,passwordEncoder);
		};
	}
	
	private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		User admin = new User();
		admin.setEmail("admin@helpdesk.com");
		admin.setPassword(passwordEncoder.encode("123456"));
		admin.setProfile(EnumProfile.ROLE_ADMIN);
		
		User find = userRepository.findByEmail(admin.getEmail());
		if(find == null) {
			userRepository.save(admin);
		}
	}
}
