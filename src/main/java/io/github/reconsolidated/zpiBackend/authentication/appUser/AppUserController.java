package io.github.reconsolidated.zpiBackend.authentication.appUser;

import io.github.reconsolidated.zpiBackend.authentication.currentUser.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class AppUserController {
    private final AppUserService appUserService;

    @PostMapping("/first_name")
    public ResponseEntity<?> setFirstName(@CurrentUser AppUser user,
                                          @RequestParam String name) {
        appUserService.setFirstName(user, name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/last_name")
    public ResponseEntity<AppUser> setLastName(@CurrentUser AppUser user, @RequestParam String name) {
        appUserService.setLastName(user, name);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/image_url")
    public ResponseEntity<AppUser> setImageUrl(@CurrentUser AppUser user, @RequestParam String imageUrl) {
        appUserService.setImageUrl(user, imageUrl);
        return ResponseEntity.ok(user);
    }

    // TODO remove before going to production
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@CurrentUser AppUser user) {
        appUserService.deleteUser(user);
        return ResponseEntity.ok().build();
    }
}
