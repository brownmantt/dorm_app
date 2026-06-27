package com.dom.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ログインユーザー設定（application.yml dom.auth.users）。
 */
@Component
@ConfigurationProperties(prefix = "dom.auth")
public class AuthProperties {

    private List<UserEntry> users = new ArrayList<>();

    public List<UserEntry> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntry> users) {
        this.users = users == null ? new ArrayList<>() : users;
    }

    public static class UserEntry {

        private String username;
        private String password;
        private List<String> roles = new ArrayList<>();

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles == null ? new ArrayList<>() : roles;
        }
    }
}
