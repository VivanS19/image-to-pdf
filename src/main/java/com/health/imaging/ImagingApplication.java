/**
 @author Vivan Singhal
 */

package com.health.imaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ImagingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImagingApplication.class, args);
	}


}
