package org.example.taskmanager_authservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChangePasswordRequest {
   private String token;
   private String oldPassword;
   private String newPassword;


}
