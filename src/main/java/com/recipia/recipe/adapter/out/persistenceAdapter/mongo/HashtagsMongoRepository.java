package com.recipia.recipe.adapter.out.persistenceAdapter.mongo;

import com.recipia.recipe.adapter.out.persistence.document.HashtagDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HashtagsMongoRepository extends MongoRepository<HashtagDocument, String> {

}
