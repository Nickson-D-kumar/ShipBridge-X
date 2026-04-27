package com.courier.service;

import com.courier.entity.UserAccount;
import com.courier.exception.DuplicateUsernameException;
import com.courier.exception.InvalidLoginException;
import com.courier.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userRepo;

    public AuthService(UserAccountRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Original AuthSystem.login() logic preserved:
     * Returns UserAccount on match, throws InvalidLoginException otherwise.
     */
    public UserAccount login(String username, String password) {
        return userRepo.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new InvalidLoginException("Invalid username or password!"));
    }

    /**
     * Original AuthSystem.signUpCustomer() logic: returns false if duplicate.
     * Here we throw DuplicateUsernameException so REST layer can respond 409.
     */
    @Transactional
    public void signUpCustomer(String username, String password) {
        if (userRepo.existsByUsername(username))
            throw new DuplicateUsernameException("Username '" + username + "' already exists!");
        userRepo.save(new UserAccount(username, password, "CUSTOMER"));
    }

    /** Original AuthSystem.addEmployeeLogin() — INSERT OR IGNORE behaviour */
    @Transactional
    public void addEmployeeLogin(String username, String password) {
        if (!userRepo.existsByUsername(username))
            userRepo.save(new UserAccount(username, password, "EMPLOYEE"));
    }

    public String getPasswordFor(String username) {
        return userRepo.findById(username)
                .map(UserAccount::getPassword)
                .orElse("unknown");
    }
}
