package io.github.reconsolidated.zpibackend.application;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.infrastracture.currentUser.CurrentUser;
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
}
