package com.demo.bff.controller;

import com.demo.bff.model.ClientLoginSession;
import com.demo.bff.model.MeResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthApiController {

    public static final String SESSION_LOGIN = "CLIENT_LOGIN";

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(HttpSession session) {
        ClientLoginSession login = (ClientLoginSession) session.getAttribute(SESSION_LOGIN);
        if (login == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(toResponse(login));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    static MeResponse toResponse(ClientLoginSession login) {
        return new MeResponse(
                new MeResponse.OidcUserView(
                        login.oidcUser().sub(),
                        login.oidcUser().username(),
                        login.oidcUser().name()),
                new MeResponse.LocalUserView(
                        login.localUser().userCode(),
                        login.localUser().username(),
                        login.localUser().nickname()),
                login.accessToken(),
                login.idToken());
    }
}
