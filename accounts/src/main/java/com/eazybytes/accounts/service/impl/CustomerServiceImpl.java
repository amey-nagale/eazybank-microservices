package com.eazybytes.accounts.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CardsDto;
import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.dto.LoansDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.ICustomerService;
import com.eazybytes.accounts.service.client.CardsFeignClient;
import com.eazybytes.accounts.service.client.LoansFeignClient;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService{
	
	private AccountRepository accountRepository;
	private CustomerRepository customerRepository;
	private CardsFeignClient cardsFeignClient;
	private LoansFeignClient loansFeignClient;

	@Override
	public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
		Customer customer =
				customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
						() -> new ResourceNotFoundException("Customer",
								"mobileNumber" , mobileNumber)
						);
				
		Accounts accounts =
		accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
				() -> new ResourceNotFoundException("Account", "CustomerId",
						customer.getCustomerId().toString())
				);
		
		CustomerDetailsDto customerDetailsDto =
				CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
		customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
		
		ResponseEntity<LoansDto> loansResponseEntity = loansFeignClient.fetchLoanDetails(mobileNumber);
		customerDetailsDto.setLoansDto(loansResponseEntity.getBody());
		
		ResponseEntity<CardsDto> cardsResponseEntity = cardsFeignClient.fetchCardDetails(mobileNumber);
		customerDetailsDto.setCardsDto(cardsResponseEntity.getBody());
		
		return customerDetailsDto;
	}

}
