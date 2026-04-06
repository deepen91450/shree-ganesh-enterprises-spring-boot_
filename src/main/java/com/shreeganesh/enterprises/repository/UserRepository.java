package com.shreeganesh.enterprises.repository;



import com.shreeganesh.enterprises.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);

}