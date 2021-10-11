package com.amigoscode.testing.utils;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setup() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({"+31123456789, true",
                "+311234567891, false",
                "311234567891, false"})
    void itShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
        // when
        boolean isValid = underTest.test(phoneNumber);
        // then
        assertThat(isValid).isEqualTo(expected);

    }
}
