package ru.rutmiit.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.repositories.MasterRepository;
import ru.rutmiit.repositories.UserRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CurrentUserServiceImpl implements CurrentUserService{

        private final UserRepository userRepository;
        private final MasterRepository masterRepository;

        public CurrentUserServiceImpl(UserRepository userRepository,
                                  MasterRepository masterRepository) {
            this.userRepository = userRepository;
            this.masterRepository = masterRepository;
        }

    @Override
    public String getCurrentBrigadierId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Master master = masterRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalStateException(
                        "User " + username + " is not associated with any master/brigadier"));

        return master.getId();
    }

    public String getCurrentBrigadierByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Master master = masterRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "User " + username + " is not associated with any master/brigadier"));

        return master.getEmail();
    }
}