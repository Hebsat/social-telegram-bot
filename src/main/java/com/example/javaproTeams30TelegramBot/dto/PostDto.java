package com.example.javaproTeams30TelegramBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {

    private Long id;

    private LocalDateTime time;

    private PersonDto author;

    private String title;

    private Integer likes;

    private List<String> tags;

    private List<CommentDto> comments;

    private String type;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("my_like")
    private Boolean myLike;

    @Override
    public String toString() {
        return "PostRs{" +
                "postId=" + id +
                ", authorId=" + author.getId() +
                ", title='" + (title.length() < 10 ? title : (title.substring(0, 8) + "..")) +
                "', likes=" + likes +
                ", tagsCount=" + tags.size() +
                ", commentsCount=" + comments.size() +
                ", isBlocked=" + isBlocked +
                ", myLike=" + myLike +
                '}';
    }
}
