package com.osoriw.springboot.webflux.app.models.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.osoriw.springboot.webflux.app.models.documents.Categoria;

public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {

}
