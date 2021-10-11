package com.amigoscode.testing.service;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.amigoscode.testing.exception.CustomerAlreadyExistsException;
import com.amigoscode.testing.repository.CustomerRepository;
import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository,
                                       PhoneNumberValidator phoneNumberValidator) {
        this.customerRepository = customerRepository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {

        // 1.1 Check if phone number is valid
        // 1.2 check if phone number is already taken
        // 2 if yes, check if belongs to same customer
        //  - 2.1 if yes, return
        //  - 2.2 if no, throw an exception
        // 3. Save customer

        String phoneNumber = request.getCustomer().getPhoneNumber();

        if (!phoneNumberValidator.test(phoneNumber)) {
            throw new IllegalStateException("Phonenumber: " + phoneNumber + " is not valid");
        }

        Optional<Customer> optionalCustomer = customerRepository
                .selectCustomerByPhoneNumber(request.getCustomer()
                        .getPhoneNumber());

        if (optionalCustomer.isPresent()) {
            if (optionalCustomer.get().getName().equals(request.getCustomer().getName())) {
                return;
            }
            throw new CustomerAlreadyExistsException(String.format("Phonenumber: [%s] already exists",
                    request.getCustomer().getPhoneNumber()));
        }

        if (request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }

        customerRepository.save(request.getCustomer());
    }


}
