package com.osoriw.springboot.webflux.app.controllers;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.support.WebExchangeBindException;

import com.osoriw.springboot.webflux.app.models.documents.Producto;
import com.osoriw.springboot.webflux.app.models.services.ProductoService;

import jakarta.validation.Valid;
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

	/*
	 * Crear un nuevo producto (valida que todos los campos mandatorios, se
	 * especifiquen en el request)
	 */
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> save(@Valid @RequestBody Mono<Producto> monoProducto) {

		Map<String, Object> respuesta = new HashMap<>();

		return monoProducto.flatMap(producto -> {
			producto.setCreateAt(new Date());

			return service.save(producto).map(p -> {
				respuesta.put("producto", p);
				respuesta.put("mensaje", "Producto creado con éxito");
				respuesta.put("timestamp", new Date());
				respuesta.put("status", HttpStatus.OK.value());

				return ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON).body(respuesta);
			});
		})
		.onErrorResume(t -> { // capturar cualquier error durante la validación del request de creación de producto
			return Mono.just(t).cast(WebExchangeBindException.class) // transformar los exepciones de tipo genérico al tipo específico WebExchangeBindException.class 
					.flatMap(e -> Mono.just(e.getFieldErrors())) // crear un observable del tipo  Mono<List<FieldError>> 
					.flatMapMany(Flux::fromIterable) // crear un observable del tipo Flux<FieldError>
					.map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage()) //mapear cada FieldError a un string con los datos relevantes
					.collectList() // crear un observable del tipo Mono<List<String>> 
					.flatMap(list -> { // transformar de Mono<List<String>> a  Mono<ResponseEntity<Map<String, Object>>> 
						respuesta.put("errors", list);
						respuesta.put("timestamp", new Date());
						respuesta.put("status", HttpStatus.BAD_REQUEST.value());

						return Mono.just(ResponseEntity.badRequest().body(respuesta));
					});
		});

	}
	
	/**
	 * Crear un nuevo producto (devolver json del producto creado en el body de la respuesta y header location del nuevo recurso creado).
	 */
	/*@PostMapping
	public Mono<ResponseEntity<Producto>> save(@RequestBody Producto producto) {

		producto.setCreateAt(new Date());

		return service.save(producto).map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON).body(p));

	}*/
	
	/**
	 * Crear un nuevo producto (devolver HttpStatus.CREATED y header location, en la respuesta).
	 */
	/*@PostMapping
	public Mono<ResponseEntity<Void>> save(@RequestBody Producto producto) {

		producto.setCreateAt(new Date());
		return service.save(producto).map(p -> {
			URI location = URI.create("/api/productos/".concat(producto.getId()));
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(location);
			return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
		});

	}*/
	

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
