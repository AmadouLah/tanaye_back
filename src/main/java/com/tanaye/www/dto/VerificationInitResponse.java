package com.tanaye.www.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationInitResponse {
    private String email;
    private String code;
    private String expiresAt; // ISO string
}
