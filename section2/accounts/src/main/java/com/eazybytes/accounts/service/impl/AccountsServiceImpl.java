package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.Constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.mapper.AccountsMapper;
import com.eazybytes.accounts.mapper.CustomerMapper;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    CustomerRepository customerRepository;

    /**
     * @param customerDto
     * @return
     */
    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if(accountsDto != null) {
            Accounts account = accountsRepository.findById(accountsDto.getAccountNumber())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Account", "Account Number",
                                    accountsDto.getAccountNumber().toString()));
            AccountsMapper.mapToAccounts(accountsDto, account);
            account = accountsRepository.save(account);
            Long customerId = account.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () ->
                            new ResourceNotFoundException("Customer", "CustomerId",
                                    customerId.toString()));
            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return isUpdated;
    }

    /**
     * @param mobileNumber
     * @return
     */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber);
        if(customer == null) {
            throw new ResourceNotFoundException("Customer", "Mobile Number", mobileNumber);
        }
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());

        return true;
    }

    AccountsRepository accountsRepository;

    /**
     * @param customerDto
     */
    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Customer optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer != null) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobile number "
            +customerDto.getMobileNumber());
        }
        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }
    private Accounts createNewAccount(Customer savedCustomer) {
        Accounts newAccount = new Accounts();
        long randomAccountNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setCustomerId(savedCustomer.getCustomerId());
        newAccount.setAccountNumber(randomAccountNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }

    /**
     * @param mobileNumber
     * @return
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Optional<Customer> customer = Optional.ofNullable(customerRepository.findByMobileNumber(mobileNumber));
        if(customer.isEmpty()) {
            throw new ResourceNotFoundException("Customer", "MobileNumber", mobileNumber);
        }
        Optional<Accounts> accounts = Optional.ofNullable(accountsRepository.findByCustomerId(customer.get().getCustomerId()));
        if(accounts.isEmpty()) {
            throw new ResourceNotFoundException("Account", "MobileNumber", customer.get().getCustomerId().toString());
        }
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer.get(), new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts.get(), new AccountsDto()));
        System.out.println(customerDto.toString());
        return customerDto;
    }



}
