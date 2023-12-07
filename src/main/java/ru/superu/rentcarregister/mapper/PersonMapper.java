package ru.superu.rentcarregister.mapper;

import ru.superu.rentcarregister.dto.NewPersonDto;
import ru.superu.rentcarregister.dto.PersonDto;
import ru.superu.rentcarregister.model.Person;

public class PersonMapper {
    public static Person newPersonDtoToPerson(NewPersonDto newPersonDto) {
        return Person.builder()
                .username(newPersonDto.getUsername())
                .password(newPersonDto.getPassword())
                .firstName(newPersonDto.getFirstName())
                .lastName(newPersonDto.getLastName())
                .email(newPersonDto.getEmail())
                .birthday(newPersonDto.getBirthday())
                .build();
    }

    public static PersonDto personToPersonDto(Person person) {
        return PersonDto.builder()
                .username(person.getUsername())
                .email(person.getEmail())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .birthday(person.getBirthday().toString())
                .build();
    }
}
