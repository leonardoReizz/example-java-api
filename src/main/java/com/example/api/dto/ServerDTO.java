package com.example.api.dto;

import com.example.api.validation.ValidationGroups;
import jakarta.validation.constraints.*;

public record ServerDTO(
        @NotBlank(
                groups = ValidationGroups.OnCreate.class,
                message = "name is required"
        )
        @Size(min = 2, max = 100)
        String name,

        @NotBlank(
                groups = ValidationGroups.OnCreate.class,
                message = "ip is required"
        )
        @Pattern(
                regexp = "^(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)){3}$",
                message = "Invalid IP"
        )
        String ip,

        @NotNull(
                groups = ValidationGroups.OnCreate.class,
                message = "port is required"
        )
        @Min(1) @Max(65535)
        Integer port,

        @NotBlank(
                groups = ValidationGroups.OnCreate.class,
                message = "username is required"
        )
        String username,

        @NotBlank(
                groups = ValidationGroups.OnCreate.class,
                message = "password is required"
        )
        String password
    ) { }

