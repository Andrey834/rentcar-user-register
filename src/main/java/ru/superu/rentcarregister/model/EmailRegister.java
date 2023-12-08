package ru.superu.rentcarregister.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmailRegister {
    @Email
    private String to;
    @NotBlank
    private String subject;
    @NotBlank
    private String text;
}
