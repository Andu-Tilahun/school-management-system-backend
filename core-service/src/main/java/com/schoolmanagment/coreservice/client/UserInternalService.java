package com.schoolmanagment.coreservice.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolmanagment.commonapplication.exception.BadRequestException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInternalService {

    @Value("${sms.default-password:ChangeMe123!}")
    private String defaultPassword;

    private final UserInternalClient userInternalClient;
    private final ObjectMapper objectMapper;

    public UserDto getCurrentUser(UUID userId) {
        return userInternalClient.getUserById(userId).getData();
    }

    public void createUser(InternalUserRegisterRequest userRegisterRequest) {

        if (defaultPassword == null || defaultPassword.isBlank()) {
            throw new BadRequestException("Default password is not configured");
        }

        userRegisterRequest.setPassword(setDefaultPassword());

        try {
            userInternalClient.register(userRegisterRequest);
        } catch (FeignException ex) {
            throw new BadRequestException(userServiceMessage(ex));
        }
    }

    private String setDefaultPassword() {
        return defaultPassword;
    }

    private String userServiceMessage(FeignException ex) {
        String body = ex.contentUTF8();
        if (body != null && !body.isBlank()) {
            try {
                JsonNode message = objectMapper.readTree(body).path("message");
                if (message.isTextual() && !message.asText().isBlank()) {
                    return message.asText();
                }
            } catch (Exception ignored) {
                // Fall through to the generic message.
            }
        }
        return "Could not create the account";
    }
}
