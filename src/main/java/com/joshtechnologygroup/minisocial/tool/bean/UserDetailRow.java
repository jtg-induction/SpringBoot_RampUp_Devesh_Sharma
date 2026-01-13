package com.joshtechnologygroup.minisocial.tool.bean;

import com.joshtechnologygroup.minisocial.bean.Gender;
import com.joshtechnologygroup.minisocial.bean.MaritalStatus;
import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDetailRow {
    @CsvBindByName(column = "Email")
    @NotBlank(message = "Email ID is required")
    @Email(message = "Invalid email format")
    private String emailId;

    @CsvBindByName(column = "Password")
    @NotBlank(message = "Password is required")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password;

    @CsvBindByName(column = "First Name")
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @CsvBindByName(column = "Last Name")
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @CsvBindByName(column = "Age")
    @NotNull(message = "Age is required")
    @Positive
    @Max(value = 150, message = "Age must be less than or equal to 150")
    private int age;

    @CsvBindByName(column = "Gender")
    @NotNull(message = "Gender is required")
    private Gender gender;

    @CsvBindByName(column = "Marital Status")
    @NotNull(message = "Marital status is required")
    private MaritalStatus maritalStatus;

    @CsvBindByName(column = "R_Address")
    @Size(max = 255, message = "Residential address must not exceed 255 characters")
    @NotBlank(message = "Residential address is required")
    private String residentialAddress;

    @CsvBindByName(column = "R_City")
    @Size(max = 100, message = "Residential city must not exceed 100 characters")
    @NotBlank(message = "Residential city is required")
    private String residentialCity;

    @CsvBindByName(column = "R_State")
    @Size(max = 100, message = "Residential state must not exceed 100 characters")
    @NotBlank(message = "Residential state is required")
    private String residentialState;

    @CsvBindByName(column = "R_Country")
    @Size(max = 100, message = "Residential country must not exceed 100 characters")
    @NotBlank(message = "Residential country is required")
    private String residentialCountry;

    @CsvBindByName(column = "R_Contact_No_1")
    @Size(max = 20, message = "Primary contact number must not exceed 20 characters")
    @NotBlank(message = "Primary contact number is required")
    private String primaryContactNumber;

    @CsvBindByName(column = "R_Contact_No_2")
    @Size(max = 20, message = "Secondary contact number must not exceed 20 characters")
    private String secondaryContactNumber;

    @CsvBindByName(column = "O_Address")
    @Size(max = 255, message = "Office address must not exceed 255 characters")
    @NotBlank(message = "Office address is required")
    private String officeAddress;

    @CsvBindByName(column = "O_City")
    @Size(max = 100, message = "Office city must not exceed 100 characters")
    @NotBlank(message = "Office city is required")
    private String officeCity;

    @CsvBindByName(column = "O_State")
    @Size(max = 100, message = "Office state must not exceed 100 characters")
    @NotBlank(message = "Office state is required")
    private String officeState;

    @CsvBindByName(column = "O_Country")
    @Size(max = 100, message = "Office country must not exceed 100 characters")
    @NotBlank(message = "Office country is required")
    private String officeCountry;

    @CsvBindByName(column = "O_Employee_Code")
    @Size(max = 20, message = "Employee code must not exceed 20 characters")
    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @CsvBindByName(column = "O_Company_Contact_No")
    @Size(max = 20, message = "Company contact number must not exceed 20 characters")
    @NotBlank(message = "Company contact number is required")
    private String companyContactNumber;

    @CsvBindByName(column = "O_Company_Contact_Email")
    @Email(message = "Invalid company email format")
    @Size(max = 255, message = "Company email must not exceed 255 characters")
    @NotBlank(message = "Company contact email is required")
    private String companyContactEmail;

    @CsvBindByName(column = "O_Company_Name")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @NotBlank(message = "Company name is required")
    private String companyName;
}
