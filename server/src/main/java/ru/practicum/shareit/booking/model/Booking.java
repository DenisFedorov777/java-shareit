package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User booker;

    @Column(name = "start_time")
    LocalDateTime start;

    @Column(name = "end_time")
    LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    Status status;
}