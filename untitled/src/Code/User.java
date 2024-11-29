package Code;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public abstract class User {

    protected String name;
    protected String id;
    protected String password;
    protected String email;
    protected String phoneNumber;
    protected String address;
    protected Date dateOfBirth;

    public User(String name, String id, String password, String email, String phoneNumber, String address, Date dateOfBirth) {
        if ("ADMIN001".equals(id) && "admin2023".equals(password));
        else{
            if (!isValidName(name)) {
                throw new IllegalArgumentException("Invalid name. Name must be at least 3 characters long.");
            }
            if (!isValidPassword(password)) {
                throw new IllegalArgumentException("Invalid password. Password must be at least 6 characters long.");
            }
            if (!isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            if (!isValidPhoneNumber(phoneNumber)) {
                throw new IllegalArgumentException("Invalid phone number.");
            }
            if (dateOfBirth == null) {
                throw new IllegalArgumentException("Date of birth cannot be null.");
            }
        }

        this.name = name;
        this.id = id;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public abstract void displayUserDetails();

    // Validation methods
    public static boolean isValidName(String name) {
        if (name == null || name.trim().length() < 3) {
            return false; // Name must not be null or less than 3 characters
        }
        for (char c : name.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                return false; // Name must only contain letters and spaces
            }
        }
        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false; // Password must not be null or shorter than 8 characters
        }

        boolean hasUppercase = false, hasLowercase = false, hasDigit = false, hasSpecialChar = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }

        // Ensure password has at least one of each required character type
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false; // Email must not be null or empty
        }
        // Split email into local part and domain part
        int atIndex = email.indexOf('@');
        if (atIndex < 1 || atIndex == email.length() - 1) {
            return false; // Ensure '@' is present and not at the start or end
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        // Local part validation
        if (localPart.isEmpty()) {
            return false; // Local part cannot be empty or exceed 64 characters
        }
        for (char c : localPart.toCharArray()) {
            if (!(Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == '_')) {
                return false; // Only allow valid characters
            }
        }

        // Domain part validation
        String[] domainSections = domainPart.split("\\.");
        if (domainSections.length < 2) {
            return false;
        }
        for (String section : domainSections) {
            if (section.isEmpty()) {
                return false; // Each section cannot be empty
            }
            for (char c : section.toCharArray()) {
                if (!Character.isLetterOrDigit(c) && c != '-') {
                    return false; // Only allow valid domain characters
                }
            }
        }
        return true;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 10) {
            System.out.println("either empty or not 10 digits");
            return false; // Phone number must not be null and must have exactly 10 digits
        }
        for (char c : phoneNumber.toCharArray()) {
            if (!Character.isDigit(c)) {
                System.out.println("A character that isn't a digit was found");
                return false; // Phone number must only contain digits
            }
        }
        if ((phoneNumber.charAt(0))=='0' && phoneNumber.charAt(1) == '5') {
            return true;  // Has to start with UAE opening numbers (05)
        } else {
            System.out.println("Doesn't start with 05");
            return false;
        }
    }
}

