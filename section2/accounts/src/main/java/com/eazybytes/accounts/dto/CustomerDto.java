package com.eazybytes.accounts.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CustomerDto {
    @NotEmpty(message = "name of the customer is empty, enter a valid name")
    @Size(min = 2, max = 20, message = "name must contain at least 2 characters")
    private String name;

    @NotEmpty(message = "email is empty")
    @Email(message = "email must be valid")
    private String email;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "mobile number can be empty or must be 10 digits if you don't have mobile number now you can update later")
    private String mobileNumber;


    private AccountsDto accountsDto;
}
