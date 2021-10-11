package com.amigoscode.testing.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardPaymentCharge {

    private final boolean isCardDebited;

    public CardPaymentCharge(boolean isCardDebited) {
        this.isCardDebited = isCardDebited;
    }

}
