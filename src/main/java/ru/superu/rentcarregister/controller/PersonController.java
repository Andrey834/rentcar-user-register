package ru.superu.rentcarregister.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.superu.rentcarregister.dto.NewPersonDto;
import ru.superu.rentcarregister.dto.PersonDto;
import ru.superu.rentcarregister.service.PersonService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/register")
@CrossOrigin(origins = "http://192.168.0.183:4200", methods = RequestMethod.POST, maxAge = 3600)
public class PersonController {
    private final PersonService service;

    @PostMapping
    public ResponseEntity<PersonDto> register(@Valid @RequestBody NewPersonDto newPersonDto,
                                              HttpServletRequest request) {

        PersonDto personDto = service.register(newPersonDto);

        log.info("{} for {} with PERSON - IP:{} Email:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                newPersonDto.getEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(personDto);
    }
}
