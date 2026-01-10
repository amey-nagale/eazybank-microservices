package com.eazybytes.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name="Account", description = "Schema to hold Account information")
public class AccountsDto {
	
	@NotEmpty(message = "Account number cannot be null or empty")
	@Pattern(regexp = "(^$[0-9]{6})", message = "Mobile number should be 6 digits only.")
	private Long accountNumber;
	@NotEmpty(message = "Account type cannot be null or empty")
	private String accountType;
	@NotEmpty(message = "Branch address cannot be null or empty")
	private String branchAddress;

}
