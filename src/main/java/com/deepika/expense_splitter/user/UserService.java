package com.deepika.expense_splitter.user;

import com.deepika.expense_splitter.user.dto.CreateUserRequest;
import com.deepika.expense_splitter.user.dto.UpdateUserRequest;
import com.deepika.expense_splitter.user.dto.UserResponse;
import com.deepika.expense_splitter.exception.DuplicateResourceException;
import com.deepika.expense_splitter.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers()
    {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse createUser(CreateUserRequest request)
    {
        if(userRepository.existsByEmail(request.email()))
        {
            throw new DuplicateResourceException("Email already Registered");
        }
        User user = new User(request.name(),request.email(), request.password());
        User saved = userRepository.save(user);
        return toResponse(saved);
    }
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return toResponse(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        boolean emailChanged = !user.getEmail().equals(request.email());
        if (emailChanged && userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        return toResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
