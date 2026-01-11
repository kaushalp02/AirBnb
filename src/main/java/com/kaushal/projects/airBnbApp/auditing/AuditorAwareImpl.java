package com.kaushal.projects.airBnbApp.auditing;

import com.kaushal.projects.airBnbApp.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@NoArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Check if Authentication object exists and is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
           return Optional.of("No User");
        }

        Object principal = authentication.getPrincipal();

        User user;

        if (principal instanceof User)
            user = (User) principal;
        else
            user = null;

        if (user != null)
            return Optional.of(user.getEmail());
        else
            return Optional.of("No User");
    }
}
