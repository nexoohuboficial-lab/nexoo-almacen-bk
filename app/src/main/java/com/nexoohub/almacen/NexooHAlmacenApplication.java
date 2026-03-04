package com.nexoohub.almacen;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class NexooHAlmacenApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexooHAlmacenApplication.class, args);
    }
    @Bean
    public CommandLineRunner generarHash(PasswordEncoder encoder) {
        return args -> {
            String hash = encoder.encode("admin123");
            System.out.println("=========================================");
            System.out.println("TU HASH SEGURO PARA admin123 ES:");
            System.out.println(hash);
            System.out.println("=========================================");
        };
    }
}
