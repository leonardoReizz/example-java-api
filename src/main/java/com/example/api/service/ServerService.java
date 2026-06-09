package com.example.api.service;

import com.example.api.dto.ServerDTO;
import com.example.api.exception.NotFoundException;
import com.example.api.model.Server;
import com.example.api.repository.ServerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServerService {

    private final ServerRepository repository;

    public ServerService(ServerRepository repository) {
        this.repository = repository;
    }

    public List<Server> findAll() {
        return this.repository.findAll();
    }

    public Server findById(String id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Server not found"));
    }

    public Server create(ServerDTO server) {
        Server serverCreate = new Server();

        serverCreate.setName(server.name());
        serverCreate.setIp(server.ip());
        serverCreate.setUsername(server.username());
        serverCreate.setPassword(server.password());
        serverCreate.setPort(server.port());
        serverCreate.setCreatedAt(LocalDateTime.now());
        serverCreate.setUpdatedAt(LocalDateTime.now());

        return this.repository.save(serverCreate);
    }

    public void delete(String id) {
        Server server = this.repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Server not found"));

        this.repository.delete(server);
    }

    public Server update(String id, ServerDTO dto) {
        Server currentServer = this.findById(id);

        currentServer.setName(dto.name());
        currentServer.setIp(dto.ip());
        currentServer.setUsername(dto.username());
        currentServer.setPassword(dto.password());
        currentServer.setPort(dto.port());
        currentServer.setUpdatedAt(LocalDateTime.now());

        return this.repository.save(currentServer);
    }
}
