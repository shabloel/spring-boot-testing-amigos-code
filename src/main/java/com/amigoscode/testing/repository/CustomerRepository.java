package com.amigoscode.testing.repository;

import com.amigoscode.testing.customer.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    @Query(
            value = "select id, name, phone_number " +
                    "from customer where phone_number = :phone_number",
            nativeQuery = true //nativeQuery makes sure customer does not refer to the Customer class.
    )
    Optional<Customer> selectCustomerByPhoneNumber(@Param("phone_number") String phoneNumber);
}
