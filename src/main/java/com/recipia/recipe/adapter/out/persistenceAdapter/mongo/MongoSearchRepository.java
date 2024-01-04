package com.recipia.recipe.adapter.out.persistenceAdapter.mongo;

import com.recipia.recipe.adapter.out.persistence.document.SearchDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * mongoDB 전용 리포지토리 클래스 작성
 */
public interface MongoSearchRepository extends MongoRepository<SearchDocument, String> {

}
