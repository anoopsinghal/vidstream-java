package dev.anoop.vidstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VidstreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(VidstreamApplication.class, args);
	}
}
