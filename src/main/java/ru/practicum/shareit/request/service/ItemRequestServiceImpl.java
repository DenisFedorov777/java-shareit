package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemRequestDto> getItemRequests(Long from, Long size, Long requestorId) {
        findUser(requestorId);
        List<ItemRequestDto> itemRequestDtos;
        Pageable pageable = PageRequest.of(
                from.intValue(), size.intValue(), Sort.by("created").descending());
        Page<ItemRequest> page =
                itemRequestRepository.findItemRequestsByExcludingRequestorId(requestorId, pageable);
        itemRequestDtos = page.getContent().stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        log.debug("Получение списка запросов: " + itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByRequestor(Long requestorId) {
        findUser(requestorId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);
        List<ItemRequestDto> itemRequestDtos = requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        log.debug("Получение списка запросов: " + itemRequestDtos);
        return itemRequestDtos;
    }

    @Transactional
    @Override
    public ItemRequestDto postItemRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        User user = findUser(requestorId);
        itemRequestDto.setRequestor(user);
        log.debug("Создание запроса на вещь: " + itemRequestDto);
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long requestorId) {
        findUser(requestorId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с данным id не найден"));
        log.debug("Получение списка запросов: " + request);
        return ItemRequestMapper.toItemRequestDto(request);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с данным id не найден"));
    }
}