package com.delivery.repository;

import com.delivery.entity.User;
import com.delivery.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
    List<User> findByRole(Role role);
    List<User> findByRoleAndActif(Role role, boolean actif);

    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_LIVREUR' AND u.actif = true")
    List<User> findAllLivreursActifs();
}
