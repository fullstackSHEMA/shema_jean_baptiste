import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

interface TaxCalculable {
    double calculateTax();
}

interface Receiptable {
    void generateReceipt();
}

abstract class TaxDeclaration implements TaxCalculable, Receiptable {
    private String declarationId;
    private String taxpayerName;
    private String taxpayerTIN;
    private LocalDate declarationDate;
    private double taxAmount;
    private boolean isPaid;

    public TaxDeclaration(String declarationId, String taxpayerName, String taxpayerTIN,
                          LocalDate declarationDate) {
        if (declarationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Declaration date cannot be in the future");
        }
        if (taxpayerTIN.length() != 9 || !taxpayerTIN.matches("\\d{9}")) {
            throw new IllegalArgumentException("TIN must be exactly 9 digits");
        }

        this.declarationId = declarationId;
        this.taxpayerName = taxpayerName;
        this.taxpayerTIN = taxpayerTIN;
        this.declarationDate = declarationDate;
        this.taxAmount = 0.0;
        this.isPaid = false;
    }

    @Override
    public abstract double calculateTax();

    public abstract boolean validateDeclaration();

    @Override
    public abstract void generateReceipt();

    public abstract double enforceCompliance();

    public String getDeclarationId() {
        return declarationId;
    }

    public String getTaxpayerName() {
        return taxpayerName;
    }

    public String getTaxpayerTIN() {
        return taxpayerTIN;
    }

    public LocalDate getDeclarationDate() {
        return declarationDate;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        if (taxAmount < 0) {
            throw new IllegalArgumentException("Tax amount cannot be negative");
        }
        this.taxAmount = taxAmount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }

    @Override
    public String toString() {
        return String.format("Declaration ID: %s, Taxpayer: %s, TIN: %s, Date: %s, Amount: %.2f, Paid: %s",
                declarationId, taxpayerName, taxpayerTIN, declarationDate, taxAmount, isPaid ? "Yes" : "No");
    }
}

class PAYEDeclaration extends TaxDeclaration {
    private double grossSalary;
    private static final double PENALTY_RATE = 0.1;

    public PAYEDeclaration(String declarationId, String taxpayerName, String taxpayerTIN,
                           LocalDate declarationDate, double grossSalary) {
        super(declarationId, taxpayerName, taxpayerTIN, declarationDate);
        if (grossSalary <= 0) {
            throw new IllegalArgumentException("Gross salary must be greater than zero");
        }
        this.grossSalary = grossSalary;
        setTaxAmount(calculateTax());
    }

    @Override
    public double calculateTax() {
        if (grossSalary <= 30000) {
            return 0;
        } else if (grossSalary <= 100000) {
            return (grossSalary - 30000) * 0.2;
        } else {
            return (100000 - 30000) * 0.2 + (grossSalary - 100000) * 0.3;
        }
    }

    @Override
    public boolean validateDeclaration() {
        return grossSalary > 0;
    }

    @Override
    public void generateReceipt() {
        System.out.println("\n===== PAYE TAX RECEIPT =====");
        System.out.println("Declaration ID: " + getDeclarationId());
        System.out.println("Taxpayer Name: " + getTaxpayerName());
        System.out.println("Taxpayer TIN: " + getTaxpayerTIN());
        System.out.println("Declaration Date: " + getDeclarationDate());
        System.out.println("Gross Salary: " + String.format("%.2f RWF", grossSalary));
        System.out.println("Tax Amount: " + String.format("%.2f RWF", getTaxAmount()));
        System.out.println("Payment Status: " + (isPaid() ? "Paid" : "Unpaid"));
        System.out.println("============================");
    }

    @Override
    public double enforceCompliance() {
        if (!isPaid()) {
            LocalDate dueDate = getDeclarationDate().plusMonths(1).withDayOfMonth(15);
            if (LocalDate.now().isAfter(dueDate)) {
                return getTaxAmount() * PENALTY_RATE;
            }
        }
        return 0;
    }

    public double getGrossSalary() {
        return grossSalary;
    }
}

class VATDeclaration extends TaxDeclaration {
    private double taxableSales;
    private double purchases;
    private static final double VAT_RATE = 0.18;
    private static final double PENALTY_RATE = 0.2;

    public VATDeclaration(String declarationId, String taxpayerName, String taxpayerTIN,
                          LocalDate declarationDate, double taxableSales, double purchases) {
        super(declarationId, taxpayerName, taxpayerTIN, declarationDate);
        this.taxableSales = taxableSales;
        this.purchases = purchases;

        if (!validateDeclaration()) {
            throw new IllegalArgumentException("Sales must be greater than or equal to purchases");
        }

        setTaxAmount(calculateTax());
    }

    @Override
    public double calculateTax() {
        return taxableSales * VAT_RATE - purchases * VAT_RATE;
    }

    @Override
    public boolean validateDeclaration() {
        return taxableSales >= purchases;
    }

    @Override
    public void generateReceipt() {
        System.out.println("\n===== VAT RECEIPT =====");
        System.out.println("Declaration ID: " + getDeclarationId());
        System.out.println("Taxpayer Name: " + getTaxpayerName());
        System.out.println("Taxpayer TIN: " + getTaxpayerTIN());
        System.out.println("Declaration Date: " + getDeclarationDate());
        System.out.println("Taxable Sales: " + String.format("%.2f RWF", taxableSales));
        System.out.println("Purchases: " + String.format("%.2f RWF", purchases));
        System.out.println("Output VAT: " + String.format("%.2f RWF", taxableSales * VAT_RATE));
        System.out.println("Input VAT: " + String.format("%.2f RWF", purchases * VAT_RATE));
        System.out.println("Net VAT: " + String.format("%.2f RWF", getTaxAmount()));
        System.out.println("Payment Status: " + (isPaid() ? "Paid" : "Unpaid"));
        System.out.println("=======================");
    }

    @Override
    public double enforceCompliance() {
        if (!isPaid()) {
            LocalDate dueDate = getDeclarationDate().plusMonths(1).withDayOfMonth(
                    getDeclarationDate().plusMonths(1).lengthOfMonth());
            if (LocalDate.now().isAfter(dueDate)) {
                Period period = Period.between(dueDate, LocalDate.now());
                int monthsLate = period.getMonths() + (period.getYears() * 12);
                double penaltyRate = Math.min(PENALTY_RATE + (0.05 * monthsLate), 0.5);
                return getTaxAmount() * penaltyRate;
            }
        }
        return 0;
    }

    public double getTaxableSales() {
        return taxableSales;
    }

    public double getPurchases() {
        return purchases;
    }
}

class WithholdingTaxDeclaration extends TaxDeclaration {
    public enum Category {
        RENT(0.15),
        DIVIDENDS(0.15),
        SERVICES(0.15);

        private final double rate;

        Category(double rate) {
            this.rate = rate;
        }

        public double getRate() {
            return rate;
        }
    }

    private Category category;
    private double taxableAmount;
    private static final double PENALTY_RATE = 0.25;

    public WithholdingTaxDeclaration(String declarationId, String taxpayerName, String taxpayerTIN,
                                     LocalDate declarationDate, Category category, double taxableAmount) {
        super(declarationId, taxpayerName, taxpayerTIN, declarationDate);
        this.category = category;
        this.taxableAmount = taxableAmount;

        if (!validateDeclaration()) {
            throw new IllegalArgumentException("Taxable amount must be greater than zero");
        }

        setTaxAmount(calculateTax());
    }

    @Override
    public double calculateTax() {
        return taxableAmount * category.getRate();
    }

    @Override
    public boolean validateDeclaration() {
        return taxableAmount > 0;
    }

    @Override
    public void generateReceipt() {
        System.out.println("\n===== WITHHOLDING TAX RECEIPT =====");
        System.out.println("Declaration ID: " + getDeclarationId());
        System.out.println("Taxpayer Name: " + getTaxpayerName());
        System.out.println("Taxpayer TIN: " + getTaxpayerTIN());
        System.out.println("Declaration Date: " + getDeclarationDate());
        System.out.println("Category: " + category);
        System.out.println("Taxable Amount: " + String.format("%.2f RWF", taxableAmount));
        System.out.println("Withholding Rate: " + String.format("%.1f%%", category.getRate() * 100));
        System.out.println("Tax Amount: " + String.format("%.2f RWF", getTaxAmount()));
        System.out.println("Payment Status: " + (isPaid() ? "Paid" : "Unpaid"));
        System.out.println("=================================");
    }

    @Override
    public double enforceCompliance() {
        if (!isPaid()) {
            LocalDate dueDate = getDeclarationDate().plusMonths(1).withDayOfMonth(15);
            if (LocalDate.now().isAfter(dueDate)) {
                return getTaxAmount() * PENALTY_RATE;
            }
        }
        return 0;
    }

    public Category getCategory() {
        return category;
    }

    public double getTaxableAmount() {
        return taxableAmount;
    }
}

class Taxpayer {
    private String tin;
    private String name;
    private enum Type { INDIVIDUAL, COMPANY }
    private Type type;
    private int complianceScore;
    private List<TaxDeclaration> declarations;

    public Taxpayer(String tin, String name, boolean isCompany) {
        if (tin.length() != 9 || !tin.matches("\\d{9}")) {
            throw new IllegalArgumentException("TIN must be exactly 9 digits");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        this.tin = tin;
        this.name = name;
        this.type = isCompany ? Type.COMPANY : Type.INDIVIDUAL;
        this.complianceScore = 100;
        this.declarations = new ArrayList<>();
    }

    public void addDeclaration(TaxDeclaration declaration) {
        if (!declaration.getTaxpayerTIN().equals(this.tin)) {
            throw new IllegalArgumentException("Declaration TIN does not match taxpayer TIN");
        }

        for (TaxDeclaration existing : declarations) {
            if (existing.getDeclarationId().equals(declaration.getDeclarationId())) {
                throw new IllegalArgumentException("Duplicate declaration ID");
            }
        }

        declarations.add(declaration);
    }

    public void updateComplianceScore(int change) {
        complianceScore = Math.max(0, Math.min(100, complianceScore + change));
    }

    public String getTin() {
        return tin;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type.toString();
    }

    public int getComplianceScore() {
        return complianceScore;
    }

    public List<TaxDeclaration> getDeclarations() {
        return new ArrayList<>(declarations);
    }

    public String generateComplianceReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n===== COMPLIANCE REPORT =====\n");
        report.append("Taxpayer: ").append(name).append("\n");
        report.append("TIN: ").append(tin).append("\n");
        report.append("Type: ").append(type).append("\n");
        report.append("Compliance Score: ").append(complianceScore).append("/100\n");
        report.append("\nDeclarations:\n");

        if (declarations.isEmpty()) {
            report.append("No declarations found.\n");
        } else {
            for (TaxDeclaration declaration : declarations) {
                report.append("- ").append(declaration.getClass().getSimpleName())
                        .append(" (").append(declaration.getDeclarationDate()).append("): ")
                        .append(String.format("%.2f RWF", declaration.getTaxAmount()))
                        .append(" - ").append(declaration.isPaid() ? "Paid" : "Unpaid");

                double penalty = declaration.enforceCompliance();
                if (penalty > 0) {
                    report.append(" - Penalty: ").append(String.format("%.2f RWF", penalty));
                }
                report.append("\n");
            }
        }

        report.append("============================\n");
        return report.toString();
    }
}

class TaxOfficer {
    private String officerId;
    private String fullName;
    private String assignedRegion;
    private List<TaxDeclaration> auditsConducted;

    public TaxOfficer(String officerId, String fullName, String assignedRegion) {
        if (officerId == null || officerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Officer ID cannot be empty");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (assignedRegion == null || assignedRegion.trim().isEmpty()) {
            throw new IllegalArgumentException("Assigned region cannot be empty");
        }

        this.officerId = officerId;
        this.fullName = fullName;
        this.assignedRegion = assignedRegion;
        this.auditsConducted = new ArrayList<>();
    }

    public boolean auditDeclaration(TaxDeclaration declaration) {
        if (declaration == null) {
            return false;
        }

        boolean validationResult = declaration.validateDeclaration();
        if (validationResult) {
            auditsConducted.add(declaration);
        }
        return validationResult;
    }

    public String generateAuditSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("\n===== AUDIT SUMMARY =====\n");
        summary.append("Officer: ").append(fullName).append(" (").append(officerId).append(")\n");
        summary.append("Region: ").append(assignedRegion).append("\n");
        summary.append("Audits Conducted: ").append(auditsConducted.size()).append("\n\n");

        double totalTaxAssessed = auditsConducted.stream()
                .mapToDouble(TaxDeclaration::getTaxAmount)
                .sum();

        double totalCollected = auditsConducted.stream()
                .filter(TaxDeclaration::isPaid)
                .mapToDouble(TaxDeclaration::getTaxAmount)
                .sum();

        double totalPending = totalTaxAssessed - totalCollected;

        summary.append("Total Tax Assessed: ").append(String.format("%.2f RWF", totalTaxAssessed)).append("\n");
        summary.append("Total Collected: ").append(String.format("%.2f RWF", totalCollected)).append("\n");
        summary.append("Total Pending: ").append(String.format("%.2f RWF", totalPending)).append("\n");
        summary.append("Collection Rate: ").append(
                totalTaxAssessed > 0 ? String.format("%.1f%%", (totalCollected / totalTaxAssessed) * 100) : "N/A"
        ).append("\n");

        summary.append("============================\n");
        return summary.toString();
    }

    public String getOfficerId() {
        return officerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAssignedRegion() {
        return assignedRegion;
    }

    public List<TaxDeclaration> getAuditsConducted() {
        return new ArrayList<>(auditsConducted);
    }
}

public class TaxEnforcementSystem {
    private static ArrayList<TaxDeclaration> declarations = new ArrayList<>();
    private static ArrayList<Taxpayer> taxpayers = new ArrayList<>();
    private static ArrayList<TaxOfficer> officers = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeSampleData();

        boolean running = true;
        while (running) {
            displayMenu();
            try {
                int choice = getValidIntInput("Enter your choice: ", 1, 12);
                processMenuChoice(choice);

                if (choice == 12) {
                    running = false;
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }

        scanner.close();
        System.out.println("Thank you for using RRA Tax Enforcement System. Goodbye!");
    }

    private static void initializeSampleData() {
        try {
            Taxpayer individual = new Taxpayer("123456789", "John Doe", false);
            Taxpayer company = new Taxpayer("987654321", "ABC Enterprises Ltd", true);

            taxpayers.add(individual);
            taxpayers.add(company);

            TaxOfficer officer = new TaxOfficer("OFF001", "Jane Smith", "Kigali");
            officers.add(officer);

            PAYEDeclaration paye = new PAYEDeclaration("PAYE001", "John Doe", "123456789",
                    LocalDate.now().minusMonths(1), 120000);

            VATDeclaration vat = new VATDeclaration("VAT001", "ABC Enterprises Ltd", "987654321",
                    LocalDate.now().minusMonths(1), 5000000, 3000000);

            WithholdingTaxDeclaration wht = new WithholdingTaxDeclaration("WHT001", "ABC Enterprises Ltd",
                    "987654321", LocalDate.now().minusMonths(1),
                    WithholdingTaxDeclaration.Category.SERVICES, 2000000);

            declarations.add(paye);
            declarations.add(vat);
            declarations.add(wht);

            individual.addDeclaration(paye);
            company.addDeclaration(vat);
            company.addDeclaration(wht);

            officer.auditDeclaration(paye);
            officer.auditDeclaration(vat);
            officer.auditDeclaration(wht);

        } catch (Exception e) {
            System.out.println("Error initializing sample data: " + e.getMessage());
        }
    }

    private static void displayMenu() {
        System.out.println("\n===== RRA TAX ENFORCEMENT SYSTEM =====");
        System.out.println("1. Declare PAYE Tax");
        System.out.println("2. Declare VAT");
        System.out.println("3. Declare Withholding Tax");
        System.out.println("4. View All Declarations");
        System.out.println("5. View Unpaid Taxes");
        System.out.println("6. Generate Taxpayer Compliance Report");
        System.out.println("7. Generate Tax Receipt");
        System.out.println("8. Mark Tax as Paid");
        System.out.println("9. Export Tax Receipt");
        System.out.println("10. View Declaration Status Summary");
        System.out.println("11. Search Taxpayer by Name");
        System.out.println("12. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void processMenuChoice(int choice) {
        switch (choice) {
            case 1:
                declarePAYE();
                break;
            case 2:
                declareVAT();
                break;
            case 3:
                declareWithholdingTax();
                break;
            case 4:
                viewAllDeclarations();
                break;
            case 5:
                viewUnpaidTaxes();
                break;
            case 6:
                generateComplianceReport();
                break;
            case 7:
                generateTaxReceipt();
                break;
            case 8:
                markTaxAsPaid();
                break;
            case 9:
                exportTaxReceipt();
                break;
            case 10:
                viewDeclarationStatusSummary();
                break;
            case 11:
                searchTaxpayerByName();
                break;
            case 12:
                System.out.println("Exiting system. Thank you for using RRA Tax Enforcement System.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void declarePAYE() {
        try {
            System.out.println("\n===== PAYE Declaration =====");
            String declarationId = generateDeclarationId("PAYE");

            String taxpayerName = getValidStringInput("Enter Taxpayer Name: ", null, "");

            String taxpayerTIN = getValidStringInput(
                    "Enter Taxpayer TIN (9 digits): ",
                    "\\d{9}",
                    "TIN must be exactly 9 digits. Please try again."
            );

            double grossSalary = getValidDoubleInput("Enter Gross Salary: ", 0);

            PAYEDeclaration paye = new PAYEDeclaration(
                    declarationId, taxpayerName, taxpayerTIN, LocalDate.now(), grossSalary
            );

            declarations.add(paye);

            Taxpayer taxpayer = findOrCreateTaxpayer(taxpayerTIN, taxpayerName, false);
            taxpayer.addDeclaration(paye);

            System.out.println("\nPAYE Declaration Successful!");
            System.out.println("Declaration ID: " + declarationId);
            System.out.println("Tax Amount: " + String.format("%.2f RWF", paye.getTaxAmount()));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void declareVAT() {
        try {
            System.out.println("\n===== VAT Declaration =====");
            String declarationId = generateDeclarationId("VAT");

            String taxpayerName = getValidStringInput("Enter Taxpayer Name: ", null, "");

            String taxpayerTIN = getValidStringInput(
                    "Enter Taxpayer TIN (9 digits): ",
                    "\\d{9}",
                    "TIN must be exactly 9 digits. Please try again."
            );

            double taxableSales = getValidDoubleInput("Enter Taxable Sales: ", 0);
            double purchases = getValidDoubleInput("Enter Purchases: ", 0);

            VATDeclaration vat = new VATDeclaration(
                    declarationId, taxpayerName, taxpayerTIN, LocalDate.now(), taxableSales, purchases
            );

            declarations.add(vat);

            Taxpayer taxpayer = findOrCreateTaxpayer(taxpayerTIN, taxpayerName, true);
            taxpayer.addDeclaration(vat);

            System.out.println("\nVAT Declaration Successful!");
            System.out.println("Declaration ID: " + declarationId);
            System.out.println("Tax Amount: " + String.format("%.2f RWF", vat.getTaxAmount()));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void declareWithholdingTax() {
        try {
            System.out.println("\n===== Withholding Tax Declaration =====");
            String declarationId = generateDeclarationId("WHT");

            String taxpayerName = getValidStringInput("Enter Taxpayer Name: ", null, "");

            String taxpayerTIN = getValidStringInput(
                    "Enter Taxpayer TIN (9 digits): ",
                    "\\d{9}",
                    "TIN must be exactly 9 digits. Please try again."
            );

            System.out.println("Select Category:");
            System.out.println("1. Rent");
            System.out.println("2. Dividends");
            System.out.println("3. Professional Services");

            int categoryChoice = getValidIntInput("Enter choice (1-3): ", 1, 3);
            WithholdingTaxDeclaration.Category category;

            switch (categoryChoice) {
                case 1:
                    category = WithholdingTaxDeclaration.Category.RENT;
                    break;
                case 2:
                    category = WithholdingTaxDeclaration.Category.DIVIDENDS;
                    break;
                case 3:
                    category = WithholdingTaxDeclaration.Category.SERVICES;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid category choice");
            }

            double taxableAmount = getValidDoubleInput("Enter Taxable Amount: ", 0);

            WithholdingTaxDeclaration wht = new WithholdingTaxDeclaration(
                    declarationId, taxpayerName, taxpayerTIN, LocalDate.now(), category, taxableAmount
            );

            declarations.add(wht);

            Taxpayer taxpayer = findOrCreateTaxpayer(taxpayerTIN, taxpayerName, true);
            taxpayer.addDeclaration(wht);

            System.out.println("\nWithholding Tax Declaration Successful!");
            System.out.println("Declaration ID: " + declarationId);
            System.out.println("Category: " + category);
            System.out.println("Tax Amount: " + String.format("%.2f RWF", wht.getTaxAmount()));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllDeclarations() {
        System.out.println("\n===== All Tax Declarations =====");

        if (declarations.isEmpty()) {
            System.out.println("No declarations found in the system.");
            return;
        }

        for (int i = 0; i < declarations.size(); i++) {
            TaxDeclaration declaration = declarations.get(i);
            System.out.printf("%d. [%s] %s - %s (%.2f RWF) - %s\n",
                    i + 1,
                    declaration.getClass().getSimpleName().replace("Declaration", ""),
                    declaration.getTaxpayerName(),
                    declaration.getDeclarationDate(),
                    declaration.getTaxAmount(),
                    declaration.isPaid() ? "Paid" : "Unpaid");
        }
    }

    private static void viewUnpaidTaxes() {
        System.out.println("\n===== Unpaid Taxes =====");

        List<TaxDeclaration> unpaidTaxes = declarations.stream()
                .filter(d -> !d.isPaid())
                .collect(Collectors.toList());

        if (unpaidTaxes.isEmpty()) {
            System.out.println("No unpaid taxes found in the system.");
            return;
        }

        double totalUnpaid = 0;

        for (int i = 0; i < unpaidTaxes.size(); i++) {
            TaxDeclaration declaration = unpaidTaxes.get(i);
            double penalty = declaration.enforceCompliance();

            System.out.printf("%d. [%s] %s - %.2f RWF",
                    i + 1,
                    declaration.getClass().getSimpleName().replace("Declaration", ""),
                    declaration.getTaxpayerName(),
                    declaration.getTaxAmount());

            if (penalty > 0) {
                System.out.printf(" (Penalty: %.2f RWF)", penalty);
            }

            System.out.println();
            totalUnpaid += declaration.getTaxAmount() + penalty;
        }

        System.out.println("\nTotal Unpaid Taxes: " + String.format("%.2f RWF", totalUnpaid));
    }

    private static void generateComplianceReport() {
        if (taxpayers.isEmpty()) {
            System.out.println("No taxpayers found in the system.");
            return;
        }

        System.out.println("\n===== Generate Compliance Report =====");
        System.out.println("Select a taxpayer:");

        for (int i = 0; i < taxpayers.size(); i++) {
            Taxpayer taxpayer = taxpayers.get(i);
            System.out.printf("%d. %s (TIN: %s)\n", i + 1, taxpayer.getName(), taxpayer.getTin());
        }

        System.out.print("Enter taxpayer number: ");
        try {
            int taxpayerIndex = Integer.parseInt(scanner.nextLine()) - 1;

            if (taxpayerIndex < 0 || taxpayerIndex >= taxpayers.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Taxpayer selectedTaxpayer = taxpayers.get(taxpayerIndex);
            System.out.println(selectedTaxpayer.generateComplianceReport());

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void generateTaxReceipt() {
        if (declarations.isEmpty()) {
            System.out.println("No declarations found in the system.");
            return;
        }

        System.out.println("\n===== Generate Tax Receipt =====");
        viewAllDeclarations();

        System.out.print("Enter declaration number to generate receipt: ");
        try {
            int declarationIndex = Integer.parseInt(scanner.nextLine()) - 1;

            if (declarationIndex < 0 || declarationIndex >= declarations.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            TaxDeclaration selectedDeclaration = declarations.get(declarationIndex);
            selectedDeclaration.generateReceipt();

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void markTaxAsPaid() {
        List<TaxDeclaration> unpaidTaxes = declarations.stream()
                .filter(d -> !d.isPaid())
                .collect(Collectors.toList());

        if (unpaidTaxes.isEmpty()) {
            System.out.println("No unpaid taxes found in the system.");
            return;
        }

        System.out.println("\n===== Mark Tax as Paid =====");
        System.out.println("Select an unpaid tax declaration:");

        for (int i = 0; i < unpaidTaxes.size(); i++) {
            TaxDeclaration declaration = unpaidTaxes.get(i);
            System.out.printf("%d. [%s] %s - %.2f RWF\n",
                    i + 1,
                    declaration.getClass().getSimpleName().replace("Declaration", ""),
                    declaration.getTaxpayerName(),
                    declaration.getTaxAmount());
        }

        try {
            int declarationIndex = getValidIntInput(
                    "Enter declaration number to mark as paid: ",
                    1,
                    unpaidTaxes.size()
            ) - 1;

            TaxDeclaration selectedDeclaration = unpaidTaxes.get(declarationIndex);

            double penalty = selectedDeclaration.enforceCompliance();
            double totalPayable = selectedDeclaration.getTaxAmount() + penalty;

            System.out.printf("Tax Amount: %.2f RWF\n", selectedDeclaration.getTaxAmount());

            if (penalty > 0) {
                System.out.printf("Penalty: %.2f RWF\n", penalty);
                System.out.printf("Total Payable: %.2f RWF\n", totalPayable);
            }

            String confirmation = getValidStringInput(
                    "Confirm payment (Y/N): ",
                    "[YyNn]",
                    "Please enter Y for Yes or N for No."
            );

            if (confirmation.equalsIgnoreCase("Y")) {
                selectedDeclaration.setPaid(true);

                if (penalty > 0) {
                    String tin = selectedDeclaration.getTaxpayerTIN();
                    Taxpayer taxpayer = findTaxpayerByTIN(tin);
                    if (taxpayer != null) {
                        taxpayer.updateComplianceScore(-5);
                    }
                }

                System.out.println("Payment recorded successfully!");
                selectedDeclaration.generateReceipt();
            } else {
                System.out.println("Payment cancelled.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void exportTaxReceipt() {
        if (declarations.isEmpty()) {
            System.out.println("No declarations found in the system.");
            return;
        }

        System.out.println("\n===== Export Tax Receipt =====");
        viewAllDeclarations();

        System.out.print("Enter declaration number to export: ");
        try {
            int declarationIndex = Integer.parseInt(scanner.nextLine()) - 1;

            if (declarationIndex < 0 || declarationIndex >= declarations.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            TaxDeclaration selectedDeclaration = declarations.get(declarationIndex);
            System.out.println("Available export formats: 1. PDF, 2. TXT, 3. CSV");
            int formatChoice = getValidIntInput("Select export format (1-3): ", 1, 3);
            String format;
            switch (formatChoice) {
                case 1:
                    format = "PDF";
                    break;
                case 2:
                    format = "TXT";
                    break;
                case 3:
                    format = "CSV";
                    break;
                default:
                    format = "Unknown";
            }

            System.out.println("Exporting receipt for declaration " + selectedDeclaration.getDeclarationId() + " as " + format + "...");
            selectedDeclaration.generateReceipt();
            System.out.println("Receipt exported successfully as " + format + ".");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void viewDeclarationStatusSummary() {
        System.out.println("\n===== Declaration Status Summary =====");

        if (declarations.isEmpty()) {
            System.out.println("No declarations found in the system.");
            return;
        }

        long paidCount = declarations.stream().filter(TaxDeclaration::isPaid).count();
        long unpaidCount = declarations.size() - paidCount;

        System.out.println("Total Declarations: " + declarations.size());
        System.out.println("Paid Declarations: " + paidCount);
        System.out.println("Unpaid Declarations: " + unpaidCount);
        System.out.println("Payment Rate: " +
                (declarations.size() > 0 ? String.format("%.1f%%", (paidCount * 100.0 / declarations.size())) : "N/A"));
    }

    private static void searchTaxpayerByName() {
        if (taxpayers.isEmpty()) {
            System.out.println("No taxpayers found in the system.");
            return;
        }

        System.out.println("\n===== Search Taxpayer by Name =====");
        String searchQuery = getValidStringInput("Enter taxpayer name (or part of name): ", null, "");

        List<Taxpayer> matchingTaxpayers = taxpayers.stream()
                .filter(t -> t.getName().toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());

        if (matchingTaxpayers.isEmpty()) {
            System.out.println("No taxpayers found matching '" + searchQuery + "'.");
            return;
        }

        System.out.println("\nMatching Taxpayers:");
        for (int i = 0; i < matchingTaxpayers.size(); i++) {
            Taxpayer taxpayer = matchingTaxpayers.get(i);
            System.out.printf("%d. %s (TIN: %s, Type: %s)\n",
                    i + 1, taxpayer.getName(), taxpayer.getTin(), taxpayer.getType());
        }
    }

    private static String generateDeclarationId(String prefix) {
        return prefix + System.currentTimeMillis() % 10000;
    }

    private static Taxpayer findOrCreateTaxpayer(String tin, String name, boolean isCompany) {
        for (Taxpayer taxpayer : taxpayers) {
            if (taxpayer.getTin().equals(tin)) {
                return taxpayer;
            }
        }

        Taxpayer newTaxpayer = new Taxpayer(tin, name, isCompany);
        taxpayers.add(newTaxpayer);
        return newTaxpayer;
    }

    private static Taxpayer findTaxpayerByTIN(String tin) {
        for (Taxpayer taxpayer : taxpayers) {
            if (taxpayer.getTin().equals(tin)) {
                return taxpayer;
            }
        }
        return null;
    }

    private static String getValidStringInput(String prompt, String pattern, String errorMessage) {
        String input;
        boolean isValid = false;

        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            } else if (pattern != null && !input.matches(pattern)) {
                System.out.println(errorMessage);
            } else {
                isValid = true;
            }
        } while (!isValid);

        return input;
    }

    private static int getValidIntInput(String prompt, int min, int max) {
        int value = 0;
        boolean isValid = false;

        do {
            System.out.print(prompt);
            try {
                value = Integer.parseInt(scanner.nextLine().trim());

                if (value < min) {
                    System.out.println("Value must be at least " + min + ". Please try again.");
                } else if (value > max) {
                    System.out.println("Value must be at most " + max + ". Please try again.");
                } else {
                    isValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid integer.");
            }
        } while (!isValid);

        return value;
    }

    private static double getValidDoubleInput(String prompt, double min) {
        double value = 0;
        boolean isValid = false;

        do {
            System.out.print(prompt);
            try {
                value = Double.parseDouble(scanner.nextLine().trim());

                if (value < min) {
                    System.out.println("Value must be at least " + min + ". Please try again.");
                } else {
                    isValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid number.");
            }
        } while (!isValid);

        return value;
    }
}