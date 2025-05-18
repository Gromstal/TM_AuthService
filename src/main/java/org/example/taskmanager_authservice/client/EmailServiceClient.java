package org.example.taskmanager_authservice.client;


import org.example.taskmanager_authservice.dto.request.VerificationTokenRequest;
import org.example.taskmanager_authservice.dto.response.RegistrationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "EmailNotificationClient",
        url = "${app.notify.url}"
)
public interface EmailServiceClient {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    RegistrationResponse sendRegistrationEmail(@RequestBody VerificationTokenRequest verificationTokenRequest);

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    RegistrationResponse sendPasswordResetEmail(@RequestBody VerificationTokenRequest verificationTokenRequest);
}
