package com.courier.config;

import com.courier.entity.Employee;
import com.courier.entity.UserAccount;
import com.courier.repository.EmployeeRepository;
import com.courier.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer
 * ---------------
 * Seeds the 7 default employees + admin account on first run.
 * Mirrors the original loadDefaultEmployees() + AuthSystem static block logic.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository    empRepo;
    private final UserAccountRepository userRepo;

    public DataInitializer(EmployeeRepository empRepo, UserAccountRepository userRepo) {
        this.empRepo  = empRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        if (empRepo.count() == 0) seedDefaultEmployees();
    }

    private void seedAdmin() {
        if (!userRepo.existsByUsername("admin"))
            userRepo.save(new UserAccount("admin", "admin123", "ADMIN"));
    }

    /** Original DefaultEmployees.getDefaultEmployees() data preserved exactly. */
    private void seedDefaultEmployees() {
        Object[][] data = {
            {"Karthick Selvam", "Nellai",        9876543210L, "karthi",  600001, "karthi123"},
            {"Viswa",           "Kazhugumalai",  9876543211L, "viswa",   600040, "viswa123"},
            {"Himachalam",      "Gumudipoondi",  9876543212L, "hima",    600026, "hima123"},
            {"Akilanethran",    "Kovilpatti",    9876543213L, "akil",    600017, "akil123"},
            {"Dhariq Anvar",    "Puliyangudi",   9876543214L, "dhariq",  600020, "dhariq123"},
            {"Samuel",          "Nellai",        9876543215L, "samuel",  600042, "sam123"},
            {"Rahesh",          "Nellai",        9876543216L, "rahesh",  600045, "rahesh123"},
        };

        for (Object[] row : data) {
            String username = (String) row[3];
            if (!empRepo.existsByUsername(username)) {
                Employee e = new Employee(
                    (String)  row[0],
                    (String)  row[1],
                    (Long)    row[2],
                    username,
                    (Integer) row[4]
                );
                empRepo.save(e);
                if (!userRepo.existsByUsername(username))
                    userRepo.save(new UserAccount(username, (String) row[5], "EMPLOYEE"));
            }
        }
        System.out.println("Default employees seeded.");
    }
}
