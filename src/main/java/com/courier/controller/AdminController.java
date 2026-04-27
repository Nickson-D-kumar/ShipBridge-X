package com.courier.controller;

import com.courier.dto.AddEmployeeRequestDto;
import com.courier.dto.LoginRequestDto;
import com.courier.entity.*;
import com.courier.exception.InvalidLoginException;
import com.courier.service.*;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AdminController — mirrors original Admin Panel menu exactly
 *
 * POST /api/admin/login
 * POST /api/admin/employees          — Add Employee
 * GET  /api/admin/employees          — View Employees
 * GET  /api/admin/couriers           — View All Couriers
 * GET  /api/admin/payments           — View All Payments
 * GET  /api/admin/customers          — View All Customers
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AuthService     authService;
    private final EmployeeService empService;
    private final CustomerService customerService;
    private final CourierService  courierService;
    private final PaymentService  paymentService;

    public AdminController(AuthService authService,
                           EmployeeService empService,
                           CustomerService customerService,
                           CourierService courierService,
                           PaymentService paymentService) {
        this.authService     = authService;
        this.empService      = empService;
        this.customerService = customerService;
        this.courierService  = courierService;
        this.paymentService  = paymentService;
    }

    /** POST /api/admin/login — validates ADMIN role */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@Valid @RequestBody LoginRequestDto req) {
        UserAccount acc = authService.login(req.getUsername(), req.getPassword());
        if (!"ADMIN".equals(acc.getRole()))
            throw new InvalidLoginException("Invalid admin login!");
        return ResponseEntity.ok(Map.of("username", acc.getUsername(), "role", acc.getRole()));
    }

    /** POST /api/admin/employees — Add Employee (Admin Panel option 1) */
    @PostMapping("/employees")
    public ResponseEntity<Map<String, Object>> addEmployee(@Valid @RequestBody AddEmployeeRequestDto req) {
        Employee emp = empService.addEmployee(req);
        return ResponseEntity.ok(Map.of(
                "empId",    emp.getEmpId(),
                "username", emp.getUsername(),
                "name",     emp.getName(),
                "message",  "Employee Added!"
        ));
    }

    /** GET /api/admin/employees — View Employees (Admin Panel option 2) */
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> viewEmployees() {
        return ResponseEntity.ok(empService.getAllEmployees());
    }

    /** GET /api/admin/couriers — View All Couriers (Admin Panel option 3) */
    @GetMapping("/couriers")
    public ResponseEntity<List<CourierEntity>> viewAllCouriers() {
        return ResponseEntity.ok(courierService.getAllCouriers());
    }

    /** GET /api/admin/payments — View All Payments (Admin Panel option 4) */
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> viewAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /** GET /api/admin/customers — View All Customers (Admin Panel option 5) */
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> viewAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    /**
     * GET /api/admin/couriers/daily-report
     * Downloads an Excel file with today's couriers (filtered by bookingDate == today).
     */
    @GetMapping("/couriers/daily-report")
    public ResponseEntity<byte[]> downloadDailyReport() throws Exception {
        LocalDate today = LocalDate.now();
        String todayStr = today.getDayOfMonth() + "-" + today.getMonthValue() + "-" + today.getYear();

        List<CourierEntity> allCouriers = courierService.getAllCouriers();
        List<CourierEntity> todayCouriers = allCouriers.stream()
                .filter(c -> todayStr.equals(c.getBookingDate()))
                .collect(Collectors.toList());

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font hf2 = wb.createFont();
            hf2.setBold(true);
            hf2.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hf2);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            Sheet sheet = wb.createSheet("Daily Courier Report - " + todayStr);

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Daily Courier Report — " + todayStr);
            CellStyle titleStyle = wb.createCellStyle();
            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Header row
            String[] headers = {"Tracking ID", "Receiver Name", "Destination", "Pincode",
                    "Weight (kg)", "Status", "Cost (₹)", "Payment Status",
                    "Sender", "Assigned Employee", "Booking Date", "Delivered Date"};
            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 2;
            for (CourierEntity c : todayCouriers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(c.getTrackingId() != null ? c.getTrackingId() : 0);
                row.createCell(1).setCellValue(nvl(c.getReceiverName()));
                row.createCell(2).setCellValue(nvl(c.getDestination()));
                row.createCell(3).setCellValue(c.getDestinationPincode() != null ? c.getDestinationPincode() : 0);
                row.createCell(4).setCellValue(c.getWeight() != null ? c.getWeight() : 0.0);
                row.createCell(5).setCellValue(nvl(c.getStatus()));
                row.createCell(6).setCellValue(c.getCost() != null ? c.getCost() : 0.0);
                row.createCell(7).setCellValue(nvl(c.getPaymentStatus()));
                row.createCell(8).setCellValue(nvl(c.getSenderUsername()));
                row.createCell(9).setCellValue(nvl(c.getEmpUsername()));
                row.createCell(10).setCellValue(nvl(c.getBookingDate()));
                row.createCell(11).setCellValue(nvl(c.getDeliveredDate()));
            }

            // Summary row
            Row summaryRow = sheet.createRow(rowNum + 1);
            summaryRow.createCell(0).setCellValue("Total Couriers Today: " + todayCouriers.size());
            long delivered = todayCouriers.stream().filter(c -> "Delivered".equalsIgnoreCase(c.getStatus())).count();
            summaryRow.createCell(3).setCellValue("Delivered: " + delivered);
            summaryRow.createCell(6).setCellValue("Pending: " + (todayCouriers.size() - delivered));

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);

            String filename = "daily_courier_report_" + todayStr.replace("-", "_") + ".xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }

    private String nvl(String s) { return s != null ? s : ""; }
}
