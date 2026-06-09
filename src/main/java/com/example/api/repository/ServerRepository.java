package com.example.api.repository;

import com.example.api.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, String> {
}
