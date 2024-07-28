package com.ufrn.frootmart.repository;

import com.ufrn.frootmart.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
}

