package ru.superu.rentcarregister.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Size(min = 3, max = 50)
    @Column(length = 50, unique = true)
    private String username;
    @Size(min = 8)
    @Column(length = 1000)
    private String password;
    @Size(min = 2, max = 50)
    @Column(length = 50)
    private String firstName;
    @Size(min = 2, max = 50)
    @Column(length = 50)
    private String lastName;
    @Email
    @Column(length = 300, unique = true)
    @NotBlank
    private String email;
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(length = 50, nullable = false)
    private LocalDate birthday;
    @Column(updatable = false)
    private LocalDateTime created;

    @PrePersist
    private void createdAt() {
        this.created = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id)
               && Objects.equals(username, person.username)
               && Objects.equals(password, person.password)
               && Objects.equals(firstName, person.firstName)
               && Objects.equals(lastName, person.lastName)
               && Objects.equals(email, person.email)
               && Objects.equals(birthday, person.birthday)
               && Objects.equals(created, person.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, firstName, lastName, email, birthday, created);
    }
}
