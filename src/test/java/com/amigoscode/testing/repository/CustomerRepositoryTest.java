package com.amigoscode.testing.repository;

import com.amigoscode.testing.customer.Customer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        } // turn this off for itShouldNotSaveCustomerWhenNameIsNull(). With this turned of the
        // @Column(nullable = false) from customer class gets read by Spring.
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTestCustomerRepo;

    Logger logger = LoggerFactory.getLogger(CustomerRepositoryTest.class);

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // given
        String phoneNumber = "0612345678";
        Customer customerToSave = Customer.builder()
                                        .name("James Last")
                                        .id(UUID.randomUUID())
                                        .phoneNumber(phoneNumber)
                                        .build();

        underTestCustomerRepo.save(customerToSave);

        // when
        Optional<Customer> optionalCustomer = underTestCustomerRepo.selectCustomerByPhoneNumber(phoneNumber);

        //then
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(customerToSave);

                });
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberIfNumberNotExists() {
        // given
        String phoneNumberToSave = "0612345678";
        String phoneNumberToRetrieve = "0611111111";
        Customer customerToSave = Customer.builder()
                .name("James Last")
                .id(UUID.randomUUID())
                .phoneNumber(phoneNumberToSave)
                .build();

        underTestCustomerRepo.save(customerToSave);

        // when
        Optional<Customer> optionalCustomer = underTestCustomerRepo.selectCustomerByPhoneNumber(phoneNumberToRetrieve);

        //then
        assertThat(optionalCustomer).isNotPresent();

        logger.info("*********" + underTestCustomerRepo.findAll());

    }


    @Test
    void itShouldSaveCustomer() {
        // given
        UUID id = UUID.randomUUID();
        Customer customerGiven = Customer.builder()
                                    .id(id)
                                    .name("James Last")
                                    .phoneNumber("0123456789")
                                    .build();
        // when
        underTestCustomerRepo.save(customerGiven);

        // then
        Optional<Customer> optionalCustomer =  underTestCustomerRepo.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                       /* assertThat(c.getId()).isEqualTo(id);
                        assertThat(c.getName()).isEqualTo(customerGiven.getName());
                        assertThat(c.getPhoneNumber()).isEqualTo(customerGiven.getPhoneNumber());*/
                    assertThat(c).isEqualToComparingFieldByField(customerGiven);
                });

    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // given
        UUID id = UUID.randomUUID();
        Customer customerGiven = Customer.builder()
                .id(id)
                .name(null)
                .phoneNumber("0123456789")
                .build();

        //when

        // then
        assertThatThrownBy(() -> underTestCustomerRepo.save(customerGiven))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSafeCustomerWhenPhonNumberIsNull() {
        // given
        UUID id = UUID.randomUUID();
        Customer customerGiven = Customer.builder()
                .id(id)
                .name("James Last")
                .phoneNumber(null)
                .build();

        // when

        // then
        assertThatThrownBy(() -> underTestCustomerRepo.save(customerGiven))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}