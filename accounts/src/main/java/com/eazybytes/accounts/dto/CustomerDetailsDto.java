package com.eazybytes.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name="CustomerDetails", description = "Schema to hold Customer, Account, Cards ,and Loans information")
public class CustomerDetailsDto {
	
	@Schema(description = "Name of customer", example = "Amey")
	@NotEmpty(message = "Name cannot be null or empty.")
	@Size(min = 5, max = 15, message = "Length of name should be between 5 to 15 characters.")
	private String name;
	@NotEmpty(message = "Email cannot be null or empty.")
	@Email(message = "Email should be a valid value.")
	private String email;
	@Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
	private String mobileNumber;
	
	@Schema(description = "Account details of the Customer")
	private AccountsDto accountsDto;
	@Schema(description = "Card details of the Customer")
	private CardsDto cardsDto;
	@Schema(description = "Loan details of the Customer")
	private LoansDto loansDto;

}
