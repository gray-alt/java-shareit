package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    void deleteAllByOwnerId(Long ownerId);

    void deleteByIdAndOwnerId(Long itemId, Long ownerId);

    boolean existsByIdAndOwnerId(Long itemId, Long ownerId);

    Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);

    Collection<Item> findAllByOwnerIdOrderById(Long ownerId);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true " +
            "and (upper(it.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(it.description) like upper(concat('%', ?1, '%')))")
    Collection<Item> findAllBySearch(String searchText);
}
