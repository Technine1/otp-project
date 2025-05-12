package ru.model;

import java.util.Objects;

public final class User extends Identity<String> {
    private String login;
    private String password;
    private String email;
    private String phone;
    private String telegram;
    private Role role;

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        this.telegram = telegram;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return Objects.equals(this.login, that.login) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.phone, that.phone) &&
                Objects.equals(this.telegram, that.telegram) &&
                Objects.equals(this.role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password, email, phone, telegram, role);
    }

}
