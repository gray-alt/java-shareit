package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String description;
    @Column(name = "is_available")
    Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    User owner;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    ItemRequest request;

    public Item() {
        this.id = null;
        this.name = null;
        this.description = null;
        this.available = null;
        this.owner = null;
        this.request = null;
    }

    public Item withItem(Item item) {
        return Item.builder()
                .id(this.getId())
                .name(item.getName() != null ? item.getName() : this.getName())
                .description(item.getDescription() != null ? item.getDescription() : this.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : this.getAvailable())
                .owner(this.getOwner())
                .request(this.getRequest())
                .build();
    }

    public Long getOwnerOfItemId() {
        return Optional.ofNullable(this.owner).map(User::getId).orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", owner=" + owner +
                ", request=" + request +
                '}';
    }
}
