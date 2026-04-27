package com.courier.controller;

import com.courier.dto.ChangeAvailabilityRequestDto;
import com.courier.dto.LoginRequestDto;
import com.courier.dto.UpdateStatusRequestDto;
import com.courier.entity.CourierEntity;
import com.courier.entity.UserAccount;
import com.courier.exception.InvalidLoginException;
import com.courier.service.AuthService;
import com.courier.service.CourierService;
import com.courier.service.EmployeeService;
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

/**
 * EmployeeController — mirrors original Employee Panel menu exactly
 *
 * POST /api/employee/login
 * GET  /api/employee/{username}/deliveries       — View Assigned Deliveries
 * PUT  /api/employee/couriers/{trackingId}/status — Update Courier Status
 * PUT  /api/employee/availability                 — Change Availability
 */
@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final AuthService     authService;
    private final EmployeeService empService;
    private final CourierService  courierService;

    public EmployeeController(AuthService authService,
                               EmployeeService empService,
                               CourierService courierService) {
        this.authService   = authService;
        this.empService    = empService;
        this.courierService = courierService;
    }

    /** POST /api/employee/login */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> employeeLogin(@Valid @RequestBody LoginRequestDto req) {
        UserAccount acc = authService.login(req.getUsername(), req.getPassword());
        if (!"EMPLOYEE".equals(acc.getRole()))
            throw new InvalidLoginException("Invalid employee login!");
        var emp = empService.getByUsername(req.getUsername());
        return ResponseEntity.ok(Map.of(
                "username",     emp.getUsername(),
                "name",         emp.getName(),
                "availability", emp.getAvailability(),
                "role",         acc.getRole()
        ));
    }

    /** GET /api/employee/{username}/deliveries — View Assigned Deliveries (option 1) */
    @GetMapping("/{username}/deliveries")
    public ResponseEntity<List<CourierEntity>> viewAssignedDeliveries(@PathVariable String username) {
        return ResponseEntity.ok(courierService.getCouriersByEmployee(username));
    }

    /**
     * PUT /api/employee/couriers/{trackingId}/status — Update Courier Status (option 2)
     * Body: { "status": "Delivered", "empUsername": "karthi" }
     * Original logic: only assigned employee can update; Delivered → employee FREE
     */
    @PutMapping("/couriers/{trackingId}/status")
    public ResponseEntity<Map<String, Object>> updateCourierStatus(
            @PathVariable Integer trackingId,
            @Valid @RequestBody UpdateStatusRequestDto req) {
        CourierEntity updated = courierService.updateStatus(trackingId, req);
        return ResponseEntity.ok(Map.of(
                "trackingId", updated.getTrackingId(),
                "status",     updated.getStatus(),
                "message",    "Status updated to: " + updated.getStatus()
        ));
    }

    /**
     * PUT /api/employee/availability — Change Availability (option 3)
     * Body: { "empUsername": "karthi", "availability": "FREE" }
     * Original: accepts FREE or ON_LEAVE only
     */
    @PutMapping("/availability")
    public ResponseEntity<Map<String, Object>> changeAvailability(
            @Valid @RequestBody ChangeAvailabilityRequestDto req) {
        var emp = empService.changeAvailability(req.getEmpUsername(), req.getAvailability());
        return ResponseEntity.ok(Map.of(
                "username",     emp.getUsername(),
                "availability", emp.getAvailability(),
                "message",      "Availability set to: " + emp.getAvailability()
        ));
    }

    /**
     * GET /api/employee/{username}/delivery-report
     * Downloads Excel with all assigned & delivered couriers for this employee,
     * including delivery details and deliveredDate.
     */
    @GetMapping("/{username}/delivery-report")
    public ResponseEntity<byte[]> downloadDeliveryReport(@PathVariable String username) throws Exception {
        List<CourierEntity> couriers = courierService.getCouriersByEmployee(username);

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("My Deliveries");

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            Font hf = wb.createFont();
            hf.setBold(true);
            hf.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hf);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // Delivered highlight style
            CellStyle deliveredStyle = wb.createCellStyle();
            deliveredStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            deliveredStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Delivery Report — Employee: " + username);
            CellStyle titleStyle = wb.createCellStyle();
            Font tf = wb.createFont();
            tf.setBold(true);
            tf.setFontHeightInPoints((short) 13);
            titleStyle.setFont(tf);
            titleCell.setCellStyle(titleStyle);

            // Section 1: All Assigned Couriers
            Row sec1Row = sheet.createRow(2);
            sec1Row.createCell(0).setCellValue("ALL ASSIGNED COURIERS");

            String[] headers = {"Tracking ID", "Receiver Name", "Destination", "Pincode",
                    "Weight (kg)", "Status", "Cost (₹)", "Payment Status",
                    "Sender", "Booking Date", "Delivered Date"};
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 4;
            for (CourierEntity c : couriers) {
                Row row = sheet.createRow(rowNum++);
                boolean isDelivered = "Delivered".equalsIgnoreCase(c.getStatus());
                row.createCell(0).setCellValue(c.getTrackingId() != null ? c.getTrackingId() : 0);
                row.createCell(1).setCellValue(nvl(c.getReceiverName()));
                row.createCell(2).setCellValue(nvl(c.getDestination()));
                row.createCell(3).setCellValue(c.getDestinationPincode() != null ? c.getDestinationPincode() : 0);
                row.createCell(4).setCellValue(c.getWeight() != null ? c.getWeight() : 0.0);
                Cell statusCell = row.createCell(5);
                statusCell.setCellValue(nvl(c.getStatus()));
                if (isDelivered) statusCell.setCellStyle(deliveredStyle);
                row.createCell(6).setCellValue(c.getCost() != null ? c.getCost() : 0.0);
                row.createCell(7).setCellValue(nvl(c.getPaymentStatus()));
                row.createCell(8).setCellValue(nvl(c.getSenderUsername()));
                row.createCell(9).setCellValue(nvl(c.getBookingDate()));
                row.createCell(10).setCellValue(nvl(c.getDeliveredDate()));
            }

            // Section 2: Delivered Only (with delivery details)
            rowNum += 2;
            Row sec2Row = sheet.createRow(rowNum++);
            sec2Row.createCell(0).setCellValue("DELIVERED COURIERS — WITH DELIVERY DETAILS");

            Row hdr2 = sheet.createRow(rowNum++);
            String[] delivHeaders = {"Tracking ID", "Receiver Name", "Destination", "Weight (kg)",
                    "Cost (₹)", "Sender", "Booking Date", "Delivered Date"};
            for (int i = 0; i < delivHeaders.length; i++) {
                Cell cell = hdr2.createCell(i);
                cell.setCellValue(delivHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            for (CourierEntity c : couriers) {
                if (!"Delivered".equalsIgnoreCase(c.getStatus())) continue;
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(c.getTrackingId() != null ? c.getTrackingId() : 0);
                row.createCell(1).setCellValue(nvl(c.getReceiverName()));
                row.createCell(2).setCellValue(nvl(c.getDestination()));
                row.createCell(3).setCellValue(c.getWeight() != null ? c.getWeight() : 0.0);
                row.createCell(4).setCellValue(c.getCost() != null ? c.getCost() : 0.0);
                row.createCell(5).setCellValue(nvl(c.getSenderUsername()));
                row.createCell(6).setCellValue(nvl(c.getBookingDate()));
                row.createCell(7).setCellValue(nvl(c.getDeliveredDate()));
            }

            // Summary
            rowNum += 2;
            long totalDelivered = couriers.stream().filter(c -> "Delivered".equalsIgnoreCase(c.getStatus())).count();
            Row sumRow = sheet.createRow(rowNum);
            sumRow.createCell(0).setCellValue("Total Assigned: " + couriers.size()
                    + "   |   Delivered: " + totalDelivered
                    + "   |   Pending: " + (couriers.size() - totalDelivered));

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);

            String today = LocalDate.now().toString();
            String filename = "delivery_report_" + username + "_" + today + ".xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        }
    }

    private String nvl(String s) { return s != null ? s : ""; }
}
