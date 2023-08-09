package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@AutoConfigureTestDatabase
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    private final LocalDateTime dateMinus10 = LocalDateTime.now().minusMinutes(10);
    private final LocalDateTime dateMinus5 = LocalDateTime.now().minusMinutes(5);
    private final LocalDateTime datePlus10 = LocalDateTime.now().plusMinutes(10);
    private final LocalDateTime datePlus5 = LocalDateTime.now().plusMinutes(5);

    private final UserDto userDto = makeUserDto("test", "test@test.ru");
    private final UserDto bookerDto = makeUserDto("booker", "booker@test.ru");
    private final ItemDto itemDto = makeItemDto("test", "test", true, null);

    @Test
    void addBookingWithEndBeforeStart() {
        BookingDto bookingDto = makeBookingDto(dateMinus5, dateMinus10, 1L);
        ValidationException e = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Дата окончания бронирования не может быть раньше даты начала бронирования"));
    }

    @Test
    void addBookingWithEndEqualsStart() {
        BookingDto bookingDto = makeBookingDto(dateMinus5, dateMinus5, 1L);
        ValidationException e = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Дата окончания бронирования не может быть равной дате начала бронирования"));
    }

    @Test
    void addBookingWithWrongBooker() {
        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, 1L);
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Не найден пользователь с id"));
    }

    @Test
    void addBookingWithWrongItem() {
        UserDto newUserDto = userService.addUser(userDto);

        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, 999L);
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(newUserDto.getId(), bookingDto));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Не найдена вещь с id"));
    }

    @Test
    void addBookingWithNotAvailableItem() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), makeItemDto("test", "test", false, null));

        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());
        NotAllowedException e = Assertions.assertThrows(NotAllowedException.class,
                () -> bookingService.addBooking(newUserDto.getId(), bookingDto));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Вещь с id"));
    }

    @Test
    void addBookingWithOwnerItem() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(newUserDto.getId(), bookingDto));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Нельзя забронировать собственную вещь"));
    }

    @Test
    void addBooking() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        UserDto newBookerDto = userService.addUser(bookerDto);
        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());
        BookingDto newBookingDto = bookingService.addBooking(newBookerDto.getId(), bookingDto);

        assertThat(newBookingDto, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(bookingDto.getStart())),
                hasProperty("end", equalTo(bookingDto.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue()),
                hasProperty("status", equalTo(BookingStatus.WAITING))
        ));

        assertThat(newBookingDto.getItem(), allOf(
                hasProperty("id", equalTo(newItemDto.getId()))
        ));

        assertThat(newBookingDto.getBooker(), allOf(
                hasProperty("id", equalTo(newBookerDto.getId()))
        ));
    }

    @Test
    void approveBookingWithNotFoundItemOwner() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(999L, 1L, true));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Не найден пользователь с id"));
    }

    @Test
    void approveBookingWithWrongBooking() {
        UserDto newUserDto = userService.addUser(userDto);
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(newUserDto.getId(), 999L, true));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Не найдено бронирование с id"));
    }

    @Test
    void approveBookingWithWrongItemOwner() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);
        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newBookingDto = bookingService.addBooking(newBookerDto.getId(), bookingDto);

        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(newBookerDto.getId(), newBookingDto.getId(), true));
        assertThat(e.getMessage(), containsStringIgnoringCase(
                "не является владельцем вещи с id"));
    }

    @Test
    void approveBookingWithAlreadyApprovedStatus() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);
        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newBookingDto = bookingService.addBooking(newBookerDto.getId(), bookingDto);

        bookingService.approveBooking(newUserDto.getId(), newBookingDto.getId(), true);

        NotAllowedException e = Assertions.assertThrows(NotAllowedException.class,
                () -> bookingService.approveBooking(newUserDto.getId(), newBookingDto.getId(), true));
        assertThat(e.getMessage(), containsStringIgnoringCase(
                "Нельзя изменить статус подтвержденного бронирования"));
    }

    @Test
    void approveBookingToStatusApprove() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);
        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newBookingDto = bookingService.addBooking(newBookerDto.getId(), bookingDto);

        BookingDto updatedBooking = bookingService.approveBooking(newUserDto.getId(), newBookingDto.getId(), true);

        assertThat(updatedBooking, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(bookingDto.getStart())),
                hasProperty("end", equalTo(bookingDto.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue()),
                hasProperty("status", equalTo(BookingStatus.APPROVED))
        ));
    }

    @Test
    void approveBookingToStatusRejected() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);
        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newBookingDto = bookingService.addBooking(newBookerDto.getId(), bookingDto);

        BookingDto updatedBooking = bookingService.approveBooking(newUserDto.getId(), newBookingDto.getId(), false);

        assertThat(updatedBooking, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("start", equalTo(bookingDto.getStart())),
                hasProperty("end", equalTo(bookingDto.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue()),
                hasProperty("status", equalTo(BookingStatus.REJECTED))
        ));
    }

    @Test
    void getBookingByIdWithWrongUser() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(999L, 1L));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Не найден пользователь с id"));
    }

    @Test
    void getBookingByIdWithWrongBooking() {
        UserDto newUserDto = userService.addUser(userDto);
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(newUserDto.getId(), 999L));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Не найдено бронирование с id"));
    }

    @Test
    void getBookingByIdForItemOwner() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);
        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newBookingDto = bookingService.addBooking(newBookerDto.getId(), bookingDto);

        BookingDto foundBooking = bookingService.getBookingById(newUserDto.getId(), newBookingDto.getId());

        assertThat(foundBooking, allOf(
                hasProperty("id", equalTo(newBookingDto.getId())),
                hasProperty("start", equalTo(newBookingDto.getStart())),
                hasProperty("end", equalTo(newBookingDto.getEnd()))
        ));
    }

    @Test
    void getBookingByIdForBooker() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);
        BookingDto bookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newBookingDto = bookingService.addBooking(newBookerDto.getId(), bookingDto);

        BookingDto foundBooking = bookingService.getBookingById(newBookerDto.getId(), newBookingDto.getId());

        assertThat(foundBooking, allOf(
                hasProperty("id", equalTo(newBookingDto.getId())),
                hasProperty("start", equalTo(newBookingDto.getStart())),
                hasProperty("end", equalTo(newBookingDto.getEnd()))
                ));
    }

    @Test
    void getAllBookingsByBookerIdWithWrongBooker() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByBookerId(999L, BookingState.ALL, 0, 10));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Не найден пользователь с id"));
    }

    @Test
    void getAllBookingsByBookerIdWithEmptyBookings() {
        UserDto newBookerDto = userService.addUser(this.bookerDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.ALL, 0, 10);

        assertThat(foundBookings.size(), equalTo(0));
    }

    @Test
    void getAllBookingsByBookerIdWithStateAll() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.ALL, 0, 10);

        assertThat(foundBookings.size(), equalTo(3));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBookingDto.getId())),
                hasProperty("start", equalTo(newPastBookingDto.getStart())),
                hasProperty("end", equalTo(newPastBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBookingDto.getId())),
                hasProperty("start", equalTo(newCurrentBookingDto.getStart())),
                hasProperty("end", equalTo(newCurrentBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBookingDto.getId())),
                hasProperty("start", equalTo(newFutureBookingDto.getStart())),
                hasProperty("end", equalTo(newFutureBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByBookerIdWithStateCurrent() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.CURRENT, 0, 10);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBookingDto.getId())),
                hasProperty("start", equalTo(newCurrentBookingDto.getStart())),
                hasProperty("end", equalTo(newCurrentBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByBookerIdWithStatePast() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.PAST, 0, 10);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBookingDto.getId())),
                hasProperty("start", equalTo(newPastBookingDto.getStart())),
                hasProperty("end", equalTo(newPastBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByBookerIdWithStateFuture() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.FUTURE, 0, 10);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBookingDto.getId())),
                hasProperty("start", equalTo(newFutureBookingDto.getStart())),
                hasProperty("end", equalTo(newFutureBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByBookerIdWithStateWaiting() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.WAITING, 0, 10);

        assertThat(foundBookings.size(), equalTo(3));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBookingDto.getId())),
                hasProperty("start", equalTo(newPastBookingDto.getStart())),
                hasProperty("end", equalTo(newPastBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBookingDto.getId())),
                hasProperty("start", equalTo(newCurrentBookingDto.getStart())),
                hasProperty("end", equalTo(newCurrentBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBookingDto.getId())),
                hasProperty("start", equalTo(newFutureBookingDto.getStart())),
                hasProperty("end", equalTo(newFutureBookingDto.getEnd()))
        )));

        bookingService.approveBooking(newUserDto.getId(), newCurrentBookingDto.getId(), true);

        foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.WAITING, 0, 10);

        assertThat(foundBookings.size(), equalTo(2));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBookingDto.getId())),
                hasProperty("start", equalTo(newPastBookingDto.getStart())),
                hasProperty("end", equalTo(newPastBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBookingDto.getId())),
                hasProperty("start", equalTo(newFutureBookingDto.getStart())),
                hasProperty("end", equalTo(newFutureBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByBookerIdWithStateRejected() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.REJECTED, 0, 10);

        assertThat(foundBookings.size(), equalTo(0));

        bookingService.approveBooking(newUserDto.getId(), newCurrentBookingDto.getId(), false);

        foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.REJECTED, 0, 10);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBookingDto.getId())),
                hasProperty("start", equalTo(newCurrentBookingDto.getStart())),
                hasProperty("end", equalTo(newCurrentBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByBookerIdWithStateDefaultCase() {
        UserDto newBookerDto = userService.addUser(this.bookerDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByBookerId(newBookerDto.getId(),
                BookingState.TEST, 0, 10);

        assertThat(foundBookings.size(), equalTo(0));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithWrongOwner() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByItemOwnerId(999L, BookingState.ALL, 0, 10));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Не найден пользователь с id"));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithEmptyBookings() {
        UserDto newUserDto = userService.addUser(userDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.ALL, 0, 10);

        assertThat(foundBookings.size(), equalTo(0));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithStateAll() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.ALL, 0, 10);

        assertThat(foundBookings.size(), equalTo(3));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBookingDto.getId())),
                hasProperty("start", equalTo(newPastBookingDto.getStart())),
                hasProperty("end", equalTo(newPastBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBookingDto.getId())),
                hasProperty("start", equalTo(newCurrentBookingDto.getStart())),
                hasProperty("end", equalTo(newCurrentBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBookingDto.getId())),
                hasProperty("start", equalTo(newFutureBookingDto.getStart())),
                hasProperty("end", equalTo(newFutureBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithStateCurrent() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.CURRENT, 0, 10);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBookingDto.getId())),
                hasProperty("start", equalTo(newCurrentBookingDto.getStart())),
                hasProperty("end", equalTo(newCurrentBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithStatePast() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.PAST, 0, 10);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBookingDto.getId())),
                hasProperty("start", equalTo(newPastBookingDto.getStart())),
                hasProperty("end", equalTo(newPastBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithStateFuture() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.FUTURE, 0, 10);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBookingDto.getId())),
                hasProperty("start", equalTo(newFutureBookingDto.getStart())),
                hasProperty("end", equalTo(newFutureBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithStateWaiting() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.WAITING, 0, 10);

        assertThat(foundBookings.size(), equalTo(3));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBookingDto.getId())),
                hasProperty("start", equalTo(newPastBookingDto.getStart())),
                hasProperty("end", equalTo(newPastBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBookingDto.getId())),
                hasProperty("start", equalTo(newCurrentBookingDto.getStart())),
                hasProperty("end", equalTo(newCurrentBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBookingDto.getId())),
                hasProperty("start", equalTo(newFutureBookingDto.getStart())),
                hasProperty("end", equalTo(newFutureBookingDto.getEnd()))
        )));

        bookingService.approveBooking(newUserDto.getId(), newCurrentBookingDto.getId(), true);

        foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.WAITING, 0, 10);

        assertThat(foundBookings.size(), equalTo(2));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBookingDto.getId())),
                hasProperty("start", equalTo(newPastBookingDto.getStart())),
                hasProperty("end", equalTo(newPastBookingDto.getEnd()))
        )));

        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBookingDto.getId())),
                hasProperty("start", equalTo(newFutureBookingDto.getStart())),
                hasProperty("end", equalTo(newFutureBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithStateRejected() {
        UserDto newUserDto = userService.addUser(userDto);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        BookingDto pastBookingDto = makeBookingDto(dateMinus10, dateMinus5, newItemDto.getId());
        BookingDto currentBookingDto = makeBookingDto(dateMinus10, datePlus10, newItemDto.getId());
        BookingDto futureBookingDto = makeBookingDto(datePlus5, datePlus10, newItemDto.getId());

        UserDto newBookerDto = userService.addUser(this.bookerDto);
        BookingDto newPastBookingDto = bookingService.addBooking(newBookerDto.getId(), pastBookingDto);
        BookingDto newCurrentBookingDto = bookingService.addBooking(newBookerDto.getId(), currentBookingDto);
        BookingDto newFutureBookingDto = bookingService.addBooking(newBookerDto.getId(), futureBookingDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.REJECTED, 0, 10);

        assertThat(foundBookings.size(), equalTo(0));

        bookingService.approveBooking(newUserDto.getId(), newCurrentBookingDto.getId(), false);

        foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.REJECTED, 0, 10);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBookingDto.getId())),
                hasProperty("start", equalTo(newCurrentBookingDto.getStart())),
                hasProperty("end", equalTo(newCurrentBookingDto.getEnd()))
        )));
    }

    @Test
    void getAllBookingsByItemOwnerIdWithStateDefaultCase() {
        UserDto newUserDto = userService.addUser(this.bookerDto);

        Collection<BookingDto> foundBookings = bookingService.getAllBookingsByItemOwnerId(newUserDto.getId(),
                BookingState.TEST, 0, 10);

        assertThat(foundBookings.size(), equalTo(0));
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        return BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(itemId)
                .build();
    }

    private ItemDto makeItemDto(String name, String description, Boolean isAvailable, Long requestId) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(isAvailable)
                .requestId(requestId)
                .build();
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }
}
