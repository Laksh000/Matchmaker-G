package com.learn.matchmaking.dto;

import com.learn.matchmaking.model.Users;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {

    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public UsersDTO(Users user) {

        super();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
