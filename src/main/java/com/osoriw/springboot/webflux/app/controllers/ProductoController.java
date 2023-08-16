package com.osoriw.springboot.webflux.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.osoriw.springboot.webflux.app.models.documents.Producto;
import com.osoriw.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	@Autowired
	private ProductoService productoService;

	/**
	 * Lista todos los productos (combinando Mono y ResponseEntity)
	 */
	@GetMapping
	public Mono<ResponseEntity<Flux<Producto>>> findAll() {
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.findAll()));
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>> findById(@PathVariable String id) {
		return productoService.findById(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(p));
	}

}
