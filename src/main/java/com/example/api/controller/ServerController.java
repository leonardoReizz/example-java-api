package com.example.api.controller;

import com.example.api.dto.ServerDTO;
import com.example.api.dto.ServerListResponse;
import com.example.api.model.Server;
import com.example.api.service.ServerService;
import com.example.api.validation.ValidationGroups;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servers")
public class ServerController
{
    private final ServerService service;

    public ServerController(ServerService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Server findById(@Validated @UUID @PathVariable String id) {
        return this.service.findById(id);
    }

    @GetMapping("")
    public ResponseEntity<ServerListResponse> findAll() {
        return ResponseEntity.ok(new ServerListResponse(this.service.findAll()));
    }

    @PostMapping("")
    public Server create(@Validated(ValidationGroups.OnCreate.class) @RequestBody ServerDTO server) {
        return this.service.create(server);
    }

    @DeleteMapping("/{id}")
    public void delete(@Validated @UUID @PathVariable String id) {
        this.service.delete(id);
    }

    @PatchMapping("/{id}")
    public Server update(@Validated @UUID @PathVariable String id, @Validated @RequestBody ServerDTO dto) {
        return this.service.update(id, dto);
    }

}
