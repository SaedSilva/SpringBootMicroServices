package br.dev.saed.userserviceapi.controller.impl;

import br.dev.saed.userserviceapi.controller.UserController;
import br.dev.saed.userserviceapi.entity.User;
import br.dev.saed.userserviceapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<User> findById(String id) {
        return ResponseEntity.ok().body(userService.findById(id));
    }
}
