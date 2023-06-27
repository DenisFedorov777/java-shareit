package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.statuses.BookingStatus;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public List<ItemDtoWithBooking> getAllItems(Long userId) {
        List<Item> itemsUserOwner = itemRepository.findAllByOwner_IdOrderByIdAsc(userId);
        List<ItemDtoWithBooking> listItemDtoWithBooking = new ArrayList<>();
        for (Item item : itemsUserOwner) {
            ItemDtoWithBooking itemDtoWithBooking = getItemDtoWithBooking(getCommentListByUser(userId), item);
            listItemDtoWithBooking.add(itemDtoWithBooking);
        }
        return listItemDtoWithBooking;
    }

    @Override
    public ItemDtoWithBooking getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Товара с таким идентификатором не существует."));
        User userOwner = item.getOwner();
        if (userId.equals(userOwner.getId())) {
            return getItemDtoWithBooking(getCommentListByItem(itemId), item);
        } else {
            return ItemMapper.toItemDtoBooking(item, getCommentListByItem(itemId));
        }
    }

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден."));
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден."));
        Item item = ItemMapper.toItem(itemDto, user);
        validationDataForUpdateItem(item, itemId);
        if (isCheckOwnerItem(userId, itemId)) {
            item.setId(itemId);
            item.setOwner(userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден.")));
            itemRepository.save(item);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<Item> searchItemForText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemForText(text);
    }

    @Transactional
    @Override
    public Comment createComment(Long userId, Comment comment, Long itemId) {
        LocalDateTime createdDate = comment.getCreated();
        if (isCheckItemExistsByRenter(itemId, userId, createdDate)) {
            comment.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден.")));
            comment.setItem(itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemNotFoundException("Товар с таким идентификатором не найден.")));
            return commentRepository.save(comment);
        } else {
            log.error("Не удалось найти товар для этого арендатора.");
            throw new ItemNotFoundException("Не удалось найти товар для этого арендатора.");
        }
    }

    private boolean isCheckItemExistsByRenter(Long itemId, Long userId, LocalDateTime createdDate) {
        List<Booking> listBooking = bookingRepository.findBookingsByBooker_IdOrderByIdAsc(userId);
        return listBooking.stream()
                .filter(status -> status.getStatus().equals(BookingStatus.APPROVED))
                .filter(data -> data.getEndDate().isBefore(createdDate))
                .filter(user -> user.getBooker().getId().equals(userId))
                .map(Booking::getItem)
                .filter(itemQ -> itemQ.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Товар не найден.")).getAvailable();
    }

    private void validationDataForUpdateItem(Item item, Long itemId) {
        if (item.getName() == null) {
            item.setName(getItemById(itemId).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(getItemById(itemId).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(getItemById(itemId).getAvailable());
        }
    }

    private boolean isCheckOwnerItem(Long userId, Long itemId) {
        return getItemById(itemId).getOwner().equals(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден.")));
    }

    private ItemDtoWithBooking getItemDtoWithBooking(List<CommentDto> commentList, Item item) {
        ItemDtoWithBooking itemDtoWithBooking = ItemMapper.toItemDtoBooking(item, commentList);
        Optional<Booking> lastBooking = bookingRepository
                .findFirstByItem_IdAndStartDateBeforeOrderByEndDateDesc(item.getId(), LocalDateTime.now());
        Optional<Booking> nextB = bookingRepository
                .findFirstByItem_IdAndStartDateAfterOrderByEndDateAsc(item.getId(), LocalDateTime.now());
        if (lastBooking.isEmpty()) {
            itemDtoWithBooking.setLastBooking(null);
            itemDtoWithBooking.setNextBooking(null);
        } else if (nextB.isEmpty()) {
            itemDtoWithBooking.setLastBooking(BookingMapper.toBookingOwnerDto(lastBooking.get()));
        } else {
            itemDtoWithBooking.setLastBooking(BookingMapper.toBookingOwnerDto(lastBooking.get()));
            itemDtoWithBooking.setNextBooking(BookingMapper.toBookingOwnerDto(nextB.get()));
        }
        return itemDtoWithBooking;
    }


    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Товар не найден."));
    }

    private List<CommentDto> getCommentListByItem(Long itemId) {
        List<CommentDto> commentList = commentRepository.findCommentsByItem_Id(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (commentList.isEmpty()) {
            commentList = Collections.emptyList();
        }
        return commentList;
    }

    private List<CommentDto> getCommentListByUser(Long userId) {
        List<CommentDto> commentList = commentRepository.findCommentsByUser_Id(userId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (commentList.isEmpty()) {
            commentList = Collections.emptyList();
        }
        return commentList;
    }
}