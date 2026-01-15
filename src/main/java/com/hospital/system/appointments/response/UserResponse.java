package com.hospital.system.appointments.response;

import com.hospital.system.appointments.entity.Authority;

import java.util.List;

public class UserResponse {

    private long id;

    private String email;

    private List<Authority> authorities;

    public UserResponse(long id, String email, List<Authority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
