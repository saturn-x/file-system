package com.example.file.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails {



    private User userPrincipal;

    private List<String> permissions;

//    @JsonIgnore
    private List<SimpleGrantedAuthority> collect;

    public LoginUser(User user,List<String> permissions) {
        this.userPrincipal = user;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 防止调用多次
        if(collect != null) {
            return collect;
        }
        collect = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public String getPassword() {
        return userPrincipal.getPassword();
    }

    @Override
    public String getUsername() {
        return userPrincipal.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}