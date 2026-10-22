package com.intrenship.SmartContractMonitor.mapper;

import com.intrenship.SmartContractMonitor.dtos.response.UserResponse;
import com.intrenship.SmartContractMonitor.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user){

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;

    }
}
