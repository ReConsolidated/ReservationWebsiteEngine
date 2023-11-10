package io.github.reconsolidated.zpibackend.features.reservation;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationDTo {

    private Long id;
    private Long itemId;
    private List<Long> subItemIds;
    private String userEmail;
    private String personalData;
    private Boolean confirmed;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer amount;
    private String message;
}
