package com.schoolmanagment.coreservice.client;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InternalUserRegisterRequest {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private List<String> policyNames;
    private UUID externalId;
}
