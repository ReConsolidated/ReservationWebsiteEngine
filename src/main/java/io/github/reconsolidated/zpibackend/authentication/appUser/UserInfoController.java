package io.github.reconsolidated.zpibackend.authentication.appUser;

import io.github.reconsolidated.zpibackend.authentication.currentUser.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserInfoController {

    @GetMapping
    @ResponseBody
    public AppUser currentUserInfo(@CurrentUser AppUser user) {
        return user;
    }

    @GetMapping("/email")
    @ResponseBody
    public String currentUserEmail(@CurrentUser AppUser user) {
        return user.getEmail();
    }

}
