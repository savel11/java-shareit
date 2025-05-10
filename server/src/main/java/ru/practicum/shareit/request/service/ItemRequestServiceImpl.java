package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "created");


    @Override
    @Transactional
    public ItemRequestDto create(NewItemRequestDto newItemRequestDto, Long userId) {
        log.info("Создание нового запроса от пользователя с id = " + userId);
        User user = checkAndGetUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(newItemRequestDto, user);
        log.info("Запрос успешно создан!");
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) {
        log.info("Просмотр запрос с id = " + itemRequestId + " пользователем с id = " + userId);
        User user = checkAndGetUser(userId);
        log.trace("Проверка существования запроса");
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(itemRequestId);
        if (itemRequestOptional.isEmpty()) {
            log.warn("Запрос с id = " + itemRequestId + " не найден!");
            throw new NotFoundException("Запроса с id = " + itemRequestId + " не существует!");
        }
        log.trace("Запрос существует");
        log.info("Выводим запрос!");
        List<ItemDtoShort> items = itemRepository.findByRequestId(itemRequestId).stream().map(ItemMapper::toItemDtoShort)
                .toList();
        return ItemRequestMapper.toItemRequestDto(itemRequestOptional.get(), items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllItemRequest(Long userId) {
        log.info("Просмотор всех  запросов пользователем с id = " + userId);
        checkAndGetUser(userId);
        return itemRequestRepository.findByUserRequestIdNot(userId, sort).stream()
                .map(req -> ItemRequestMapper.toItemRequestDto(req, null)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        log.info("Просмотор всех своих запросов пользователем с id = " + userId);
        checkAndGetUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findByUserRequestId(userId, sort);
        Map<Long, List<ItemDtoShort>> items = itemRepository.findByRequestIdIn(requests.stream().map(ItemRequest::getId)
                .toList()).stream().map(ItemMapper::toItemDtoShort).collect(Collectors.groupingBy(ItemDtoShort::getId));
        return requests.stream().map(req -> ItemRequestMapper.toItemRequestDto(req, items.get(req.getId()))).toList();
    }

    private User checkAndGetUser(Long userId) {
        log.trace("Проверка существования пользователя");
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с id = " + userId + " не найден!");
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
        log.trace("Проверка закончена: Пользователь существует");
        return optionalUser.get();
    }
}
