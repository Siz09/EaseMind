package com.mindease.backend.service;

import com.mindease.backend.dto.UserDTO;
import com.mindease.backend.exception.DuplicateResourceException;
import com.mindease.backend.exception.ResourceNotFoundException;
import com.mindease.backend.model.User;
import com.mindease.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Check for existing email
        if (userRepository.existsByEmail(userDTO.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Check for existing username
        if (userRepository.existsByUsername(userDTO.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Convert DTO to Entity
        User user = new User();
        user.setEmail(userDTO.email());
        user.setUsername(userDTO.username());
        user.setPassword(passwordEncoder.encode(userDTO.password()));

        try {
            User savedUser = userRepository.save(user);
            return convertToDTO(savedUser);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("User already exists with given credentials");
        }
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update fields if present in DTO
        Optional.ofNullable(userDTO.email()).ifPresent(existingUser::setEmail);
        Optional.ofNullable(userDTO.username()).ifPresent(existingUser::setUsername);

        if (userDTO.password() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.password()));
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                null, // Never return password
                user.getProfileImageUrl()
        );
    }

    @Transactional
    public void createLocalUser(String firebaseUid, String email) {
        User user = new User();
        user.setFirebaseUid(firebaseUid);
        user.setEmail(email);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserByFirebaseUid(String uid) {
        userRepository.deleteByFirebaseUid(uid);
    }
}