package com.dmitring.trippledata.controllers;

import com.dmitring.trippledata.domain.Client;
import com.dmitring.trippledata.exceptions.ClientAlreadyExistsException;
import com.dmitring.trippledata.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public UUID registerClient(@RequestBody UUID clientId) {
        final Client existedClient = clientRepository.findOne(clientId);
        if (existedClient != null)
            throw new ClientAlreadyExistsException(clientId);

        return clientRepository.save(new Client(clientId)).getId();
    }
}
