package ru.practicum.shareit.comment.dto;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentMapper {
    public static Comment mapToComment(CommentDto commentDto, User author, Item item, LocalDateTime created) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .created(created)
                .build();
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(Optional.ofNullable(comment.getAuthor()).map(User::getName).orElse(""))
                .created(comment.getCreated())
                .build();
    }

    public static Collection<CommentDto> mapToCommentDto(Collection<Comment> comments) {
        return comments.stream().map(CommentMapper::mapToCommentDto).collect(Collectors.toList());
    }
}
