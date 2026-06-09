package com.example.api.dto;

import com.example.api.model.Server;

import java.util.List;

public record ServerListResponse(List<Server> servers) {
}
