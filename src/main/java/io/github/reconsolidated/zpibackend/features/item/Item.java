package io.github.reconsolidated.zpibackend.features.item;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    protected Long Id;
    protected Integer capacity;
    @OneToMany
    protected List<AppUser> users;
    protected Integer quantity;
    protected LocalDateTime rentalStart;
    protected LocalDateTime rentalEnd;


}
