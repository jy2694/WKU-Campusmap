package kr.ac.wku;

import kr.ac.wku.configuration.StorageProperties;
import kr.ac.wku.configuration.WonkwangAPI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class CampusMapApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusMapApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageProperties properties){
		return (args) -> Files.createDirectories(Paths.get(properties.getLocation()));
	}
}
