package com.learn.matchmaking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Player")
public class Player {

    @Id
    private String id;
    private String name;
    private Map<String, Object> attributes;
}
