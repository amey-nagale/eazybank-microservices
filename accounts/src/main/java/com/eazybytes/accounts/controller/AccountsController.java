package com.eazybytes.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsContactInfoDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.dto.ErrorResponseDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.service.IAccountsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;

@Tag(name = "CRUD REST APIs for Accounts Controller",
		description = "CRUD REST APIs in EazyBank to Create, Update, Fetch, Delete account details"
		)
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
//@AllArgsConstructor
@Validated
public class AccountsController {
	
	private IAccountsService iAccountsService;
	
	@Value("${build.version}")
	private String buildVersion;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	
	private AccountsContactInfoDto accountsContactInfoDto;
	
	public AccountsController(IAccountsService iAccountsService) {
		super();
		this.iAccountsService = iAccountsService;
	}

	@Operation(summary = "Create Account REST API",
				description = "REST API to create new Customer and Account in EazyBank")
	@ApiResponse(responseCode = "201", description = "HTTP Status Created")
	@PostMapping("/create")
	public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto){
		
		iAccountsService.createAccount(customerDto);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ResponseDto(AccountsConstants.STATUS_201, 
										AccountsConstants.MESSAGE_201));
	}
	
	@Operation(summary = "Fetch Account Details REST API",
				description = "REST API to fetch Customer & Account based on Mobile Number")
	@ApiResponse(responseCode = "200", description = "HTTP Status Ok")
	@GetMapping("/fetch")
	public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam
			@Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
			String mobileNumber){
		CustomerDto customerDto= iAccountsService.fetchAccount(mobileNumber);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(customerDto);
	}
	
	@Operation(summary = "Update Account Details REST API",
			description = "REST API to update Customer & Account details based on a account number")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "HTTP Status OK"),
		@ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error")
	})
	@PutMapping("/update")
	public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto){
		boolean isUpdated = iAccountsService.updateAccount(customerDto);
		if(isUpdated) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(new ResponseDto(AccountsConstants.STATUS_200, 
							AccountsConstants.MESSAGE_200));
		} else {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDto(AccountsConstants.STATUS_500, 
							AccountsConstants.MESSAGE_500));
		}
	}
	
	@Operation(summary = "Delete Account Details REST API",
			description = "REST API to delete Customer & Account details based on a account number")
	@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "HTTP Status OK"
        ),
        @ApiResponse(
                responseCode = "500",
                description = "HTTP Status Internal Server Error",
                content = @Content(
                        schema = @Schema(implementation = ErrorResponseDto.class)
                )
        )
	})
	@DeleteMapping("/delete")
	public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam 
			@Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
			String mobileNumber){
		boolean isDeleted = iAccountsService.deleteAccount(mobileNumber);
		if(isDeleted) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(new ResponseDto(AccountsConstants.STATUS_200, 
							AccountsConstants.MESSAGE_200));
		}else {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDto(AccountsConstants.STATUS_500, 
							AccountsConstants.MESSAGE_500));
		}
	}
	
	@Operation(summary = "Fetch build information",
			description = "Fetch build information from Accounts MS")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "HTTP Status OK"),
		@ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error")
	})
	@GetMapping("/build-info")
	public ResponseEntity<String> getBuildInfo(){
		return ResponseEntity.status(HttpStatus.OK).body(buildVersion);
	}
	
	@Operation(summary = "Fetch Java Version information",
			description = "Fetch Java Version for Accounts MS")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "HTTP Status OK"),
		@ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error")
	})
	@GetMapping("/java-version")
	public ResponseEntity<String> getJavaVersion(){
		return ResponseEntity.status(HttpStatus.OK).body(environment.getProperty("JAVA_HOME"));
	}
	
	@Operation(summary = "Fetch Contact Details information",
			description = "Fetch Contact Details for Accounts MS")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "HTTP Status OK"),
		@ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error")
	})
	@GetMapping("/contact-info")
	public ResponseEntity<AccountsContactInfoDto> getContactInfo(){
		return ResponseEntity.status(HttpStatus.OK).body(accountsContactInfoDto);
	}
}
