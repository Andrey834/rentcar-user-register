package ru.superu.rentcarregister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.superu.rentcarregister.model.Person;

import java.util.UUID;

@Repository
public interface PersonDao extends JpaRepository<Person, UUID> {
}
