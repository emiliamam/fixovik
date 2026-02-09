package ru.rutmiit.services;

import ru.rutmiit.dto.UserRegistrationDto;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.models.enums.UserRoles;

public interface AuthService {
    void register(UserRegistrationDto registrationDTO);
    User registerMaster(String email, String fullName, String rawPassword, UserRoles role);

    User getUser(String username);
}
