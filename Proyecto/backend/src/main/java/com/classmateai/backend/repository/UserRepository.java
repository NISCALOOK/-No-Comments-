package com.classmateai.backend.repository;

import com.classmateai.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
<<<<<<< HEAD
}
=======
}
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454
