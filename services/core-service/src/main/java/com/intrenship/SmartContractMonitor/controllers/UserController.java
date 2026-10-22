package com.intrenship.SmartContractMonitor.controllers;

import com.intrenship.SmartContractMonitor.dtos.request.LoginRequest;
import com.intrenship.SmartContractMonitor.dtos.response.LoginResponse;
import com.intrenship.SmartContractMonitor.dtos.response.UserResponse;
import com.intrenship.SmartContractMonitor.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return userService.login(request);
    }
}
