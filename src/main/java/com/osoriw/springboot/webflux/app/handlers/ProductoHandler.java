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

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Producto.class);

    }

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

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Producto> productoMono = request.bodyToMono(Producto.class);

        return productoMono
                .flatMap(producto -> {
                    if(producto.getCreateAt() == null){
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


}
