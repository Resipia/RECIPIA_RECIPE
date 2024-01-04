package com.recipia.recipe.adapter.out.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "search")
public class SearchDocument {

    @Id
    private String id;

    @Indexed
    private List<String> ingredients;

    @Indexed
    private List<String> hashtags;

}
