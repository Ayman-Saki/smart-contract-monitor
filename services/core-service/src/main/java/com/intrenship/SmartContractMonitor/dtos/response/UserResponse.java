package com.intrenship.SmartContractMonitor.dtos.response;

import com.intrenship.SmartContractMonitor.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private Role role;

}
