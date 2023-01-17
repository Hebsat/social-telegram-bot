package com.example.javaproTeams30TelegramBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {

    private Long id;

    private LocalDateTime time;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("parent_id")
    private Long parentId;

    private PersonDto author;

    @JsonProperty("comment_text")
    private String commentText;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("sub_comments")
    private List<CommentDto> embeddedComments;

    private Integer likes;

    @JsonProperty("my_like")
    private Boolean myLike;

    @Override
    public String toString() {
        return "CommentRs{" +
                "commentId=" + id +
                ", postId=" + postId +
                ", parentCommentId=" + parentId +
                ", authorId=" + author.getId() +
                ", isBlocked=" + isBlocked +
                ", isDeleted=" + isDeleted +
                ", embeddedCommentsCount=" + embeddedComments.size() +
                ", likes=" + likes +
                ", myLike=" + myLike +
                '}';
    }
}
