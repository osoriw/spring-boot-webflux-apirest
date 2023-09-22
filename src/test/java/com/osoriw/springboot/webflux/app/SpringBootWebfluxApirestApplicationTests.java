package com.osoriw.springboot.webflux.app;

import com.osoriw.springboot.webflux.app.models.documents.Producto;
import com.osoriw.springboot.webflux.app.models.services.ProductoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductoService productoService;

    @Test
    void testFindAllProductos() {
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

    @Test
    void testGetProducto() {
        Producto producto = productoService.findByNombre("Sony Notebook").block(); // se usa block porque las pruebas unitarias deben ser síncronas

        //validar que el producto no sea nulo,antes de continuar con las validaciones restantes.
        Assertions.assertNotNull(producto);
        client.get()
                .uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Producto.class)
                .consumeWith(response -> {
                    Producto p = response.getResponseBody();

                    Assertions.assertNotNull(p);
                    Assertions.assertFalse(p.getId().isEmpty());
                    Assertions.assertEquals(p.getNombre(), "Sony Notebook");
                });
                /*.expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Sony Notebook");*/

    }

}
