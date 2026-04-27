package com.courier.service;

import com.courier.dto.AddEmployeeRequestDto;
import com.courier.entity.Employee;
import com.courier.exception.DuplicateUsernameException;
import com.courier.exception.NoEmployeeAvailableException;
import com.courier.exception.ResourceNotFoundException;
import com.courier.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository empRepo;
    private final AuthService        authService;

    public EmployeeService(EmployeeRepository empRepo, AuthService authService) {
        this.empRepo      = empRepo;
        this.authService  = authService;
    }

    public List<Employee> getAllEmployees() {
        return empRepo.findAll();
    }

    public Employee getByUsername(String username) {
        return empRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + username));
    }

    /**
     * Original admin "Add Employee" logic preserved:
     * Creates employee + user_account row atomically.
     */
    @Transactional
    public Employee addEmployee(AddEmployeeRequestDto req) {
        if (empRepo.existsByUsername(req.getUsername()))
            throw new DuplicateUsernameException("Username '" + req.getUsername() + "' already taken!");

        Employee emp = new Employee(
                req.getName(), req.getAddress(), req.getPhone(),
                req.getUsername(), req.getPincode()
        );
        Employee saved = empRepo.save(emp);
        authService.addEmployeeLogin(req.getUsername(), req.getPassword());
        return saved;
    }

    /**
     * Original employee "Change Availability" logic preserved:
     * Accepts FREE or ON_LEAVE (BUSY is set internally by booking).
     */
    @Transactional
    public Employee changeAvailability(String username, String availability) {
        if (!availability.equals("FREE") && !availability.equals("ON_LEAVE"))
            throw new IllegalArgumentException("Availability must be FREE or ON_LEAVE");
        Employee emp = getByUsername(username);
        emp.setAvailability(availability);
        return empRepo.save(emp);
    }

    /** Internal — called when courier is booked (markBusy original logic). */
    @Transactional
    public void markBusy(String username) {
        Employee emp = getByUsername(username);
        emp.markBusy();
        empRepo.save(emp);
    }

    /** Internal — called when delivery is Delivered (markFree original logic). */
    @Transactional
    public void markFree(String username) {
        Employee emp = getByUsername(username);
        emp.markFree();
        empRepo.save(emp);
    }

    /**
     * Original findNearestEmployee() logic preserved exactly:
     * Finds FREE employee with smallest |pincode difference|.
     * Exact-match (diff==0) is returned immediately.
     *
     * FIX: NoEmployeeAvailableException is now imported at the top of the file
     * instead of being referenced with a full inline package path.
     */
    public Employee findNearest(int destinationPincode) {
        List<Employee> freeEmps = empRepo.findByAvailability("FREE");
        if (freeEmps.isEmpty())
            throw new NoEmployeeAvailableException(
                    "No FREE employees available! All are BUSY or ON_LEAVE.");

        Employee nearest = null;
        int minDiff = Integer.MAX_VALUE;
        for (Employee e : freeEmps) {
            int diff = Math.abs(e.getPincode() - destinationPincode);
            if (diff == 0) return e;
            if (diff < minDiff) { minDiff = diff; nearest = e; }
        }
        return nearest;
    }
}
