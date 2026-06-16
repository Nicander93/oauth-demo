package com.demo.manual.auth.repository;

import com.demo.manual.auth.model.PortalApp;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PortalAppRepository {

    private final List<PortalApp> apps = List.of(
            new PortalApp("系统 A", "http://localhost:8060/login/oauth"),
            new PortalApp("系统 B", "http://localhost:8061/login/oauth"),
            new PortalApp("系统 BFF", "http://localhost:8070/login/oauth")
    );

    public List<PortalApp> findAll() {
        return apps;
    }
}
