package com.deneksepeti.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class PostDto {
    String postId;
    String userId;
    String displayName;
    String title;
    String description;
    String requirements;
    int quota;
    Date deadline;
    String image;
    List<String> users;
}
