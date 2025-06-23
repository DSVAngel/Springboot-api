package com.dsvangel.spring.segurity.postgresql.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.dsvangel.spring.segurity.postgresql.models.ERole;
import com.dsvangel.spring.segurity.postgresql.models.Role;
import com.dsvangel.spring.segurity.postgresql.repository.RoleRepository;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar roles si no existen
        for (ERole role : ERole.values()) {
            if (!roleRepository.existsByName(role)) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
            }
        }
    }
} 