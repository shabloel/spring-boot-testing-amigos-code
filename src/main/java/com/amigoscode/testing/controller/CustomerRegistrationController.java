package com.amigoscode.testing.controller;


import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.amigoscode.testing.service.CustomerRegistrationService;
import org.springframework.web.bind.annotation.*;

//makes sure the request coming in is valid. That is, it needs to have all the non-null values in the Customer class
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/customer-registration")
public class CustomerRegistrationController {

    private final CustomerRegistrationService customerRegistrationService;

    public CustomerRegistrationController(CustomerRegistrationService customerRegistrationService) {
        this.customerRegistrationService = customerRegistrationService;
    }

    @PutMapping
    public void registerNewCustomer(@RequestBody CustomerRegistrationRequest request){
        customerRegistrationService.registerNewCustomer(request);
    }
}
