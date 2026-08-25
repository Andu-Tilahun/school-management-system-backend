package com.schoolmanagment.commonapplication.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private UUID paymentRequestId;
    private BigDecimal amount;
    private ZonedDateTime dueDate;
    private String fullName;
    private String utility;
    private String email;
    private String phoneNumber;

}
