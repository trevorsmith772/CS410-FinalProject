package com.CS410.GradeBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import org.springframework.boot.Banner;

/**
 * @brief This application uses Spring Shell to provide a 
 * 		command line shell interface for grade management.
 * 		This application is currently only usable with an 
 * 		SSH connection to onyx.boisestate.edu in order to 
 * 		retrieve the JDBC connector and database, but can be
 * 		configured with an alternate database and connector.
 * 		This application was created for the CS410 Databases
 * 		course at Boise State University.
 * 
 * @author Trevor Smith (trevorsmith772)
 * @author Berto Cisneros (bertocisneros)
 * @date 04/28/2022
 */
@SpringBootApplication
public class GradeBookApplication {
	public static void main(String[] args) {
		
		SpringApplication application = new SpringApplication(GradeBookApplication.class);
        //application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
	}
}