package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;


public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByUserRequestId(Long userRequestId, Sort sort);

    List<ItemRequest> findByUserRequestIdNot(Long userRequestId, Sort sort);
}
