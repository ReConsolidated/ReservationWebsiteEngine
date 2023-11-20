package io.github.reconsolidated.zpibackend.authentication.appUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AppUserServiceTest {
    @Autowired
    private AppUserService appUserService;

    @Test
    @Transactional
    public void testCreateUser() {
        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");

        assertThat(appUserService.findUserById(user.getId())).isPresent();
        assertThat(appUserService.getUser(user.getId()).getKeycloakId()).isEqualTo(keycloakId);
    }

//    @Test
//    @Transactional
//    public void testSetImageUrl() {
//        final String imageUrl = "a.pl";
//        final String keycloakId = "unique_id";
//        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");
//
//        appUserService.setImageUrl(user, imageUrl);
//
//        assertThat(appUserService.getUser(user.getId()).getImageUrl()).isEqualTo(imageUrl);
//    }

    @Test
    @Transactional
    public void testSetFirstName() {
        final String name = "Tomek";
        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");

        appUserService.setFirstName(user, name);

        assertThat(appUserService.getUser(user.getId()).getFirstName()).isEqualTo(name);
    }

    @Test
    @Transactional
    public void testSetLastName() {
        final String name = "Tomek";
        final String keycloakId = "unique_id";
        AppUser user = appUserService.getOrCreateUser(keycloakId, "any@any.com", "name", "lastname");

        appUserService.setLastName(user, name);

        assertThat(appUserService.getUser(user.getId()).getLastName()).isEqualTo(name);
    }
}
