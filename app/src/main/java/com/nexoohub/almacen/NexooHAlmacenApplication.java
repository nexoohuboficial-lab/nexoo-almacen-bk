package com.nexoohub.almacen;

import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class NexooHAlmacenApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexooHAlmacenApplication.class, args);
    }
    
    /**
     * Inicializa un usuario administrador por defecto solo en perfil dev
     * Usuario: admin
     * Password: admin123
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner initUsuarioAdmin(UsuarioRepository usuarioRepo, PasswordEncoder encoder) {
        return args -> {
            // Solo crear usuario si no existe
            if (usuarioRepo.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                usuarioRepo.save(admin);
                
                System.out.println("=========================================");
                System.out.println("✅ Usuario administrador creado:");
                System.out.println("   Username: admin");
                System.out.println("   Password: admin123");
                System.out.println("=========================================");
            }
        };
    }
}
