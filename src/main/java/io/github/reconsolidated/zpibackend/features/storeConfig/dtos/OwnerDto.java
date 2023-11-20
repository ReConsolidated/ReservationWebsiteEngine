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

    public OwnerDto(Owner owner) {
        this.ownerId = owner.getOwnerId();
        this.name = owner.getStoreName();
        this.logoSrc = owner.getImageUrl();
        this.phone = owner.getPhoneNumber();
        this.email = owner.getEmail();
        this.color = owner.getColor();
    }

    public Owner toOwner(Long ownerUserId) {
        return Owner.builder()
                .ownerId(ownerId)
                .appUserId(ownerUserId)
                .storeName(name.replaceAll("[ /]", "_"))
                .imageUrl(logoSrc)
                .phoneNumber(phone)
                .email(email)
                .color(color)
                .build();
    }
}
