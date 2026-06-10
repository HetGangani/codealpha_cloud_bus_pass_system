package com.het.buspasssystem.repository;

import com.het.buspasssystem.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Integer> {
}
