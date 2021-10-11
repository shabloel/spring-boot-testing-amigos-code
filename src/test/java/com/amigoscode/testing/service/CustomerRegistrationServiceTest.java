package com.amigoscode.testing.service;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.amigoscode.testing.exception.CustomerAlreadyExistsException;
import com.amigoscode.testing.repository.CustomerRepository;
import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    //Because customerRepository is already tested we can now Mock it.
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    //another way to Mock the CustomerRepository. remove the MockitoAnnotations.initMocks(this); from setUp()
    //private CustomerRepository customerRepository = mock(CustomerRepository.class);

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        //before each test we are having a fresh new instance of CustomerRegistrationService
        underTest = new CustomerRegistrationService(customerRepository, phoneNumberValidator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // given
        String phoneNumber = "06123456789";
        Customer customer = Customer.builder()
                                .id(UUID.randomUUID())
                                .name("James Last")
                                .phoneNumber(phoneNumber)
                                .build();

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        // when
        underTest.registerNewCustomer(customerRegistrationRequest);

        // then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // given
        String phoneNumber = "06123456789";
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("James Last")
                .phoneNumber(phoneNumber)
                .build();

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        // when
        underTest.registerNewCustomer(customerRegistrationRequest);

        // then
        then(customerRepository).should(never()).save(any());
        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
        //after running selectCustomerByPhoneNumber Mock should have no more interactions
        //then(customerRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrowWhenPhoneNumberAlreadyExists() {
        // given
        String phoneNumber = "06123456789";
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("James Last")
                .phoneNumber(phoneNumber)
                .build();

        Customer customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .name("Peppa Pig")
                .phoneNumber(phoneNumber)
                .build();

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer2));
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        // when


        // then
        assertThatThrownBy(() -> underTest.registerNewCustomer(customerRegistrationRequest))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining(String.format("Phonenumber: [%s] already exists", customerRegistrationRequest.getCustomer().getPhoneNumber()));

        //Finally
        then(customerRepository).should(never()).save(any());

    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // given
        String phoneNumber = "06123456789";
        Customer customer = Customer.builder()
                .id(null)
                .name("James Last")
                .phoneNumber(phoneNumber)
                .build();

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);
        // when
        underTest.registerNewCustomer(customerRegistrationRequest);

        // then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptor.getValue().getId()).isNotNull();
    }

    @Test
    void itShouldThrowIllegalStateExceptionWhenPhoneNotValid() {
        // given
        String phoneNumber = "06123456789";
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("James Last")
                .phoneNumber(phoneNumber)
                .build();

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        given(phoneNumberValidator.test(phoneNumber)).willReturn(false);

        // then
        assertThatThrownBy(() -> underTest.registerNewCustomer(customerRegistrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phonenumber: %s is not valid", phoneNumber));

        //Finally
        then(customerRepository).shouldHaveNoInteractions();
    }


}