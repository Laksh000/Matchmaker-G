package com.learn.matchmaking.model;

import com.learn.matchmaking.dto.UsersDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "User")
public class Users {

    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;

    public Users(UsersDTO user) {

        super();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
