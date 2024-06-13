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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final BCryptPasswordEncoder encoder;

    public UserResponse findById(final String id) {
        return mapper.fromEntity(find(id));
//        return userRepository.findById(id).orElse(null);
    }

    public void save(CreateUserRequest request) {
        verifyIfEmailAllreadyExists(request.email(), null);
        repository.save(repository.save(mapper.fromRequest(request)).withPassword(encoder.encode(request.password())));
    }

    private void verifyIfEmailAllreadyExists(final String email, final String id) {
        repository.findByEmail(email)
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException("Email already exists: " + email);
                });
    }

    public List<UserResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromEntity)
                .toList();
    }

    public UserResponse update(final String id, final UpdateUserRequest request) {
        User entity = find(id);
        verifyIfEmailAllreadyExists(request.email(), id);
        return mapper.fromEntity(repository.save(mapper.update(request, entity)
                .withPassword(request.password() != null ? encoder.encode(request.password()) : entity.getPassword())));
    }

    private User find (final String id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Object not found. Id: " + id + ", Type: " + UserResponse.class.getSimpleName()));
    }
}
