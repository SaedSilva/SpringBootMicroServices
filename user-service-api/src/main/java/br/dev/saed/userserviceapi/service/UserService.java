package br.dev.saed.userserviceapi.service;

import br.dev.saed.userserviceapi.entity.User;
import br.dev.saed.userserviceapi.mapper.UserMapper;
import br.dev.saed.userserviceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import models.exceptions.ResourceNotFoundException;
import models.reponses.UserResponse;
import models.requests.CreateUserRequest;
import models.requests.UpdateUserRequest;
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
        return userMapper.fromEntity(find(id));
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

    public UserResponse update(String id, UpdateUserRequest updateUserRequest) {
        User entity = find(id);
        verifyIfEmailAllreadyExists(updateUserRequest.email(), id);
        return userMapper.fromEntity(userRepository.save(userMapper.update(updateUserRequest, entity)));
    }

    private User find (final String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + UserResponse.class.getSimpleName()));
    }
}
