package io.github.reconsolidated.zpiBackend.devTest;

import io.github.reconsolidated.zpiBackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpiBackend.authentication.currentUser.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/dev", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class DevTestController {

    @GetMapping(path = "/is_alive")
    @ResponseBody
    public String isAlive() {
        return "I'm fine, hi!";
    }

    @GetMapping(path = "/is_logged_in")
    @ResponseBody
    public String isLoggedIn(@CurrentUser AppUser user) {
        if (user != null && user.getId() != null) {
            return "You are logged in!";
        } else {
            return "You are not logged in! " + SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
    }
}
