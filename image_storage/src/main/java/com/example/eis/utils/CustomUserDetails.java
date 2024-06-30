package com.example.eis.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Long userId;

    public CustomUserDetails(Long userId) {
        this.userId = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // Non forniamo autorizzazioni in questo esempio
    }

    @Override
    public String getPassword() {
        return null; // Non forniamo una password in questo esempio
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Consideriamo l'account sempre valido
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Consideriamo l'account sempre sbloccato
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Consideriamo le credenziali sempre valide
    }

    @Override
    public boolean isEnabled() {
        return true; // Consideriamo l'utente sempre abilitato
    }
}
