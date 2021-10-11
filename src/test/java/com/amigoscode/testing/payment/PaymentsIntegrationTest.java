package com.amigoscode.testing.payment;


import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.amigoscode.testing.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentsIntegrationTest {

    //actually only mockMvc is allowed
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void it_should_create_payment_succesfully() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Henk", "+31123456789");
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);
        long paymentId = 1L;
        Payment payment = new Payment(
                paymentId,
                id,
                new BigDecimal("10.00"),
                Currency.USD,
                "VISA",
                "New Shoes"
        );

        PaymentRequest paymentRequest = new PaymentRequest(payment);

        ResultActions resultActionsCustomer = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(customerRegistrationRequest)));

        ResultActions resultActionsPayment = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/payment-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(paymentRequest)));

        // then
        resultActionsCustomer.andExpect(status().isOk());
        resultActionsPayment.andExpect(status().isOk());

        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(
                        p -> assertThat(p)
                                .isEqualToComparingFieldByField(payment));
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to Json");
            return null;
        }
    }
}
