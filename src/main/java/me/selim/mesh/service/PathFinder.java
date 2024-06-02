package me.selim.mesh.service;

import me.selim.mesh.domain.Route;

public interface PathFinder {
    Route findOptimalRoute(Long start, Long end);
}
