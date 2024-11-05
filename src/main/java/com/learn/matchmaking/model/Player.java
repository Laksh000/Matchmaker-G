package com.learn.matchmaking.model;

import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.dto.PlayerDTO;
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
    private Boolean isSearchingForMatch;

    public Player(PlayerBasicDTO playerBasicDTO) {
        super();
        this.id = playerBasicDTO.getId();
        this.name = playerBasicDTO.getName();
        this.attributes = playerBasicDTO.getAttributes();
    }

    public Player(PlayerDTO playerDTO) {
        super();
        this.id = playerDTO.getId();
        this.name = playerDTO.getName();
        this.attributes = playerDTO.getAttributes();
        this.isSearchingForMatch = playerDTO.getIsSearchingForMatch();
    }
}
