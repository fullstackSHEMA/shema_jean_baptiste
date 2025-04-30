// Abstract Vehicle class
abstract class Vehicle {
    private String vehicleId;
    private String ownerName;
    private int yearOfFabrication;
    private String registrationNumber;
    private double baseTaxRate;
    private String vehicleType;

    public Vehicle(String vehicleId, String ownerName, int yearOfFabrication,
                   String registrationNumber, double baseTaxRate, String vehicleType) {
        this.vehicleId = vehicleId;
        this.ownerName = ownerName;
        setYearOfFabrication(yearOfFabrication);
        this.registrationNumber = registrationNumber;
        setBaseTaxRate(baseTaxRate);
        this.vehicleType = vehicleType;
    }

    public abstract double calculateTax();
    public abstract void generateTaxReport();

    public boolean validateYearOfFabrication(int year) {
        int currentYear = java.time.Year.now().getValue();
        return year <= currentYear;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getYearOfFabrication() {
        return yearOfFabrication;
    }

    public void setYearOfFabrication(int yearOfFabrication) {
        if (validateYearOfFabrication(yearOfFabrication)) {
            this.yearOfFabrication = yearOfFabrication;
        } else {
            throw new IllegalArgumentException("Year of fabrication cannot be in the future");
        }
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public double getBaseTaxRate() {
        return baseTaxRate;
    }

    public void setBaseTaxRate(double baseTaxRate) {
        if (baseTaxRate >= 0) {
            this.baseTaxRate = baseTaxRate;
        } else {
            throw new IllegalArgumentException("Base tax rate cannot be negative");
        }
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getVehicleAge() {
        int currentYear = java.time.Year.now().getValue();
        return currentYear - yearOfFabrication;
    }

    @Override
    public String toString() {
        return "Vehicle ID: " + vehicleId +
                "\nOwner: " + ownerName +
                "\nYear of Fabrication: " + yearOfFabrication +
                "\nRegistration Number: " + registrationNumber +
                "\nVehicle Type: " + vehicleType +
                "\nBase Tax Rate: $" + baseTaxRate;
    }
}

// Car class
class Car extends Vehicle {
    private boolean isElectric;

    public Car(String vehicleId, String ownerName, int yearOfFabrication,
               String registrationNumber, double baseTaxRate, boolean isElectric) {
        super(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, "Car");
        this.isElectric = isElectric;
    }

    public boolean isElectric() {
        return isElectric;
    }

    public void setElectric(boolean electric) {
        isElectric = electric;
    }

    @Override
    public double calculateTax() {
        double tax = getBaseTaxRate();

        if (isElectric) {
            tax *= 0.8;
        }

        if (getVehicleAge() > 10) {
            tax *= 0.9;
        }

        return tax;
    }

    @Override
    public void generateTaxReport() {
        System.out.println("\n=== CAR TAX REPORT ===");
        System.out.println(toString());
        System.out.println("Electric: " + (isElectric ? "Yes" : "No"));
        System.out.println("Vehicle Age: " + getVehicleAge() + " years");
        System.out.println("Calculated Tax: $" + String.format("%.2f", calculateTax()));
        System.out.println("====================");
    }

    @Override
    public String toString() {
        return super.toString() + "\nElectric: " + (isElectric ? "Yes" : "No");
    }
}

class Truck extends Vehicle {
    private double loadCapacity;

    public Truck(String vehicleId, String ownerName, int yearOfFabrication,
                 String registrationNumber, double baseTaxRate, double loadCapacity) {
        super(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, "Truck");
        setLoadCapacity(loadCapacity);
    }

    public double getLoadCapacity() {
        return loadCapacity;
    }

    public void setLoadCapacity(double loadCapacity) {
        if (loadCapacity > 0) {
            this.loadCapacity = loadCapacity;
        } else {
            throw new IllegalArgumentException("Load capacity must be positive");
        }
    }

    @Override
    public double calculateTax() {
        double tax = getBaseTaxRate();

        if (getVehicleAge() > 15) {
            tax *= 1.15;
        }

        if (loadCapacity > 10) {
            tax *= 1.25;
        }

        return tax;
    }

    @Override
    public void generateTaxReport() {
        System.out.println("\n=== TRUCK TAX REPORT ===");
        System.out.println(toString());
        System.out.println("Vehicle Age: " + getVehicleAge() + " years");
        System.out.println("Calculated Tax: $" + String.format("%.2f", calculateTax()));
        System.out.println("========================");
    }

    @Override
    public String toString() {
        return super.toString() + "\nLoad Capacity: " + loadCapacity + " tons";
    }
}

class Motorcycle extends Vehicle {
    private int engineCapacity;

    public Motorcycle(String vehicleId, String ownerName, int yearOfFabrication,
                      String registrationNumber, double baseTaxRate, int engineCapacity) {
        super(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, "Motorcycle");
        setEngineCapacity(engineCapacity);
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(int engineCapacity) {
        if (engineCapacity > 0) {
            this.engineCapacity = engineCapacity;
        } else {
            throw new IllegalArgumentException("Engine capacity must be positive");
        }
    }

    @Override
    public double calculateTax() {
        double tax = getBaseTaxRate();

        if (engineCapacity > 500) {
            tax *= 1.2;
        }

        int ageReductions = getVehicleAge() / 5;
        tax *= Math.pow(0.95, ageReductions);

        return tax;
    }

    @Override
    public void generateTaxReport() {
        System.out.println("\n=== MOTORCYCLE TAX REPORT ===");
        System.out.println(toString());
        System.out.println("Vehicle Age: " + getVehicleAge() + " years");
        System.out.println("Calculated Tax: $" + String.format("%.2f", calculateTax()));
        System.out.println("============================");
    }

    @Override
    public String toString() {
        return super.toString() + "\nEngine Capacity: " + engineCapacity + " cc";
    }
}

class Bus extends Vehicle {
    private int passengerCapacity;

    public Bus(String vehicleId, String ownerName, int yearOfFabrication,
               String registrationNumber, double baseTaxRate, int passengerCapacity) {
        super(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, "Bus");
        setPassengerCapacity(passengerCapacity);
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        if (passengerCapacity > 0) {
            this.passengerCapacity = passengerCapacity;
        } else {
            throw new IllegalArgumentException("Passenger capacity must be positive");
        }
    }

    @Override
    public double calculateTax() {
        double tax = getBaseTaxRate();

        double passengerFactor = 1 + ((passengerCapacity / 10) * 0.02);
        tax *= passengerFactor;

        if (getVehicleAge() > 20) {
            tax *= 1.1;
        }

        return tax;
    }

    @Override
    public void generateTaxReport() {
        System.out.println("\n=== BUS TAX REPORT ===");
        System.out.println(toString());
        System.out.println("Vehicle Age: " + getVehicleAge() + " years");
        System.out.println("Calculated Tax: $" + String.format("%.2f", calculateTax()));
        System.out.println("=====================");
    }

    @Override
    public String toString() {
        return super.toString() + "\nPassenger Capacity: " + passengerCapacity;
    }
}

class SUV extends Vehicle {
    private boolean fourWheelDrive;

    public SUV(String vehicleId, String ownerName, int yearOfFabrication,
               String registrationNumber, double baseTaxRate, boolean fourWheelDrive) {
        super(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, "SUV");
        this.fourWheelDrive = fourWheelDrive;
    }

    public boolean isFourWheelDrive() {
        return fourWheelDrive;
    }

    public void setFourWheelDrive(boolean fourWheelDrive) {
        this.fourWheelDrive = fourWheelDrive;
    }

    @Override
    public double calculateTax() {
        double tax = getBaseTaxRate();

        if (fourWheelDrive) {
            tax *= 1.1;
        }

        if (getVehicleAge() > 10) {
            tax *= 0.95;
        }

        return tax;
    }

    @Override
    public void generateTaxReport() {
        System.out.println("\n=== SUV TAX REPORT ===");
        System.out.println(toString());
        System.out.println("Vehicle Age: " + getVehicleAge() + " years");
        System.out.println("Calculated Tax: $" + String.format("%.2f", calculateTax()));
        System.out.println("=====================");
    }

    @Override
    public String toString() {
        return super.toString() + "\nFour-Wheel Drive: " + (fourWheelDrive ? "Yes" : "No");
    }
}

class VehicleManagementSystem {
    private static final java.util.Scanner scanner = new java.util.Scanner(System.in);
    private static final java.util.ArrayList<Vehicle> vehicles = new java.util.ArrayList<>();

    public static void main(String[] args) {
        boolean exit = false;

        // Display instructional welcome message
        System.out.println("=========================================");
        System.out.println("Welcome to the Vehicle Tax Management System!");
        System.out.println("This system helps you manage vehicle tax records.");
        System.out.println("=========================================");
        displayMenu(); // Moved below the welcome message

        while (!exit) {
            try {
                int choice = getValidIntInput();
                exit = processMenuChoice(choice);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                displayMenu();
                System.out.print("Enter your choice (1-7): ");
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\nPlease select an option:");
        System.out.println("1. Register a new vehicle");
        System.out.println("2. View registered vehicles");
        System.out.println("3. Calculate tax for all vehicles");
        System.out.println("4. Generate tax reports");
        System.out.println("5. Search vehicle by owner name");
        System.out.println("6. View vehicle type summary");
        System.out.println("7. Exit");
    }

    private static boolean processMenuChoice(int choice) {
        switch (choice) {
            case 1:
                registerNewVehicle();
                displayMenu();
                System.out.print("Enter your choice (1-7): ");
                return false;
            case 2:
                viewRegisteredVehicles();
                displayMenu();
                System.out.print("Enter your choice (1-7): ");
                return false;
            case 3:
                calculateTaxForAllVehicles();
                displayMenu();
                System.out.print("Enter your choice (1-7): ");
                return false;
            case 4:
                generateTaxReports();
                displayMenu();
                System.out.print("Enter your choice (1-7): ");
                return false;
            case 5:
                searchVehicleByOwnerName();
                displayMenu();
                System.out.print("Enter your choice (1-7): ");
                return false;
            case 6:
                viewVehicleTypeSummary();
                displayMenu();
                System.out.print("Enter your choice (1-7): ");
                return false;
            case 7:
                System.out.println("Thank you for using the Vehicle Tax Management System. Goodbye!");
                displayMenu(); // Optional, as program exits
                return true;
            default:
                System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                displayMenu();
                System.out.print("Enter your choice (1-7): ");
                return false;
        }
    }

    private static void registerNewVehicle() {
        System.out.println("\n=== Register a New Vehicle ===");
        String vehicleType = getVehicleType();

        try {
            String vehicleId = getUniqueVehicleId();
            String registrationNumber = getUniqueRegistrationNumber();
            String ownerName = getValidOwnerName();
            int yearOfFabrication = getValidYearOfFabrication();
            double baseTaxRate = getValidBaseTaxRate();

            switch (vehicleType.toUpperCase()) {
                case "CAR":
                    registerCar(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate);
                    break;
                case "TRUCK":
                    registerTruck(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate);
                    break;
                case "MOTORCYCLE":
                    registerMotorcycle(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate);
                    break;
                case "BUS":
                    registerBus(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate);
                    break;
                case "SUV":
                    registerSUV(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate);
                    break;
            }

            System.out.println("\nVehicle registered successfully!");
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private static String getVehicleType() {
        while (true) {
            System.out.println("Available vehicle types: Car, Truck, Motorcycle, Bus, SUV");
            System.out.print("Enter vehicle type: ");
            String type = scanner.nextLine().trim();

            switch (type.toUpperCase()) {
                case "CAR":
                case "TRUCK":
                case "MOTORCYCLE":
                case "BUS":
                case "SUV":
                    return type;
                default:
                    System.out.println("Invalid vehicle type. Please enter a valid type.");
            }
        }
    }

    private static void registerCar(String vehicleId, String ownerName, int yearOfFabrication,
                                    String registrationNumber, double baseTaxRate) {
        boolean isElectric = getValidBooleanInput("Is the car electric? (true/false): ");
        vehicles.add(new Car(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, isElectric));
    }

    private static void registerTruck(String vehicleId, String ownerName, int yearOfFabrication,
                                      String registrationNumber, double baseTaxRate) {
        double loadCapacity = getValidLoadCapacity();
        vehicles.add(new Truck(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, loadCapacity));
    }

    private static void registerMotorcycle(String vehicleId, String ownerName, int yearOfFabrication,
                                           String registrationNumber, double baseTaxRate) {
        int engineCapacity = getValidEngineCapacity();
        vehicles.add(new Motorcycle(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, engineCapacity));
    }

    private static void registerBus(String vehicleId, String ownerName, int yearOfFabrication,
                                    String registrationNumber, double baseTaxRate) {
        int passengerCapacity = getValidPassengerCapacity();
        vehicles.add(new Bus(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, passengerCapacity));
    }

    private static void registerSUV(String vehicleId, String ownerName, int yearOfFabrication,
                                    String registrationNumber, double baseTaxRate) {
        boolean fourWheelDrive = getValidBooleanInput("Does the SUV have four-wheel drive? (true/false): ");
        vehicles.add(new SUV(vehicleId, ownerName, yearOfFabrication, registrationNumber, baseTaxRate, fourWheelDrive));
    }

    private static String getValidOwnerName() {
        while (true) {
            String ownerName = getValidStringInput("Enter owner name: ");

            if (ownerName.matches("^[a-zA-Z ]+$")) {
                return ownerName;
            } else {
                System.out.println("Owner name should contain only letters and spaces. Please try again.");
            }
        }
    }

    private static String getValidVehicleId() {
        while (true) {
            String vehicleId = getValidStringInput("Enter vehicle ID: ");

            if (vehicleId.matches("^[A-Z]-\\d{3}$")) {
                return vehicleId;
            } else {
                System.out.println("Invalid vehicle ID format. Please use format like 'V-123' (one uppercase letter, hyphen, 3 digits).");
            }
        }
    }

    private static String getUniqueVehicleId() {
        while (true) {
            String vehicleId = getValidVehicleId();

            boolean isDuplicate = false;
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getVehicleId().equals(vehicleId)) {
                    isDuplicate = true;
                    break;
                }
            }

            if (isDuplicate) {
                System.out.println("This vehicle ID already exists. Please enter a unique ID.");
            } else {
                return vehicleId;
            }
        }
    }

    private static String getValidRegistrationNumber() {
        while (true) {
            String registrationNumber = getValidStringInput("Enter registration number (numbers only): ");

            if (registrationNumber.matches("^\\d+$")) {
                return registrationNumber;
            } else {
                System.out.println("Invalid registration number format. Please use numbers only.");
            }
        }
    }

    private static String getUniqueRegistrationNumber() {
        while (true) {
            String registrationNumber = getValidRegistrationNumber();

            boolean isDuplicate = false;
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getRegistrationNumber().equals(registrationNumber)) {
                    isDuplicate = true;
                    break;
                }
            }

            if (isDuplicate) {
                System.out.println("This registration number already exists. Please enter a unique registration number.");
            } else {
                return registrationNumber;
            }
        }
    }

    private static int getValidYearOfFabrication() {
        final int MINIMUM_YEAR = 1800;
        int currentYear = java.time.Year.now().getValue();

        while (true) {
            int year = getValidIntInput("Enter year of fabrication: ");

            if (year >= MINIMUM_YEAR && year <= currentYear) {
                return year;
            } else if (year < MINIMUM_YEAR) {
                System.out.println("Year of fabrication cannot be before " + MINIMUM_YEAR + ".");
            } else {
                System.out.println("Year of fabrication cannot be in the future. Current year is " + currentYear);
            }
        }
    }

    private static double getValidLoadCapacity() {
        final double MAX_LOAD = 50.0;

        while (true) {
            double loadCapacity = getValidDoubleInput("Enter load capacity (in tons): ");

            if (loadCapacity <= MAX_LOAD) {
                return loadCapacity;
            } else {
                System.out.println("Load capacity exceeds maximum limit of " + MAX_LOAD + " tons. Please enter a valid value.");
            }
        }
    }

    private static int getValidEngineCapacity() {
        final int MAX_ENGINE_CAPACITY = 5000;

        while (true) {
            int engineCapacity = getValidIntInput("Enter engine capacity (in cc): ");

            if (engineCapacity <= MAX_ENGINE_CAPACITY) {
                return engineCapacity;
            } else {
                System.out.println("Engine capacity exceeds maximum limit of " + MAX_ENGINE_CAPACITY + " cc. Please enter a valid value.");
            }
        }
    }

    private static int getValidPassengerCapacity() {
        final int MAX_PASSENGERS = 100;

        while (true) {
            int passengerCapacity = getValidIntInput("Enter passenger capacity: ");

            if (passengerCapacity <= MAX_PASSENGERS) {
                return passengerCapacity;
            } else {
                System.out.println("Passenger capacity exceeds maximum limit of " + MAX_PASSENGERS + ". Please enter a valid value.");
            }
        }
    }

    private static double getValidBaseTaxRate() {
        final double MAX_TAX_RATE = 10000.0;

        while (true) {
            double baseTaxRate = getValidDoubleInput("Enter base tax rate ($): ");

            if (baseTaxRate <= MAX_TAX_RATE) {
                return baseTaxRate;
            } else {
                System.out.println("Base tax rate exceeds maximum limit of $" + MAX_TAX_RATE + ". Please enter a valid value.");
            }
        }
    }

    private static void viewRegisteredVehicles() {
        System.out.println("\n=== Registered Vehicles ===");

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered yet.");
            return;
        }

        for (int i = 0; i < vehicles.size(); i++) {
            System.out.println("\nVehicle #" + (i + 1));
            System.out.println(vehicles.get(i).toString());
        }
    }

    private static void calculateTaxForAllVehicles() {
        System.out.println("\n=== Tax Calculation for All Vehicles ===");

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered yet.");
            return;
        }

        double totalTax = 0;

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            double tax = vehicle.calculateTax();
            totalTax += tax;

            System.out.println("\nVehicle #" + (i + 1) + " - " + vehicle.getVehicleType());
            System.out.println("Registration: " + vehicle.getRegistrationNumber());
            System.out.println("Owner: " + vehicle.getOwnerName());
            System.out.println("Tax Amount: $" + String.format("%.2f", tax));
        }

        System.out.println("\nTotal Tax for All Vehicles: $" + String.format("%.2f", totalTax));
    }

    private static void generateTaxReports() {
        System.out.println("\n=== Generate Tax Reports ===");

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered yet.");
            return;
        }

        for (Vehicle vehicle : vehicles) {
            vehicle.generateTaxReport();
        }
    }

    private static void searchVehicleByOwnerName() {
        System.out.println("\n=== Search Vehicle by Owner Name ===");

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered yet.");
            return;
        }

        String searchQuery = getValidStringInput("Enter owner name (or part of name): ");

        java.util.ArrayList<Vehicle> matchingVehicles = new java.util.ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getOwnerName().toLowerCase().contains(searchQuery.toLowerCase())) {
                matchingVehicles.add(vehicle);
            }
        }

        if (matchingVehicles.isEmpty()) {
            System.out.println("No vehicles found for owner name containing '" + searchQuery + "'.");
            return;
        }

        System.out.println("\nMatching Vehicles:");
        for (int i = 0; i < matchingVehicles.size(); i++) {
            System.out.println("\nVehicle #" + (i + 1));
            System.out.println(matchingVehicles.get(i).toString());
        }
    }

    private static void viewVehicleTypeSummary() {
        System.out.println("\n=== Vehicle Type Summary ===");

        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered yet.");
            return;
        }

        int carCount = 0, truckCount = 0, motorcycleCount = 0, busCount = 0, suvCount = 0;

        for (Vehicle vehicle : vehicles) {
            switch (vehicle.getVehicleType()) {
                case "Car":
                    carCount++;
                    break;
                case "Truck":
                    truckCount++;
                    break;
                case "Motorcycle":
                    motorcycleCount++;
                    break;
                case "Bus":
                    busCount++;
                    break;
                case "SUV":
                    suvCount++;
                    break;
            }
        }

        System.out.println("Total Vehicles: " + vehicles.size());
        System.out.println("Cars: " + carCount);
        System.out.println("Trucks: " + truckCount);
        System.out.println("Motorcycles: " + motorcycleCount);
        System.out.println("Buses: " + busCount);
        System.out.println("SUVs: " + suvCount);
    }

    private static String getValidStringInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
    }

    private static int getValidIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    private static int getValidIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("Value must be positive. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static double getValidDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("Value must be positive. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static boolean getValidBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("true") || input.equals("false")) {
                return Boolean.parseBoolean(input);
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }
    }
}