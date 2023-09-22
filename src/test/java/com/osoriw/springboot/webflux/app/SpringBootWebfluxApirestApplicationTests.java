package com.osoriw.springboot.webflux.app;

import com.osoriw.springboot.webflux.app.models.documents.Producto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {

    @Autowired
    private WebTestClient client;


    @Test
    void findAllProductosTest() {
        client.get()
                .uri("/api/v2/productos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Producto.class)
                .consumeWith(listEntityExchangeResult -> {
                    List<Producto> productoList = listEntityExchangeResult.getResponseBody();
                    productoList.forEach(producto -> System.out.println(producto.getNombre()));
                });


    }

}
