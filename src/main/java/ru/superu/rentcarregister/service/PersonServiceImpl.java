package ru.superu.rentcarregister.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.superu.rentcarregister.dto.NewPersonDto;
import ru.superu.rentcarregister.dto.PersonDto;
import ru.superu.rentcarregister.mapper.PersonMapper;
import ru.superu.rentcarregister.model.Person;
import ru.superu.rentcarregister.repository.PersonDao;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonDao personDao;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public PersonDto register(NewPersonDto newPersonDto) {
        newPersonDto.setPassword(encoder.encode(newPersonDto.getPassword()));
        final Person person = personDao.save(PersonMapper.newPersonDtoToPerson(newPersonDto));

        log.info("Successful Person registration with email: {}", person.getEmail());

        return PersonMapper.personToPersonDto(person);
    }
}
