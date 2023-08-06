package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @EntityGraph(attributePaths = {"items"})
    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    @EntityGraph(attributePaths = {"items"})
    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    Optional<ItemRequest> findById(Long requestId);
}
