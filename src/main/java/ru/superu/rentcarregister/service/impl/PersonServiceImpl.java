package ru.superu.rentcarregister.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.superu.rentcarregister.dto.NewPersonDto;
import ru.superu.rentcarregister.dto.PersonDto;
import ru.superu.rentcarregister.mapper.PersonMapper;
import ru.superu.rentcarregister.model.AccountActivation;
import ru.superu.rentcarregister.model.EmailRegister;
import ru.superu.rentcarregister.model.Person;
import ru.superu.rentcarregister.repository.ActivationDao;
import ru.superu.rentcarregister.repository.PersonDao;
import ru.superu.rentcarregister.service.EmailService;
import ru.superu.rentcarregister.service.PersonService;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:values.properties")
public class PersonServiceImpl implements PersonService {
    private final PersonDao personDao;
    private final ActivationDao activationDao;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    @Value("${host}")
    private String url;

    @Override
    @Transactional(rollbackFor = {MessagingException.class})
    public PersonDto register(NewPersonDto newPersonDto) {
        newPersonDto.setPassword(encoder.encode(newPersonDto.getPassword()));
        final Person person = personDao.save(PersonMapper.newPersonDtoToPerson(newPersonDto));

        final String confirmation = confirmationString();

        activationDao.save(AccountActivation.builder()
                .personId(person.getId())
                .email(person.getEmail())
                .emailActivationCode(confirmation)
                .build()
        );

        try {
            emailService.sendWithHtml(EmailRegister.builder()
                    .to(person.getEmail())
                    .subject("account activation")
                    .text(getLink(confirmation, person.getEmail()))
                    .build());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


        log.info("Successful Person registration with email: {}", person.getEmail());

        return PersonMapper.personToPersonDto(person);
    }

    private String confirmationString() {
        return new Random()
                .ints(30, 33, 122)
                .collect(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append
                )
                .toString();
    }

    private String getLink(String confirmation, String email) {
        return String.format("<a href=\"" + url + "/persons/active/%s/%s\">ACTIVE</a>",
                email,
                confirmation
        );
    }
}
