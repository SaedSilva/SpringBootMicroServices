package br.dev.saed.userserviceapi.service;

import br.dev.saed.userserviceapi.mapper.UserMapper;
import br.dev.saed.userserviceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import models.exceptions.ResourceNotFoundException;
import models.reponses.UserResponse;
import models.requests.CreateUserRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse findById(final String id) {
        return userMapper.fromEntity(userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + UserResponse.class.getSimpleName())));
//        return userRepository.findById(id).orElse(null);
    }

    public void save(CreateUserRequest createUserRequest) {
        verifyIfEmailAllreadyExists(createUserRequest.email(), null);
        userRepository.save(userMapper.fromRequest(createUserRequest));
    }

    private void verifyIfEmailAllreadyExists(final String email, final String id) {
        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException("Email already exists: " + email);
                });
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::fromEntity)
                .toList();
    }
}
