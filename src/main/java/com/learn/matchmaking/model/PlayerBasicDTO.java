package com.learn.matchmaking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerBasicDTO {

    private String id;
    private String name;
    private Map<String, Object> attributes;

    public PlayerBasicDTO(Player player) {
        super();
        this.id = player.getId();
        this.name = player.getName();
        this.attributes = player.getAttributes();
    }
}
