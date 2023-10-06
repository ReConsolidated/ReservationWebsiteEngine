package io.github.reconsolidated.zpibackend.authentication.appUser;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
// changes should always be done through AppUserService
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@Entity
public class AppUser {
    @Id
    @GeneratedValue(generator = "app_users")
    private Long id;
    private String keycloakId;
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String imageUrl = "";
    private String phoneNumber = "";
    @Enumerated(EnumType.STRING)
    private AppUserRole role = AppUserRole.USER;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        AppUser appUser = (AppUser) o;
        return id != null && Objects.equals(id, appUser.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
