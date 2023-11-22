package io.github.reconsolidated.zpibackend.authentication.appUser;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;


@Service
@Validated
public class AppUserService {

    private static final String USER_NOT_FOUND_MESSAGE =
            "user with email %s not found";
    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Optional<AppUser> findUserById(Long appUserId) {
        return appUserRepository.findById(appUserId);
    }

    public Optional<AppUser> findUserByEmail(String inviteeEmail) {
        return appUserRepository.findByEmail(inviteeEmail);
    }

    public AppUser getOrCreateUser(String keycloakId, String email, String firstName, String lastName) {
        return appUserRepository.findByKeycloakId(keycloakId).orElseGet(() -> {
            if (appUserRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("User with email " + email + " already exists");
            }
            AppUser appUser = new AppUser();
            appUser.setKeycloakId(keycloakId);
            appUser.setEmail(email);
            appUser.setFirstName(firstName);
            appUser.setLastName(lastName);
            appUser.setNickname(firstName + " " + lastName);

            return appUserRepository.save(appUser);
        });
    }

    public void setFirstName(AppUser user, String name) {
        user.setFirstName(name);
        appUserRepository.save(user);
    }

    public void setLastName(AppUser user, String name) {
        user.setLastName(name);
        appUserRepository.save(user);
    }

    public AppUser getUser(Long appUserId) {
        return appUserRepository.findById(appUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    public void deleteUser(AppUser user) {
        appUserRepository.delete(user);
    }

}
