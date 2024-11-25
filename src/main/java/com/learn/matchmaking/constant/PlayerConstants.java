package com.learn.matchmaking.constant;

public final class PlayerConstants {

    public static final String SAVE_SUCCESS_MESSAGE = "Player registration successful";
    public static final String SAVE_FAILURE_MESSAGE = "Player registration failed for the following players: ";
    public static final String UPDATE_SUCCESS_MESSAGE = "Player update successful";
    public static final String UPDATE_FAILURE_MESSAGE = "Update failed for the following players as they were not found: ";
    public static final String UPDATE_FAILURE_MESSAGE2 = "Update failed for %s players, as player id's were not found in the request";
    public static final String DELETE_SUCCESSFUL_MESSAGE = "Player deletion successful";
    public static final String DELETE_FAILURE_MESSAGE = "Player deletion failed for the following players: ";

    private PlayerConstants() {
    }
}
