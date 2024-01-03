package com.recipia.recipe.adapter.out.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hashtags")
public class HashtagDocument {

    @Id
    private String id;
    private List<String> hashtags;
}
