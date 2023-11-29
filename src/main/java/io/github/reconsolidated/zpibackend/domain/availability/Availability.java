package io.github.reconsolidated.zpibackend.domain.availability;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.reconsolidated.zpibackend.domain.reservation.ReservationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Availability {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endDateTime;
    private ReservationType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Availability that)) {
            return false;
        }
        if (!startDateTime.equals(that.startDateTime)) {
            return false;
        }
        if (!endDateTime.equals(that.endDateTime)) {
            return false;
        }
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = startDateTime.hashCode();
        result = 31 * result + endDateTime.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
