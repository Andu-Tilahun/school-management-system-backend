package com.schoolmanagment.commonapplication.payment;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseEvent {
    private UUID paymentRequestId;

}
