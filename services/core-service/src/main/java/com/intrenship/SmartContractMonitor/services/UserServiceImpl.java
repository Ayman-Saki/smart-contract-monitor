package com.intrenship.SmartContractMonitor.services;

import com.intrenship.SmartContractMonitor.dtos.request.LoginRequest;
import com.intrenship.SmartContractMonitor.dtos.response.LoginResponse;
import com.intrenship.SmartContractMonitor.dtos.response.UserResponse;
import com.intrenship.SmartContractMonitor.entities.User;
import com.intrenship.SmartContractMonitor.mapper.UserMapper;
import com.intrenship.SmartContractMonitor.repositories.UserRepository;
import com.intrenship.SmartContractMonitor.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid email or password"
                                )
                        );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateToken(user);

        return new LoginResponse(
                token,
                userMapper.toResponse(user)
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
