package com.courier.repository;

import com.courier.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByUsername(String username);
    List<Employee> findByAvailability(String availability);
    boolean existsByUsername(String username);
}
