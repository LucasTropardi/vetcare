package com.lucast.vetcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = {
		"org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
})
public class VetcareApplication {

	public static void main(String[] args) {
		SpringApplication.run(VetcareApplication.class, args);
	}
}
