package ru.rutmiit.services;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.repositories.MasterRepository;
import ru.rutmiit.repositories.UserRepository;

public interface CurrentUserService{
    public String getCurrentBrigadierId();
    public String getCurrentBrigadierByEmail();

}