package com.codetrix.auth.service;

import com.codetrix.auth.entity.User;
import com.codetrix.auth.repository.UserRepository;
import com.codetrix.team.entity.Team;
import com.codetrix.team.entity.TeamStatus;
import com.codetrix.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()))
        );
    }

    public UserDetails loadTeamByTeamCode(String teamCode) throws UsernameNotFoundException {
        Team team = teamRepository.findByTeamCode(teamCode)
                .orElseThrow(() -> new UsernameNotFoundException("Team not found: " + teamCode));

        boolean enabled = team.getStatus() != TeamStatus.DISQUALIFIED;

        return new org.springframework.security.core.userdetails.User(
                team.getTeamCode(),
                team.getLoginPinHash(),
                enabled,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEAM"))
        );
    }
}
