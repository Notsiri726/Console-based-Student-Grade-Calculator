import java.sql.*;
import java.util.Scanner;

public class GraderManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String DB_USER = "use_your_username";
    private static final String DB_PASSWORD = "use_your_password";  // Change this!

    private Connection connection;
    private Scanner scanner;

    public static void main(String[] args) {
        GraderManager manager = new GraderManager();
        manager.run();
    }

    public GraderManager() {
        scanner = new Scanner(System.in);
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        while (true) {
            System.out.println("\n=== Student Grade Management System ===");
            System.out.println("1. Student Operations");
            System.out.println("2. Course Operations");
            System.out.println("3. Enrollment Operations");
            System.out.println("4. Display Options");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: studentOperations(); break;
                case 2: courseOperations(); break;
                case 3: enrollmentOperations(); break;
                case 4: displayOptions(); break;
                case 5:
                    try {
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        System.err.println("Error closing connection: " + e.getMessage());
                    }
                    System.exit(0);
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private void studentOperations() {
        System.out.println("\n=== Student Operations ===");
        System.out.println("1. Add New Student");
        System.out.println("2. View All Students");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: addStudent(); break;
            case 2: displayAllStudents(); break;
            case 3: return;
            default: System.out.println("Invalid choice!");
        }
    }

    private void courseOperations() {
        System.out.println("\n=== Course Operations ===");
        System.out.println("1. Add New Course");
        System.out.println("2. View All Courses");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: addCourse(); break;
            case 2: displayAllCourses(); break;
            case 3: return;
            default: System.out.println("Invalid choice!");
        }
    }

    private void enrollmentOperations() {
        System.out.println("\n=== Enrollment Operations ===");
        System.out.println("1. Enroll Student in Course");
        System.out.println("2. Update Grade/Status");
        System.out.println("3. View All Enrollments");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: enrollStudent(); break;
            case 2: updateGrade(); break;
            case 3: displayAllEnrollments(); break;
            case 4: return;
            default: System.out.println("Invalid choice!");
        }
    }

    private void displayOptions() {
        System.out.println("\n=== Display Options ===");
        System.out.println("1. View Student Report");
        System.out.println("2. View Course Report");
        System.out.println("3. View Enrollment Statistics");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: viewStudentReport(); break;
            case 2: viewCourseReport(); break;
            case 3: viewEnrollmentStatistics(); break;
            case 4: return;
            default: System.out.println("Invalid choice!");
        }
    }

    private void addStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student email: ");
        String email = scanner.nextLine();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO students (name, email) VALUES (?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
            System.out.println("Student added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
        }
    }

    private void addCourse() {
        System.out.print("Enter course name: ");
        String name = scanner.nextLine();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO courses (course_name) VALUES (?)");
            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("Course added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    private void enrollStudent() {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter course ID: ");
        int courseId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, 'enrolled')");
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
            System.out.println("Student enrolled successfully!");
        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
        }
    }

    private void updateGrade() {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        System.out.print("Enter course ID: ");
        int courseId = scanner.nextInt();
        System.out.print("Enter grade (0-100) or -1 to skip: ");
        double grade = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        System.out.print("Update status (enrolled/completed) or 'skip' to keep current: ");
        String status = scanner.nextLine();

        try {
            StringBuilder sql = new StringBuilder("UPDATE enrollments SET ");
            boolean needsComma = false;

            if (grade != -1) {
                sql.append("grade = ?");
                needsComma = true;
            }

            if (!status.equalsIgnoreCase("skip")) {
                if (needsComma) sql.append(", ");
                sql.append("status = ?");
            }

            sql.append(" WHERE student_id = ? AND course_id = ?");

            PreparedStatement stmt = connection.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (grade != -1) {
                stmt.setDouble(paramIndex++, grade);
            }
            if (!status.equalsIgnoreCase("skip")) {
                stmt.setString(paramIndex++, status);
            }
            stmt.setInt(paramIndex++, studentId);
            stmt.setInt(paramIndex++, courseId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Record updated successfully!");
            } else {
                System.out.println("No matching enrollment found!");
            }
        } catch (SQLException e) {
            System.err.println("Error updating grade/status: " + e.getMessage());
        }
    }

    private void displayAllStudents() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY name");

            System.out.println("\n=== All Students ===");
            System.out.printf("%-10s %-30s %-30s\n", "ID", "Name", "Email");
            System.out.println("------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10d %-30s %-30s\n",
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students: " + e.getMessage());
        }
    }

    private void displayAllCourses() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM courses ORDER BY course_name");

            System.out.println("\n=== All Courses ===");
            System.out.printf("%-10s %-30s\n", "ID", "Course Name");
            System.out.println("--------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10d %-30s\n",
                        rs.getInt("course_id"),
                        rs.getString("course_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving courses: " + e.getMessage());
        }
    }

    private void displayAllEnrollments() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT e.enrollment_id, s.name AS student_name, c.course_name, e.status, e.grade " +
                            "FROM enrollments e " +
                            "JOIN students s ON e.student_id = s.student_id " +
                            "JOIN courses c ON e.course_id = c.course_id " +
                            "ORDER BY s.name, c.course_name");

            System.out.println("\n=== All Enrollments ===");
            System.out.printf("%-5s %-25s %-25s %-15s %-10s\n",
                    "ID", "Student", "Course", "Status", "Grade");
            System.out.println("------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-25s %-25s %-15s %-10s\n",
                        rs.getInt("enrollment_id"),
                        rs.getString("student_name"),
                        rs.getString("course_name"),
                        rs.getString("status"),
                        rs.getObject("grade") == null ? "N/A" : String.format("%.2f", rs.getDouble("grade")));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving enrollments: " + e.getMessage());
        }
    }

    private void viewStudentReport() {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            // Get student info
            PreparedStatement studentStmt = connection.prepareStatement(
                    "SELECT name, email FROM students WHERE student_id = ?");
            studentStmt.setInt(1, studentId);
            ResultSet studentRs = studentStmt.executeQuery();

            if (!studentRs.next()) {
                System.out.println("Student not found!");
                return;
            }

            String name = studentRs.getString("name");
            String email = studentRs.getString("email");

            System.out.println("\n=== Student Report ===");
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("\nCourse Enrollments:");

            // Get enrollments
            PreparedStatement enrollStmt = connection.prepareStatement(
                    "SELECT c.course_name, e.status, e.grade " +
                            "FROM enrollments e JOIN courses c ON e.course_id = c.course_id " +
                            "WHERE e.student_id = ?");
            enrollStmt.setInt(1, studentId);
            ResultSet enrollRs = enrollStmt.executeQuery();

            int completedCourses = 0;
            double totalGradePoints = 0;
            boolean hasCompletedCourses = false;

            System.out.printf("%-25s %-15s %-10s\n", "Course", "Status", "Grade");
            System.out.println("---------------------------------------------");

            while (enrollRs.next()) {
                String courseName = enrollRs.getString("course_name");
                String status = enrollRs.getString("status");
                Double grade = enrollRs.getObject("grade") == null ? null : enrollRs.getDouble("grade");

                System.out.printf("%-25s %-15s %-10s\n",
                        courseName,
                        status,
                        grade == null ? "N/A" : String.format("%.2f", grade));

                if ("completed".equals(status) && grade != null) {
                    double gradePoint = convertToGradePoint(grade);
                    totalGradePoints += gradePoint;
                    completedCourses++;
                    hasCompletedCourses = true;
                }
            }

            if (hasCompletedCourses) {
                double gpa = totalGradePoints / completedCourses;
                System.out.printf("\nGPA (based on completed courses): %.2f\n", gpa);
            } else {
                System.out.println("\nNo completed courses with grades.");
            }

        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private void viewCourseReport() {
        System.out.print("Enter course ID: ");
        int courseId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            // Get course info
            PreparedStatement courseStmt = connection.prepareStatement(
                    "SELECT course_name FROM courses WHERE course_id = ?");
            courseStmt.setInt(1, courseId);
            ResultSet courseRs = courseStmt.executeQuery();

            if (!courseRs.next()) {
                System.out.println("Course not found!");
                return;
            }

            String courseName = courseRs.getString("course_name");

            System.out.println("\n=== Course Report ===");
            System.out.println("Course: " + courseName);
            System.out.println("\nEnrolled Students:");

            // Get enrollments for this course
            PreparedStatement enrollStmt = connection.prepareStatement(
                    "SELECT s.name, e.status, e.grade " +
                            "FROM enrollments e JOIN students s ON e.student_id = s.student_id " +
                            "WHERE e.course_id = ? ORDER BY s.name");
            enrollStmt.setInt(1, courseId);
            ResultSet enrollRs = enrollStmt.executeQuery();

            int enrolledCount = 0;
            int completedCount = 0;
            double totalGrade = 0;

            System.out.printf("%-25s %-15s %-10s\n", "Student", "Status", "Grade");
            System.out.println("---------------------------------------------");

            while (enrollRs.next()) {
                enrolledCount++;
                String status = enrollRs.getString("status");
                Double grade = enrollRs.getObject("grade") == null ? null : enrollRs.getDouble("grade");

                System.out.printf("%-25s %-15s %-10s\n",
                        enrollRs.getString("name"),
                        status,
                        grade == null ? "N/A" : String.format("%.2f", grade));

                if ("completed".equals(status) && grade != null) {
                    completedCount++;
                    totalGrade += grade;
                }
            }

            System.out.println("\nStatistics:");
            System.out.println("Total Enrolled: " + enrolledCount);
            System.out.println("Completed: " + completedCount);

            if (completedCount > 0) {
                System.out.printf("Average Grade: %.2f\n", totalGrade / completedCount);
            }

        } catch (SQLException e) {
            System.err.println("Error generating course report: " + e.getMessage());
        }
    }

    private void viewEnrollmentStatistics() {
        try {
            System.out.println("\n=== Enrollment Statistics ===");

            // Total students
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM students");
            rs.next();
            System.out.println("Total Students: " + rs.getInt("total"));

            // Total courses
            rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM courses");
            rs.next();
            System.out.println("Total Courses: " + rs.getInt("total"));

            // Enrollment counts
            rs = stmt.executeQuery(
                    "SELECT status, COUNT(*) AS count FROM enrollments GROUP BY status");

            System.out.println("\nEnrollment Status Counts:");
            while (rs.next()) {
                System.out.println(rs.getString("status") + ": " + rs.getInt("count"));
            }

            // Courses with most enrollments
            rs = stmt.executeQuery(
                    "SELECT c.course_name, COUNT(*) AS enrollments " +
                            "FROM enrollments e JOIN courses c ON e.course_id = c.course_id " +
                            "GROUP BY c.course_name ORDER BY enrollments DESC LIMIT 5");

            System.out.println("\nTop 5 Popular Courses:");
            while (rs.next()) {
                System.out.printf("%-30s: %d enrollments\n",
                        rs.getString("course_name"), rs.getInt("enrollments"));
            }

        } catch (SQLException e) {
            System.err.println("Error generating statistics: " + e.getMessage());
        }
    }

    private double convertToGradePoint(double grade) {
        if (grade >= 90) return 4.0;
        if (grade >= 80) return 3.0;
        if (grade >= 70) return 2.0;
        if (grade >= 60) return 1.0;
        return 0.0;
    }

}  // This ends the GradeManager class