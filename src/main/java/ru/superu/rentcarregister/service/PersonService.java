package ru.superu.rentcarregister.service;

import ru.superu.rentcarregister.dto.NewPersonDto;
import ru.superu.rentcarregister.dto.PersonDto;

public interface PersonService {
    PersonDto register(NewPersonDto newPersonDto);
}
