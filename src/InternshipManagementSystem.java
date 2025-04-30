import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InternshipManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static List<Student> students = new ArrayList<>();
    private static List<Supervisor> supervisors = new ArrayList<>();
    private static List<Company> companies = new ArrayList<>();
    private static List<Internship> internships = new ArrayList<>();

    public static void main(String[] args) {
        addDemoData();
        boolean running = true;
        System.out.println("=== Internship Management System ===");

        while (running) {
            displayMainMenu();
            int choice = ValidationUtil.getValidInt("Enter your choice: ", 1, 9);

            switch (choice) {
                case 1:
                    registerStudent();
                    break;
                case 2:
                    registerSupervisor();
                    break;
                case 3:
                    registerCompany();
                    break;
                case 4:
                    createInternship();
                    break;
                case 5:
                    viewAllInternships();
                    break;
                case 6:
                    addProgressNote();
                    break;
                case 7:
                    displayInternshipSummary();
                    break;
                case 8:
                    exportInternshipList();
                    break;
                case 9:
                    running = false;
                    System.out.println("Thank you for using the Internship Management System!");
                    break;
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Register Student");
        System.out.println("2. Register Supervisor");
        System.out.println("3. Register Company");
        System.out.println("4. Create Internship");
        System.out.println("5. View All Internships");
        System.out.println("6. Add Progress Note");
        System.out.println("7. Display Internship Summary");
        System.out.println("8. Export Internship List");
        System.out.println("9. Exit");
    }

    private static void registerStudent() {
        System.out.println("\n=== Student Registration ===");

        try {
            Student student = new Student();
            students.add(student);
            System.out.println("Student registered successfully: " + student);
        } catch (IllegalArgumentException e) {
            System.out.println("Error registering student: " + e.getMessage());
        }
    }

    private static void registerSupervisor() {
        System.out.println("\n=== Supervisor Registration ===");

        try {
            Supervisor supervisor = new Supervisor();
            supervisors.add(supervisor);
            System.out.println("Supervisor registered successfully: " + supervisor);
        } catch (IllegalArgumentException e) {
            System.out.println("Error registering supervisor: " + e.getMessage());
        }
    }

    private static void registerCompany() {
        System.out.println("\n=== Company Registration ===");

        try {
            Company company = new Company();
            companies.add(company);
            System.out.println("Company registered successfully: " + company);
        } catch (IllegalArgumentException e) {
            System.out.println("Error registering company: " + e.getMessage());
        }
    }

    private static void createInternship() {
        System.out.println("\n=== Internship Creation ===");

        if (students.isEmpty() || supervisors.isEmpty() || companies.isEmpty()) {
            System.out.println("Please register students, supervisors, and companies first.");
            return;
        }

        System.out.println("\nAvailable Students:");
        for (int i = 0; i < students.size(); i++) {
            System.out.println((i+1) + ". " + students.get(i).getFullName());
        }

        int studentIndex = ValidationUtil.getValidInt("Select student (number): ", 1, students.size()) - 1;
        Student selectedStudent = students.get(studentIndex);

        boolean hasActiveInternship = internships.stream()
                .anyMatch(internship ->
                        internship.getStudent().getStudentId().equals(selectedStudent.getStudentId())
                                && (internship.getStatus().equals("PENDING") || internship.getStatus().equals("ONGOING")));

        if (hasActiveInternship) {
            System.out.println("Error: This student already has an active internship.");
            return;
        }

        System.out.println("\nAvailable Companies:");
        for (int i = 0; i < companies.size(); i++) {
            System.out.println((i+1) + ". " + companies.get(i).getName());
        }

        int companyIndex = ValidationUtil.getValidInt("Select company (number): ", 1, companies.size()) - 1;
        Company selectedCompany = companies.get(companyIndex);

        System.out.println("\nAvailable Supervisors:");
        for (int i = 0; i < supervisors.size(); i++) {
            System.out.println((i+1) + ". " + supervisors.get(i).getFullName());
        }

        int supervisorIndex = ValidationUtil.getValidInt("Select supervisor (number): ", 1, supervisors.size()) - 1;
        Supervisor selectedSupervisor = supervisors.get(supervisorIndex);

        LocalDate startDate = ValidationUtil.getValidDate("Enter start date");
        LocalDate endDate = ValidationUtil.getValidDate("Enter end date");

        if (endDate.isBefore(startDate)) {
            System.out.println("Error: End date cannot be before start date.");
            return;
        }

        long weeks = ChronoUnit.WEEKS.between(startDate, endDate);
        if (weeks < 4) {
            System.out.println("Error: Internship must be at least 4 weeks long.");
            return;
        }

        String internshipId = "INT" + (internships.size() + 1);
        String internshipType = ValidationUtil.getValidChoice("Select internship type:", "Standard", "Remote");

        Internship internship = null;
        try {
            if (internshipType.equals("Standard")) {
                internship = new StandardInternship(internshipId, selectedStudent, selectedCompany.getName(),
                        selectedSupervisor, startDate, endDate);
            } else {
                String platform = ValidationUtil.getValidChoice("Select communication platform:",
                        "Zoom", "Teams", "Slack");
                internship = new RemoteInternship(internshipId, selectedStudent, selectedCompany.getName(),
                        selectedSupervisor, startDate, endDate, platform);
            }

            internships.add(internship);
            System.out.println("Internship created successfully:");
            System.out.println(internship);

        } catch (IllegalArgumentException e) {
            System.out.println("Error creating internship: " + e.getMessage());
        }
    }

    private static void viewAllInternships() {
        System.out.println("\n=== All Internships ===");

        if (internships.isEmpty()) {
            System.out.println("No internships created yet.");
            return;
        }

        for (int i = 0; i < internships.size(); i++) {
            System.out.println((i+1) + ". " + internships.get(i));
            System.out.println("-------------------");
        }

        boolean updateStatus = ValidationUtil.getYesNoInput("Would you like to update an internship status?");
        if (updateStatus) {
            int index = ValidationUtil.getValidInt("Select internship number to update: ", 1, internships.size()) - 1;
            String newStatus = ValidationUtil.getValidChoice("Select new status:", "PENDING", "ONGOING", "COMPLETED");
            internships.get(index).updateStatus(newStatus);
            System.out.println("Status updated successfully.");
        }
    }

    private static void addProgressNote() {
        System.out.println("\n=== Add Progress Note ===");

        if (internships.isEmpty()) {
            System.out.println("No internships available to add progress notes.");
            return;
        }

        System.out.println("\nAvailable Internships:");
        for (int i = 0; i < internships.size(); i++) {
            System.out.println((i+1) + ". " + internships.get(i));
        }

        int index = ValidationUtil.getValidInt("Select internship number: ", 1, internships.size()) - 1;
        Internship selectedInternship = internships.get(index);

        System.out.println("Enter progress note:");
        String note = scanner.nextLine().trim();
        if (!note.isEmpty()) {
            selectedInternship.addProgressNote(note);
            System.out.println("Progress note added successfully.");
        } else {
            System.out.println("Error: Progress note cannot be empty.");
        }
    }

    private static void displayInternshipSummary() {
        System.out.println("\n=== Internship Summary ===");

        if (internships.isEmpty()) {
            System.out.println("No internships available.");
            return;
        }

        long totalInternships = internships.size();
        long completedInternships = internships.stream()
                .filter(internship -> internship.getStatus().equals("COMPLETED"))
                .count();
        long activeInternships = internships.stream()
                .filter(internship -> internship.getStatus().equals("PENDING") ||
                        internship.getStatus().equals("ONGOING"))
                .count();

        System.out.println("Total Internships: " + totalInternships);
        System.out.println("Completed Internships: " + completedInternships);
        System.out.println("Active Internships: " + activeInternships);
    }

    private static void exportInternshipList() {
        System.out.println("\n=== Internship List Export ===");

        if (internships.isEmpty()) {
            System.out.println("No internships available to export.");
            return;
        }

        System.out.println("Generating internship list report:");
        System.out.println("=================================");
        System.out.println("Internship Management System Report");
        System.out.println("Generated on: " + LocalDate.now());
        System.out.println("=================================");

        for (Internship internship : internships) {
            System.out.println(internship.generateReport());
            System.out.println("-------------------");
        }

        System.out.println("=================================");
        System.out.println("Report generated successfully.");
    }

    private static void addDemoData() {
        students.add(new Student("S001", "John Doe", "General", "john.doe@example.com"));
        students.add(new Student("S002", "Jane Smith", "General", "jane.smith@example.com"));

        supervisors.add(new Supervisor("SUP001", "Dr. Wilson", "PhD", "wilson@example.com"));
        supervisors.add(new Supervisor("SUP002", "Prof. Taylor", "Masters", "taylor@example.com"));

        companies.add(new Company("C001", "Tech Corp", "IT", "City"));
        companies.add(new Company("C002", "Finance Inc", "Finance", "Town"));
    }
}

class ValidationUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static String getValidString(String prompt) {
        System.out.print(prompt + " ");
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) return input;
        System.out.println("Error: Input cannot be empty.");
        return getValidString(prompt);
    }

    public static int getValidInt(String prompt, int min, int max) {
        System.out.print(prompt + " ");
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            if (value >= min && value <= max) return value;
            System.out.println("Error: Please enter a number between " + min + " and " + max + ".");
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid integer.");
        }
        return getValidInt(prompt, min, max);
    }

    public static String getValidEmail(String prompt) {
        System.out.print(prompt + " ");
        String email = scanner.nextLine().trim();
        if (email.contains("@")) return email;
        System.out.println("Error: Invalid email format.");
        return getValidEmail(prompt);
    }

    public static String getValidChoice(String prompt, String... options) {
        System.out.print(prompt + " [" + String.join(", ", options) + "]: ");
        String input = scanner.nextLine().trim();
        for (String option : options) {
            if (option.equalsIgnoreCase(input)) return option;
        }
        System.out.println("Error: Invalid choice.");
        return getValidChoice(prompt, options);
    }

    public static LocalDate getValidDate(String prompt) {
        System.out.print(prompt + " (YYYY-MM-DD): ");
        try {
            return LocalDate.parse(scanner.nextLine().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid date format.");
            return getValidDate(prompt);
        }
    }

    public static boolean getYesNoInput(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("y")) return true;
        if (input.equals("n")) return false;
        System.out.println("Error: Please enter 'y' or 'n'.");
        return getYesNoInput(prompt);
    }
}

class Student {
    private String studentId;
    private String fullName;
    private String university;
    private String email;

    public Student(String studentId, String fullName, String university, String email) {
        setStudentId(studentId);
        setFullName(fullName);
        setUniversity(university);
        setEmail(email);
    }

    public Student() {
        this.studentId = ValidationUtil.getValidString("Enter student ID:");
        this.fullName = ValidationUtil.getValidString("Enter full name:");
        this.university = ValidationUtil.getValidString("Enter university:");
        this.email = ValidationUtil.getValidEmail("Enter email:");
    }

    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getUniversity() { return university; }
    public String getEmail() { return email; }

    public void setStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty())
            throw new IllegalArgumentException("Student ID cannot be empty");
        this.studentId = studentId;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty())
            throw new IllegalArgumentException("Full name cannot be empty");
        this.fullName = fullName;
    }

    public void setUniversity(String university) {
        if (university == null || university.trim().isEmpty())
            throw new IllegalArgumentException("University cannot be empty");
        this.university = university;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email");
        this.email = email;
    }

    @Override
    public String toString() {
        return "Student{ID=" + studentId + ", Name=" + fullName + ", University=" + university + "}";
    }
}

class Supervisor {
    private String supervisorId;
    private String fullName;
    private String qualification;
    private String email;

    public Supervisor(String supervisorId, String fullName, String qualification, String email) {
        setSupervisorId(supervisorId);
        setFullName(fullName);
        setQualification(qualification);
        setEmail(email);
    }

    public Supervisor() {
        this.supervisorId = ValidationUtil.getValidString("Enter supervisor ID:");
        this.fullName = ValidationUtil.getValidString("Enter full name:");
        this.qualification = ValidationUtil.getValidChoice("Enter qualification:",
                "Bachelors", "Masters", "PhD");
        this.email = ValidationUtil.getValidEmail("Enter email:");
    }

    public String getSupervisorId() { return supervisorId; }
    public String getFullName() { return fullName; }
    public String getQualification() { return qualification; }
    public String getEmail() { return email; }

    public void setSupervisorId(String supervisorId) {
        if (supervisorId == null || supervisorId.trim().isEmpty())
            throw new IllegalArgumentException("Supervisor ID cannot be empty");
        this.supervisorId = supervisorId;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty())
            throw new IllegalArgumentException("Full name cannot be empty");
        this.fullName = fullName;
    }

    public void setQualification(String qualification) {
        if (qualification == null || qualification.trim().isEmpty())
            throw new IllegalArgumentException("Qualification cannot be empty");
        this.qualification = qualification;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email");
        this.email = email;
    }

    @Override
    public String toString() {
        return "Supervisor{ID=" + supervisorId + ", Name=" + fullName + ", Qual=" + qualification + "}";
    }
}

class Company {
    private String companyId;
    private String name;
    private String industryType;
    private String location;

    public Company(String companyId, String name, String industryType, String location) {
        setCompanyId(companyId);
        setName(name);
        setIndustryType(industryType);
        setLocation(location);
    }

    public Company() {
        this.companyId = ValidationUtil.getValidString("Enter company ID:");
        this.name = ValidationUtil.getValidString("Enter company name:");
        this.industryType = ValidationUtil.getValidChoice("Enter industry type:",
                "IT", "Finance", "Other");
        this.location = ValidationUtil.getValidString("Enter location:");
    }

    public String getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getIndustryType() { return industryType; }
    public String getLocation() { return location; }

    public void setCompanyId(String companyId) {
        if (companyId == null || companyId.trim().isEmpty())
            throw new IllegalArgumentException("Company ID cannot be empty");
        this.companyId = companyId;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Company name cannot be empty");
        this.name = name;
    }

    public void setIndustryType(String industryType) {
        if (industryType == null || industryType.trim().isEmpty())
            throw new IllegalArgumentException("Industry type cannot be empty");
        this.industryType = industryType;
    }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty())
            throw new IllegalArgumentException("Location cannot be empty");
        this.location = location;
    }

    @Override
    public String toString() {
        return "Company{ID=" + companyId + ", Name=" + name + ", Industry=" + industryType + "}";
    }
}

abstract class Internship {
    private String internshipId;
    private Student student;
    private String companyName;
    private Supervisor supervisor;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private List<String> progressNotes;

    public Internship(String internshipId, Student student, String companyName,
                      Supervisor supervisor, LocalDate startDate, LocalDate endDate) {
        setInternshipId(internshipId);
        setStudent(student);
        setCompanyName(companyName);
        setSupervisor(supervisor);
        setStartDate(startDate);
        setEndDate(endDate);
        this.status = "PENDING";
        this.progressNotes = new ArrayList<>();
        validateInternship();
    }

    public abstract void trackProgress();
    public abstract String generateReport();

    protected void validateInternship() {
        if (startDate.isAfter(endDate))
            throw new IllegalArgumentException("Start date must be before end date");
        if (ChronoUnit.WEEKS.between(startDate, endDate) < 4)
            throw new IllegalArgumentException("Internship must be at least 4 weeks");
    }

    public void addProgressNote(String note) {
        if (note != null && !note.trim().isEmpty())
            progressNotes.add(note);
    }

    public void updateStatus(String status) {
        if (status != null && (status.equals("PENDING") || status.equals("ONGOING") || status.equals("COMPLETED")))
            this.status = status;
        else
            throw new IllegalArgumentException("Invalid status");
    }

    protected String getBaseReport() {
        StringBuilder report = new StringBuilder();
        report.append("Internship Report\n");
        report.append("ID: ").append(internshipId).append("\n");
        report.append("Student: ").append(student.getFullName()).append("\n");
        report.append("Company: ").append(companyName).append("\n");
        report.append("Supervisor: ").append(supervisor.getFullName()).append("\n");
        report.append("Duration: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Status: ").append(status).append("\n");
        report.append("Notes:\n").append(progressNotes.isEmpty() ? "None\n" : String.join("\n", progressNotes));
        return report.toString();
    }

    public String getInternshipId() { return internshipId; }
    public Student getStudent() { return student; }
    public String getCompanyName() { return companyName; }
    public Supervisor getSupervisor() { return supervisor; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public List<String> getProgressNotes() { return progressNotes; }

    public void setInternshipId(String internshipId) {
        if (internshipId == null || internshipId.trim().isEmpty())
            throw new IllegalArgumentException("Internship ID cannot be empty");
        this.internshipId = internshipId;
    }

    public void setStudent(Student student) {
        if (student == null)
            throw new IllegalArgumentException("Student cannot be null");
        this.student = student;
    }

    public void setCompanyName(String companyName) {
        if (companyName == null || companyName.trim().isEmpty())
            throw new IllegalArgumentException("Company name cannot be empty");
        this.companyName = companyName;
    }

    public void setSupervisor(Supervisor supervisor) {
        if (supervisor == null)
            throw new IllegalArgumentException("Supervisor cannot be null");
        this.supervisor = supervisor;
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate == null)
            throw new IllegalArgumentException("Start date cannot be null");
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate == null)
            throw new IllegalArgumentException("End date cannot be null");
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Internship{ID=" + internshipId + ", Student=" + student.getFullName() +
                ", Company=" + companyName + ", Status=" + status + "}";
    }
}

class StandardInternship extends Internship {
    public StandardInternship(String internshipId, Student student, String companyName,
                              Supervisor supervisor, LocalDate startDate, LocalDate endDate) {
        super(internshipId, student, companyName, supervisor, startDate, endDate);
    }

    @Override
    public void trackProgress() {
        System.out.println("Enter progress note for internship " + getInternshipId() + ":");
        String note = new Scanner(System.in).nextLine();
        addProgressNote(note);
        System.out.println("Progress note added.");
    }

    @Override
    public String generateReport() {
        return getBaseReport() + "\nType: Standard Internship";
    }

    @Override
    public String toString() {
        return super.toString() + "\nType: Standard";
    }
}

class RemoteInternship extends Internship {
    private String communicationPlatform;

    public RemoteInternship(String internshipId, Student student, String companyName,
                            Supervisor supervisor, LocalDate startDate, LocalDate endDate,
                            String communicationPlatform) {
        super(internshipId, student, companyName, supervisor, startDate, endDate);
        this.communicationPlatform = communicationPlatform;
    }

    @Override
    public void trackProgress() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter progress note for remote internship " + getInternshipId() + ":");
        String note = scanner.nextLine();
        addProgressNote(note + " (via " + communicationPlatform + ")");
        System.out.println("Progress note added.");
    }

    @Override
    public String generateReport() {
        return getBaseReport() + "\nType: Remote Internship\nPlatform: " + communicationPlatform;
    }

    public String getCommunicationPlatform() { return communicationPlatform; }

    @Override
    public String toString() {
        return super.toString() + "\nType: Remote\nPlatform: " + communicationPlatform;
    }
}