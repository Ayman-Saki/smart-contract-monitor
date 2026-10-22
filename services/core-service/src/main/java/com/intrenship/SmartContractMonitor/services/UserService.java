package com.intrenship.SmartContractMonitor.services;

import com.intrenship.SmartContractMonitor.dtos.request.LoginRequest;
import com.intrenship.SmartContractMonitor.dtos.response.LoginResponse;
import com.intrenship.SmartContractMonitor.dtos.response.UserResponse;
import com.intrenship.SmartContractMonitor.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    LoginResponse login(LoginRequest request);
    Optional<User> findByEmail(String email);
    User findById(Long id);

}
