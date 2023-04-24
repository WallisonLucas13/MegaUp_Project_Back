package com.example.MegaUp_Server.security.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserViewModel {

    public UserViewModel(String username, String permissions){
        this.username = username;
        this.permissions = permissions;
    }

    private String username;

    private String permissions;
}
