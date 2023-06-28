package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository usersRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest create(ItemRequestDto requestDto, Long userId) {
        User user = usersRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователя при создании реквеста нет."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, user);
        return requestRepository.save(itemRequest);
    }

    @Override
    public ItemRequestDto getRequestDto(ItemRequest request) {
        List<ItemDto> itemToRequests = new ArrayList<>();
        List<Item> itemsByRequest = itemRepository.findAllByRequestId(request.getId());
        itemsByRequest.forEach(item -> itemToRequests.add(ItemMapper.toItemDto(item)));
        return ItemRequestMapper.toItemRequestDto(request, itemToRequests);
    }

    @Override
    public List<ItemRequest> getRequestByUser(Long userId) {
        usersRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователя при getRequestByUser нет."));
        List<ItemRequest> itemRequests = requestRepository.findAllByRequester_IdOrderByCreatedDesc(userId);
        itemRequests.sort(Comparator.comparing(ItemRequest::getCreated).reversed());
        return itemRequests;
    }

    @Override
    public Page<ItemRequest> getAllRequests(Long userId, int from, int size) {
        return requestRepository.findAllByRequester_IdNotOrderByCreatedDesc(userId, PageRequest.of(from, size));
    }

    @Override
    public ItemRequest getItemRequest(Long requestId, Long userId) {
        usersRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не был найден."));
        return requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException("Такого запроса просто нет."));
    }
}