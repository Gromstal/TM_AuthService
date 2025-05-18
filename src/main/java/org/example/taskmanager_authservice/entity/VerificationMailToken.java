package org.example.taskmanager_authservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationMailToken {
    @Id
    private Long id;
    private String token;
    private String email;

    @Builder.Default
    private boolean isUsed=false;
}
