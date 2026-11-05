
package com.example.demo;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/api")
class ApiController {

    // 1. Repository dependencies
    private final StudentRepository students;
    private final AttendanceRepository attendance;
    private final MarkRepository marks;
    private final FeeRepository fees;
    private final NoticeRepository notices;

    // 2. Constructor injection
    ApiController(
            StudentRepository s,
            AttendanceRepository a,
            MarkRepository m,
            FeeRepository f,
            NoticeRepository n) {

        students = s;
        attendance = a;
        marks = m;
        fees = f;
        notices = n;
    }

    // 3. DASHBOARD API
    @GetMapping("/dashboard")
    Map<String, Object> dashboard() {

        List<Student> all = students.findAll();
        List<Fee> fs = fees.findAll();

        double total = fs.stream()
                .mapToDouble(x -> nz(x.getAmount()))
                .sum();

        double paid = fs.stream()
                .mapToDouble(x -> nz(x.getPaidAmount()))
                .sum();

        return Map.of(
                "totalStudents", all.size(),
                "activeStudents", all.stream()
                        .filter(x -> "Active".equalsIgnoreCase(x.getStatus()))
                        .count(),
                "totalAttendance", attendance.count(),
                "totalFees", total,
                "paidFees", paid,
                "pendingFees", Math.max(0, total - paid),
                "recentStudents", all.stream()
                        .sorted(Comparator.comparing(Student::getId).reversed())
                        .limit(5).toList(),
                "recentNotices", notices.findAll().stream()
                        .sorted(Comparator.comparing(Notice::getCreatedAt).reversed())
                        .limit(5).toList()
        );
    }

    // 4. STUDENT APIs

    // Get all students or search students
    @GetMapping("/students")
    List<Student> getStudents(
            @RequestParam(required = false) String search) {

        if (search == null || search.isBlank())
            return students.findAll();

        return students.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                search, search, search);
    }

    // Add a new student
    @PostMapping("/students")
    ResponseEntity<?> addStudent(@RequestBody Student s) {

        if (s.getName() == null || s.getName().isBlank()
                || s.getEmail() == null || s.getEmail().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Name and email are required"));
        }

        if (students.findAll().stream()
                .anyMatch(x -> x.getEmail().equalsIgnoreCase(s.getEmail()))) {

            return ResponseEntity.status(409)
                    .body(Map.of("error", "Email already exists"));
        }

        s.setId(null);

        return ResponseEntity.ok(students.save(s));
    }

    // Update student details
    @PutMapping("/students/{id}")
    ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @RequestBody Student s) {

        Optional<Student> found = students.findById(id);

        if (found.isEmpty())
            return ResponseEntity.notFound().build();

        Student old = found.get();

        old.setName(s.getName());
        old.setEmail(s.getEmail());
        old.setPhone(s.getPhone());
        old.setDepartment(s.getDepartment());
        old.setYearOfStudy(s.getYearOfStudy());
        old.setStatus(s.getStatus());

        return ResponseEntity.ok(students.save(old));
    }

    // Delete student
    @DeleteMapping("/students/{id}")
    ResponseEntity<?> deleteStudent(@PathVariable Long id) {

        if (!students.existsById(id))
            return ResponseEntity.notFound().build();

        students.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // 5. ATTENDANCE APIs

    // Get all attendance records or filter by student ID
    @GetMapping("/attendance")
    List<Attendance> getAttendance(
            @RequestParam(required = false) Long studentId) {

        return studentId == null
                ? attendance.findAll()
                : attendance.findByStudentId(studentId);
    }

    // Add or update attendance
    @PostMapping("/attendance")
    ResponseEntity<?> saveAttendance(@RequestBody Attendance a) {

        if (!students.existsById(a.getStudentId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Student not found"));
        }

        if (a.getAttendanceDate() == null)
            a.setAttendanceDate(LocalDate.now());

        if (a.getStatus() == null
                || !List.of("Present", "Absent", "Late").contains(a.getStatus())) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Status must be Present, Absent or Late"));
        }

        Optional<Attendance> existing = attendance.findAll().stream()
                .filter(x -> x.getStudentId().equals(a.getStudentId())
                        && x.getAttendanceDate().equals(a.getAttendanceDate()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setStatus(a.getStatus());
            return ResponseEntity.ok(attendance.save(existing.get()));
        }

        a.setId(null);

        return ResponseEntity.ok(attendance.save(a));
    }

    // 6. MARKS APIs

    // Get all marks or filter by student ID
    @GetMapping("/marks")
    List<Mark> getMarks(
            @RequestParam(required = false) Long studentId) {

        return studentId == null
                ? marks.findAll()
                : marks.findByStudentId(studentId);
    }

    // Add marks
    @PostMapping("/marks")
    ResponseEntity<?> saveMark(@RequestBody Mark m) {

        if (!students.existsById(m.getStudentId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Student not found"));
        }

        if (m.getMarksObtained() == null
                || m.getMaxMarks() == null
                || m.getMarksObtained() < 0
                || m.getMaxMarks() <= 0
                || m.getMarksObtained() > m.getMaxMarks()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Enter valid marks and maximum marks"));
        }

        m.setId(null);

        return ResponseEntity.ok(marks.save(m));
    }

    // Delete marks
    @DeleteMapping("/marks/{id}")
    ResponseEntity<?> deleteMark(@PathVariable Long id) {

        if (!marks.existsById(id))
            return ResponseEntity.notFound().build();

        marks.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // 7. FEES APIs

    // Get all fees or filter by student ID
    @GetMapping("/fees")
    List<Fee> getFees(
            @RequestParam(required = false) Long studentId) {

        return studentId == null
                ? fees.findAll()
                : fees.findByStudentId(studentId);
    }

    // Add fee record
    @PostMapping("/fees")
    ResponseEntity<?> saveFee(@RequestBody Fee f) {

        if (!students.existsById(f.getStudentId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Student not found"));
        }

        if (f.getAmount() == null
                || f.getAmount() < 0
                || f.getPaidAmount() == null
                || f.getPaidAmount() < 0
                || f.getPaidAmount() > f.getAmount()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Enter valid fee amounts"));
        }

        f.setStatus(
                f.getPaidAmount() >= f.getAmount()
                        ? "Paid"
                        : f.getPaidAmount() > 0
                        ? "Partial"
                        : "Pending"
        );

        f.setId(null);

        return ResponseEntity.ok(fees.save(f));
    }

    // Delete fee record
    @DeleteMapping("/fees/{id}")
    ResponseEntity<?> deleteFee(@PathVariable Long id) {

        if (!fees.existsById(id))
            return ResponseEntity.notFound().build();

        fees.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // 8. NOTIFICATION APIs

    // Get all notifications
    @GetMapping("/notifications")
    List<Notice> getNotices() {

        return notices.findAll().stream()
                .sorted(Comparator.comparing(Notice::getCreatedAt).reversed())
                .toList();
    }

    // Add notification
    @PostMapping("/notifications")
    Notice addNotice(@RequestBody Notice n) {

        n.setId(null);
        n.setCreatedAt(LocalDateTime.now());

        return notices.save(n);
    }

    // Delete notification
    @DeleteMapping("/notifications/{id}")
    ResponseEntity<?> deleteNotice(@PathVariable Long id) {

        if (!notices.existsById(id))
            return ResponseEntity.notFound().build();

        notices.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // 9. STUDENT CSV REPORT

    @GetMapping(value = "/reports/students.csv", produces = "text/csv")
    ResponseEntity<String> studentReport() {

        StringBuilder b = new StringBuilder(
                "ID,Name,Email,Phone,Department,Year,Status\n");

        for (Student s : students.findAll()) {
            b.append(s.getId()).append(',')
                    .append(csv(s.getName())).append(',')
                    .append(csv(s.getEmail())).append(',')
                    .append(csv(s.getPhone())).append(',')
                    .append(csv(s.getDepartment())).append(',')
                    .append(csv(s.getYearOfStudy())).append(',')
                    .append(csv(s.getStatus())).append('\n');
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=students-report.csv")
                .body(b.toString());
    }

    // 10. ATTENDANCE CSV REPORT

    @GetMapping(value = "/reports/attendance.csv", produces = "text/csv")
    ResponseEntity<String> attendanceReport() {

        StringBuilder b = new StringBuilder(
                "ID,Student ID,Date,Status\n");

        for (Attendance a : attendance.findAll()) {
            b.append(a.getId()).append(',')
                    .append(a.getStudentId()).append(',')
                    .append(a.getAttendanceDate()).append(',')
                    .append(csv(a.getStatus())).append('\n');
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=attendance-report.csv")
                .body(b.toString());
    }

    // 11. MARKS CSV REPORT

    @GetMapping(value = "/reports/marks.csv", produces = "text/csv")
    ResponseEntity<String> marksReport() {

        StringBuilder b = new StringBuilder(
                "ID,Student ID,Subject,Exam,Marks,Maximum\n");

        for (Mark m : marks.findAll()) {
            b.append(m.getId()).append(',')
                    .append(m.getStudentId()).append(',')
                    .append(csv(m.getSubject())).append(',')
                    .append(csv(m.getExamType())).append(',')
                    .append(m.getMarksObtained()).append(',')
                    .append(m.getMaxMarks()).append('\n');
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=marks-report.csv")
                .body(b.toString());
    }

    // 12. FEES CSV REPORT

    @GetMapping(value = "/reports/fees.csv", produces = "text/csv")
    ResponseEntity<String> feeReport() {

        StringBuilder b = new StringBuilder(
                "ID,Student ID,Description,Amount,Paid,Due Date,Status\n");

        for (Fee f : fees.findAll()) {
            b.append(f.getId()).append(',')
                    .append(f.getStudentId()).append(',')
                    .append(csv(f.getDescription())).append(',')
                    .append(nz(f.getAmount())).append(',')
                    .append(nz(f.getPaidAmount())).append(',')
                    .append(f.getDueDate()).append(',')
                    .append(csv(f.getStatus())).append('\n');
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=fees-report.csv")
                .body(b.toString());
    }

    // 13. HELPER METHODS

    // Convert null values into zero
    private double nz(Double x) {
        return x == null ? 0 : x;
    }

    // Format values for CSV
    private String csv(String s) {
        if (s == null)
            return "";

        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
