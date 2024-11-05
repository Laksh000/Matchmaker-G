package com.learn.matchmaking.dto;

import com.learn.matchmaking.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    private String id;
    private String name;
    private Map<String, Object> attributes;
    private Boolean isSearchingForMatch;

    public PlayerDTO(Player player) {
        super();
        this.id = player.getId();
        this.name = player.getName();
        this.attributes = player.getAttributes();
        this.isSearchingForMatch = player.getIsSearchingForMatch();
    }
}
