package io.github.reconsolidated.zpibackend.authentication.currentUser;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUserService;
import lombok.AllArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@AllArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final AppUserService appUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            return null;
        }
        if (!(authentication.getPrincipal() instanceof KeycloakPrincipal principal)) {
            throw new BadCredentialsException("User not found");
        }
        String keycloakId = principal.getName();
        String email = principal.getKeycloakSecurityContext().getToken().getEmail();
        String firstName = principal.getKeycloakSecurityContext().getToken().getGivenName();
        String lastName = principal.getKeycloakSecurityContext().getToken().getFamilyName();
        if (keycloakId == null) {
            throw new BadCredentialsException("Keycloak id not found");
        }

        return appUserService.getOrCreateUser(keycloakId, email, firstName, lastName);
    }

}
