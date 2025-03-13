package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query(value = "select * " +
            "from items as item " +
            "where item.is_available = TRUE " +
            "AND (item.name ilike ?1 " +
            "OR item.description ilike ?1)", nativeQuery = true)
    List<Item> search(String text);
}
