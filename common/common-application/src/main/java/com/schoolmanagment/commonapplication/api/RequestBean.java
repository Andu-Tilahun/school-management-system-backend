package com.schoolmanagment.commonapplication.api;


import org.springframework.stereotype.Component;

@Component
public class RequestBean {

    private String correlationId;
    private String csrfToken;
    private String userId;

    private String clientServiceAccessToken;

    public RequestBean(){
    }

    public void initialize(String correlationId,String userId, String csrfToken) {
        this.correlationId = correlationId;
        this.userId = userId;
        this.csrfToken = csrfToken;
    }

    public void setClientServiceAccessToken(String clientServiceAccessToken) {
        this.clientServiceAccessToken = clientServiceAccessToken;
    }

    public String getClientServiceAccessToken() {
        return clientServiceAccessToken;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public String getUserId() {
        return userId;
    }
}
