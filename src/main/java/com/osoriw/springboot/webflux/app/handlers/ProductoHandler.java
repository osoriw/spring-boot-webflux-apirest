package com.osoriw.springboot.webflux.app.handlers;

import com.osoriw.springboot.webflux.app.models.documents.Producto;
import com.osoriw.springboot.webflux.app.models.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Component
public class ProductoHandler {

    @Autowired
    private ProductoService service;

    /**
     * Retorna todos los productos.
     * */
    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Producto.class);

    }

    /**
     * Retorna un producto por su id
     * */
    public Mono<ServerResponse> findById(ServerRequest request) {
        //obtenemos el path variable id, directamente del request
        String id = request.pathVariable("id");

        return service.findById(id).flatMap(p -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(p), Producto.class)) // el producto p, por ser un objeto no reactivo, se debe envolver en un Mono
                //.body(BodyInserters.fromValue(p))); // opcional si se quiere usar el método BodyInserters.fromValue(...)
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    /**
     * Guarda un nuevo producto en base de datos.
     * */
    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Producto> productoMono = request.bodyToMono(Producto.class);

        return productoMono
                .flatMap(producto -> {
                    if (producto.getCreateAt() == null) {
                        producto.setCreateAt(new Date());
                    }
                    return service.save(producto);
                })
                .flatMap(producto -> {
                            return ServerResponse
                                    .created(URI.create("/api/v2/productos/".concat(producto.getId())))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(BodyInserters.fromValue(producto));
                            //.body(Mono.just(producto), Producto.class); // opcional para crear un nuevo mono<Producto>, dentto del body
                        }
                );

    }

    /**
     * Actualiza un producto: esta forma de actualizar no es recomendada, sólo se coloca aquí con fines ilustrativos.
     */
    /*public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Producto> productoMono = request.bodyToMono(Producto.class);

        return productoMono.flatMap(pRequest -> {
                    return service.findById(id)
                            .flatMap(pBd -> {
                                pBd.setNombre(pRequest.getNombre());
                                pBd.setPrecio(pRequest.getPrecio());
                                pBd.setCategoria(pRequest.getCategoria());

                                return service.save(pBd);
                            });
                })
                .flatMap(pResponse -> {
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromValue(pResponse));
                });

    }*/

    /**
     * Actualiza un producto existente en base de datos.
     */
    public Mono<ServerResponse> update(ServerRequest request) {
        Mono<Producto> pRequest = request.bodyToMono(Producto.class);
        String id = request.pathVariable("id");

        Mono<Producto> pDb = service.findById(id);

        return pDb.zipWith(pRequest, (db, req) -> {
                    db.setNombre(req.getNombre());
                    db.setPrecio(req.getPrecio());
                    db.setCategoria(req.getCategoria());
                    return db;
                })
                .flatMap(producto -> {
                    return ServerResponse.created(URI.create("/api/v2/productos/".concat(producto.getId())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(service.save(producto), Producto.class);
                })
                .switchIfEmpty(ServerResponse.notFound().build());

    }


}
