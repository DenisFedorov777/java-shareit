package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    User owner;
    Item item;

    void createUserAndItem() {
        owner = new User();
        owner.setId(1L);
        owner.setName("TestUser");
        owner.setEmail("testuser@user.com");
        userRepository.save(owner);

        item = new Item();
        item.setName("Item1");
        item.setOwner(owner);
        item.setDescription("Description1");
        itemRepository.save(item);
    }

    @Test
    void findByItemIdAndOwnerId() {
        createUserAndItem();

        Long itemId = 1L;
        Long ownerId = 1L;

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(owner);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(booking);
        entityManager.flush();

        List<Booking> result = bookingRepository.findByItemIdAndOwnerId(itemId, ownerId);

        assertThat(result).isNotEmpty();
        assertThat(result).contains(booking);
    }

    @Test
    void findAllBookings() {
        createUserAndItem();
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(owner);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(booking);

        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(owner);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(booking2);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findAllBookings(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(booking, booking2);
    }

    @Test
    void findCurrentBookings() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(owner);
        currentBooking.setStart(LocalDateTime.now().minusHours(1));
        currentBooking.setEnd(LocalDateTime.now().plusHours(1));
        entityManager.persist(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(owner);
        pastBooking.setStart(LocalDateTime.now().minusHours(2));
        pastBooking.setEnd(LocalDateTime.now().minusHours(1));
        entityManager.persist(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setBooker(owner);
        futureBooking.setItem(item);
        futureBooking.setStart(LocalDateTime.now().plusHours(1));
        futureBooking.setEnd(LocalDateTime.now().plusHours(2));
        entityManager.persist(futureBooking);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findCurrentBookings(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(currentBooking);
        assertThat(result.getContent()).doesNotContain(pastBooking, futureBooking);
    }

    @Test
    void findPastBookings() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(owner);
        currentBooking.setStart(LocalDateTime.now());
        currentBooking.setEnd(LocalDateTime.now().plusHours(3));
        entityManager.persist(currentBooking);

        Booking pastBooking1 = new Booking();
        pastBooking1.setItem(item);
        pastBooking1.setBooker(owner);
        pastBooking1.setStart(LocalDateTime.now().minusHours(3));
        pastBooking1.setEnd(LocalDateTime.now().minusHours(1));
        entityManager.persist(pastBooking1);

        Booking pastBooking2 = new Booking();
        pastBooking2.setItem(item);
        pastBooking2.setBooker(owner);
        pastBooking2.setStart(LocalDateTime.now().minusDays(2));
        pastBooking2.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(pastBooking2);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(owner);
        futureBooking.setStart(LocalDateTime.now());
        futureBooking.setEnd(LocalDateTime.now().plusHours(1));
        entityManager.persist(futureBooking);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findPastBookings(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(pastBooking1, pastBooking2);
    }

    @Test
    void findFutureBookings() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(owner);
        currentBooking.setStart(LocalDateTime.now());
        currentBooking.setEnd(LocalDateTime.now().plusHours(1));
        entityManager.persist(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setBooker(owner);
        pastBooking.setItem(item);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(pastBooking);

        Booking futureBooking1 = new Booking();
        futureBooking1.setBooker(owner);
        futureBooking1.setItem(item);
        futureBooking1.setStart(LocalDateTime.now().plusHours(1));
        futureBooking1.setEnd(LocalDateTime.now().plusHours(3));
        entityManager.persist(futureBooking1);

        Booking futureBooking2 = new Booking();
        futureBooking2.setBooker(owner);
        futureBooking2.setItem(item);
        futureBooking2.setStart(LocalDateTime.now().plusDays(1));
        futureBooking2.setEnd(LocalDateTime.now().plusDays(2));
        entityManager.persist(futureBooking2);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findFutureBookings(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(futureBooking1, futureBooking2);
    }

    @Test
    void findBookingsByWaiting() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking waitingBooking1 = new Booking();
        waitingBooking1.setBooker(owner);
        waitingBooking1.setItem(item);
        waitingBooking1.setStart(LocalDateTime.now());
        waitingBooking1.setEnd(LocalDateTime.now().plusDays(1));
        waitingBooking1.setStatus(Status.WAITING);
        entityManager.persist(waitingBooking1);

        Booking waitingBooking2 = new Booking();
        waitingBooking2.setBooker(owner);
        waitingBooking2.setItem(item);
        waitingBooking2.setStart(LocalDateTime.now());
        waitingBooking2.setStatus(Status.WAITING);
        waitingBooking2.setEnd(LocalDateTime.now().plusHours(5));
        entityManager.persist(waitingBooking2);

        Booking confirmedBooking = new Booking();
        confirmedBooking.setBooker(owner);
        confirmedBooking.setItem(item);
        confirmedBooking.setStart(LocalDateTime.now());
        confirmedBooking.setEnd(LocalDateTime.now().plusHours(4));
        confirmedBooking.setStatus(Status.APPROVED);
        entityManager.persist(confirmedBooking);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findBookingsByWaiting(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(waitingBooking1, waitingBooking2);
        assertThat(result.getContent()).doesNotContain(confirmedBooking);
    }

    @Test
    void findBookingsByRejected() {
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking waitingBooking1 = new Booking();
        waitingBooking1.setBooker(owner);
        waitingBooking1.setItem(item);
        waitingBooking1.setStart(LocalDateTime.now());
        waitingBooking1.setEnd(LocalDateTime.now().plusDays(1));
        waitingBooking1.setStatus(Status.REJECTED);
        entityManager.persist(waitingBooking1);

        Booking waitingBooking2 = new Booking();
        waitingBooking2.setBooker(owner);
        waitingBooking2.setItem(item);
        waitingBooking2.setStart(LocalDateTime.now());
        waitingBooking2.setStatus(Status.REJECTED);
        waitingBooking2.setEnd(LocalDateTime.now().plusHours(5));
        entityManager.persist(waitingBooking2);

        Booking confirmedBooking = new Booking();
        confirmedBooking.setBooker(owner);
        confirmedBooking.setItem(item);
        confirmedBooking.setStart(LocalDateTime.now());
        confirmedBooking.setEnd(LocalDateTime.now().plusHours(4));
        confirmedBooking.setStatus(Status.APPROVED);
        entityManager.persist(confirmedBooking);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findBookingsByRejected(1L, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(waitingBooking1, waitingBooking2);
        assertThat(result.getContent()).doesNotContain(confirmedBooking);
    }

    @Test
    void findOwnerAllBookings() {
        createUserAndItem();
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(owner);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(booking);

        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(owner);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(booking2);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findOwnerAllBookings(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(booking, booking2);
    }

    @Test
    void findOwnerCurrentBookings() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(owner);
        currentBooking.setStart(LocalDateTime.now().minusHours(1));
        currentBooking.setEnd(LocalDateTime.now().plusHours(1));
        entityManager.persist(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(owner);
        pastBooking.setStart(LocalDateTime.now().minusHours(2));
        pastBooking.setEnd(LocalDateTime.now().minusHours(1));
        entityManager.persist(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setBooker(owner);
        futureBooking.setItem(item);
        futureBooking.setStart(LocalDateTime.now().plusHours(1));
        futureBooking.setEnd(LocalDateTime.now().plusHours(2));
        entityManager.persist(futureBooking);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findOwnerCurrentBookings(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(currentBooking);
        assertThat(result.getContent()).doesNotContain(pastBooking, futureBooking);
    }

    @Test
    void findOwnerPastBookings() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(owner);
        currentBooking.setStart(LocalDateTime.now());
        currentBooking.setEnd(LocalDateTime.now().plusHours(3));
        entityManager.persist(currentBooking);

        Booking pastBooking1 = new Booking();
        pastBooking1.setItem(item);
        pastBooking1.setBooker(owner);
        pastBooking1.setStart(LocalDateTime.now().minusHours(3));
        pastBooking1.setEnd(LocalDateTime.now().minusHours(1));
        entityManager.persist(pastBooking1);

        Booking pastBooking2 = new Booking();
        pastBooking2.setItem(item);
        pastBooking2.setBooker(owner);
        pastBooking2.setStart(LocalDateTime.now().minusDays(2));
        pastBooking2.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(pastBooking2);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(owner);
        futureBooking.setStart(LocalDateTime.now());
        futureBooking.setEnd(LocalDateTime.now().plusHours(1));
        entityManager.persist(futureBooking);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findOwnerPastBookings(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(pastBooking1, pastBooking2);
    }

    @Test
    void findOwnerFutureBookings() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(owner);
        currentBooking.setStart(LocalDateTime.now());
        currentBooking.setEnd(LocalDateTime.now().plusHours(1));
        entityManager.persist(currentBooking);

        Booking pastBooking = new Booking();
        pastBooking.setBooker(owner);
        pastBooking.setItem(item);
        pastBooking.setStart(LocalDateTime.now().minusDays(2));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(pastBooking);

        Booking futureBooking1 = new Booking();
        futureBooking1.setBooker(owner);
        futureBooking1.setItem(item);
        futureBooking1.setStart(LocalDateTime.now().plusHours(1));
        futureBooking1.setEnd(LocalDateTime.now().plusHours(3));
        entityManager.persist(futureBooking1);

        Booking futureBooking2 = new Booking();
        futureBooking2.setBooker(owner);
        futureBooking2.setItem(item);
        futureBooking2.setStart(LocalDateTime.now().plusDays(1));
        futureBooking2.setEnd(LocalDateTime.now().plusDays(2));
        entityManager.persist(futureBooking2);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findOwnerFutureBookings(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(futureBooking1, futureBooking2);
    }

    @Test
    void findOwnerBookingsByRejected() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking waitingBooking1 = new Booking();
        waitingBooking1.setBooker(owner);
        waitingBooking1.setItem(item);
        waitingBooking1.setStart(LocalDateTime.now());
        waitingBooking1.setEnd(LocalDateTime.now().plusDays(1));
        waitingBooking1.setStatus(Status.REJECTED);
        entityManager.persist(waitingBooking1);

        Booking waitingBooking2 = new Booking();
        waitingBooking2.setBooker(owner);
        waitingBooking2.setItem(item);
        waitingBooking2.setStart(LocalDateTime.now());
        waitingBooking2.setStatus(Status.REJECTED);
        waitingBooking2.setEnd(LocalDateTime.now().plusHours(5));
        entityManager.persist(waitingBooking2);

        Booking confirmedBooking = new Booking();
        confirmedBooking.setBooker(owner);
        confirmedBooking.setItem(item);
        confirmedBooking.setStart(LocalDateTime.now());
        confirmedBooking.setEnd(LocalDateTime.now().plusHours(4));
        confirmedBooking.setStatus(Status.APPROVED);
        entityManager.persist(confirmedBooking);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findOwnerBookingsByRejected(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(waitingBooking1, waitingBooking2);
        assertThat(result.getContent()).doesNotContain(confirmedBooking);
    }

    @Test
    void findOwnerBookingsByWaitingStatus() {
        Long ownerId = 1L;
        createUserAndItem();
        Pageable pageable = PageRequest.of(0, 10);

        Booking waitingBooking1 = new Booking();
        waitingBooking1.setBooker(owner);
        waitingBooking1.setItem(item);
        waitingBooking1.setStart(LocalDateTime.now());
        waitingBooking1.setEnd(LocalDateTime.now().plusDays(1));
        waitingBooking1.setStatus(Status.WAITING);
        entityManager.persist(waitingBooking1);

        Booking waitingBooking2 = new Booking();
        waitingBooking2.setBooker(owner);
        waitingBooking2.setItem(item);
        waitingBooking2.setStart(LocalDateTime.now());
        waitingBooking2.setStatus(Status.WAITING);
        waitingBooking2.setEnd(LocalDateTime.now().plusHours(5));
        entityManager.persist(waitingBooking2);

        Booking confirmedBooking = new Booking();
        confirmedBooking.setBooker(owner);
        confirmedBooking.setItem(item);
        confirmedBooking.setStart(LocalDateTime.now());
        confirmedBooking.setEnd(LocalDateTime.now().plusHours(4));
        confirmedBooking.setStatus(Status.APPROVED);
        entityManager.persist(confirmedBooking);

        entityManager.flush();

        Page<Booking> result = bookingRepository.findOwnerBookingsByWaiting(ownerId, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(waitingBooking1, waitingBooking2);
        assertThat(result.getContent()).doesNotContain(confirmedBooking);
    }

    @Test
    void findLastBookingForItem() {
        Long itemId = 1L;
        Long ownerId = 1L;
        createUserAndItem();

        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(owner);
        booking1.setStart(LocalDateTime.now().minusDays(2));
        booking1.setEnd(LocalDateTime.now().minusDays(1));
        booking1.setStatus(Status.WAITING);
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(owner);
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().plusHours(1));
        booking2.setStatus(Status.APPROVED);
        entityManager.persist(booking2);

        entityManager.flush();

        List<Booking> result = bookingRepository.findLastBookingForItem(itemId, ownerId);

        assertThat(result).isNotEmpty();
        assertThat(result).contains(booking2);
    }

    @Test
    void findNextBookingForItem() {
        Long itemId = 1L;
        Long ownerId = 1L;
        createUserAndItem();

        Booking booking1 = new Booking();
        booking1.setItem(item);
        booking1.setBooker(owner);
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setStatus(Status.WAITING);
        entityManager.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setItem(item);
        booking2.setBooker(owner);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setStatus(Status.APPROVED);
        entityManager.persist(booking2);

        entityManager.flush();

        List<Booking> result = bookingRepository.findNextBookingForItem(itemId, ownerId);

        assertThat(result).isNotEmpty();
        assertThat(result).contains(booking1);
    }
}