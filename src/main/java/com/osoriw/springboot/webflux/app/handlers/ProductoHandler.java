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
                .body(Mono.just(p), Producto.class));
                //.body(BodyInserters.fromValue(p))); // opcional si se quiere usar el método BodyInserters.fromValue(...)

    }


}
