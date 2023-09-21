package io.github.reconsolidated.zpiBackend.features.item;

import io.github.reconsolidated.zpiBackend.authentication.appUser.AppUser;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
    protected ArrayList<AppUser> users;
    protected Integer quantity;
    protected LocalDateTime rentalStart;
    protected LocalDateTime rentalEnd;


}
