package com.eazybytes.accounts.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IAccountsService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor // due to this no need to manually autowire dependencies
public class AccountsServiceImpl implements IAccountsService{
	
	private AccountRepository accountRepository;
	private CustomerRepository customerRepository;

	@Override
	public void createAccount(CustomerDto customerDto) {
		Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
		
		Optional<Customer> optionalCustomer = 
				customerRepository.findByMobileNumber(customer.getMobileNumber());
		
		if(optionalCustomer.isPresent()) {
			throw new CustomerAlreadyExistsException(
					"Customer already registered with given mobile number: " 
					+ customer.getMobileNumber());
		}
//		customer.setCreatedAt(LocalDateTime.now());
//		customer.setCreatedBy("dummy");
		Customer savedCustomer = customerRepository.save(customer);
		accountRepository.save(createNewAccount(savedCustomer));
	}
	
	private Accounts createNewAccount(Customer customer) {
		Accounts newAccount = new Accounts();
		newAccount.setCustomerId(customer.getCustomerId());
		
		long randomAccNumber = 100000L + new Random().nextInt(900000);
		newAccount.setAccountNumber(randomAccNumber);
		
		newAccount.setAccountType(AccountsConstants.SAVINGS);
		newAccount.setBranchAddress(AccountsConstants.ADDRESS);
//		newAccount.setCreatedAt(LocalDateTime.now());
//		newAccount.setCreatedBy("dummy");
		return newAccount;
	}

	@Override
	public CustomerDto fetchAccount(String mobileNumber) {
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
		
		CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
		AccountsDto accountsDto = AccountsMapper.mapToAccountsDto(accounts, new AccountsDto());
		customerDto.setAccountsDto(accountsDto);
		return customerDto;
	}
	
	public boolean updateAccount(CustomerDto customerDto) {
		boolean isUpdated = false;
		AccountsDto accountsDto = customerDto.getAccountsDto();
		if(null != accountsDto) {
			Accounts accounts = 
					accountRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
							() -> new ResourceNotFoundException("Account", 
									"AcoountNumber", accountsDto.getAccountNumber().toString()));
			AccountsMapper.mapToAccount(accountsDto, accounts);
			accounts = accountRepository.save(accounts);
			
			Long customerId =accounts.getCustomerId();
			Customer customer =
					customerRepository.findById(customerId).orElseThrow(
							() -> new ResourceNotFoundException("Customer", 
									"CustomerId", customerId.toString()));
			CustomerMapper.mapToCustomer(customerDto, customer);
			customerRepository.save(customer);
			isUpdated = true;
		}
		return isUpdated;
	}
	
	public boolean deleteAccount(String mobileNumber) {
		Customer customer = customerRepository.findByMobileNumber(mobileNumber)
				.orElseThrow(
						() -> new ResourceNotFoundException("Customer",
								"mobileNumer", mobileNumber));
		accountRepository.deleteByCustomerId(customer.getCustomerId());
		customerRepository.deleteById(customer.getCustomerId());
		return true;
	}

}
