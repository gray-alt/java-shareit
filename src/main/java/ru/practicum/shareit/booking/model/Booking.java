package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "start_date", nullable = false)
    LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    User booker;
    @Enumerated(EnumType.STRING)
    BookingStatus status;

    public Booking() {
        this.id = null;
        this.start = null;
        this.end = null;
        this.item = null;
        this.booker = null;
        this.status = null;
    }

    public Booking withStatus(BookingStatus status) {
        return Booking.builder()
                .id(this.getId())
                .start(this.getStart())
                .end(this.getEnd())
                .item(this.getItem())
                .booker(this.booker)
                .status(status)
                .build();
    }

    public Long getItemOfBookingId() {
        return Optional.ofNullable(this.item).map(Item::getId).orElse(null);
    }

    public Long getBookerOfBookingId() {
        return Optional.ofNullable(this.booker).map(User::getId).orElse(null);
    }

    public Long getOwnerOfItemId() {
        return Optional.of(this.item).map(Item::getOwnerOfItemId).orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
