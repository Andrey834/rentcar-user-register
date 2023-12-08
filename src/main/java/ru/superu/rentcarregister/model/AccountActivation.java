package ru.superu.rentcarregister.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder(toBuilder = true)
public class AccountActivation {
    @Id
    @Column(name = "person_id", unique = true)
    private UUID personId;
    @Column(unique = true)
    private String email;
    private String emailActivationCode;
    @Column(updatable = false)
    private LocalDateTime timeActivation;
    private boolean active;

    @PrePersist
    private void activationAt() {
        this.timeActivation = LocalDateTime.now();
    }
}
