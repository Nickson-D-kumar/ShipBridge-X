package com.courier.repository;

import com.courier.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    Optional<UserAccount> findByUsernameAndPassword(String username, String password);
    boolean existsByUsername(String username);
}
