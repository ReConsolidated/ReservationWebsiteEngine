package io.github.reconsolidated.zpibackend.features.storeConfig.dtos;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.storeConfig.Owner;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerDto {
    private final Long ownerId;
    private final String name;
    private final String logoSrc;
    private final String phone;
    private final String email;
    private final String color;

    public OwnerDto(Owner owner, AppUser appUser) {
        this.ownerId = owner.getOwnerId();
        this.name = appUser.getFirstName() + " " + appUser.getLastName();
        this.logoSrc = appUser.getImageUrl();
        this.phone = appUser.getPhoneNumber();
        this.email = appUser.getEmail();
        this.color = appUser.getColor();
    }
}
