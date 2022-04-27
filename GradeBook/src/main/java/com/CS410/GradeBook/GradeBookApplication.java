package com.CS410.GradeBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellComponent;


@SpringBootApplication
public class GradeBookApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(GradeBookApplication.class, args);
	}
	@Autowired
	JdbcTemplate jdbc;

	@Override
	public void run(String... strings) throws Exception {
		jdbc.execute("INSERT INTO categories(category_id, name) VALUES (1,'bitch')");
	}
}


@ShellComponent
class PrimaryCommands {

}
