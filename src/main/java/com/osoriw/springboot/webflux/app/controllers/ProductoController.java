package com.osoriw.springboot.webflux.app.controllers;

import java.net.URI;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	private ProductoService service;

	/**
	 * Lista todos los productos (combinando Mono y ResponseEntity).
	 */
	@GetMapping
	public Mono<ResponseEntity<Flux<Producto>>> findAll() {
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(service.findAll()));
	}

	/**
	 * Obtener un producto por su id.
	 */
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>> findById(@PathVariable String id) {
		return service.findById(id).map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	/**
	 * Crear un nuevo producto.
	 */
	@PostMapping
	public Mono<ResponseEntity<Producto>> save(@RequestBody Producto producto) {

		producto.setCreateAt(new Date());

		return service.save(producto).map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON).body(p));

	}

	/**
	 * Actualizar un producto.
	 */
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Producto>> update(@RequestBody Producto producto, @PathVariable String id) {

		/*service.findById(id).map(p -> {

		p.setNombre(producto.getNombre());
		p.setPrecio(producto.getPrecio());
		p.setCategoria(producto.getCategoria());

		return service.save(p).map(p1 -> ResponseEntity.created(URI.create("/api/productos/".concat(id)))
				.contentType(MediaType.APPLICATION_JSON).body(p1));
		});

		return service.findById(id).flatMap(p -> {

		p.setNombre(producto.getNombre());
		p.setPrecio(producto.getPrecio());
		p.setCategoria(producto.getCategoria());

		return service.save(p).map(p1 -> ResponseEntity.created(URI.create("/api/productos/".concat(id)))
				.contentType(MediaType.APPLICATION_JSON).body(p1));
		});*/
		
		return service.findById(id).flatMap(p -> {
			p.setNombre(producto.getNombre());
			p.setPrecio(producto.getPrecio());
			p.setCategoria(producto.getCategoria());

			return service.save(p);
		}).map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(id)))
				.contentType(MediaType.APPLICATION_JSON).body(p)).defaultIfEmpty(ResponseEntity.notFound().build());

	}
	
	/*
	 * Eliminar un producto.
	 */
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {

		return service.findById(id)
				.flatMap(p -> service.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
				.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));

	}
	
}