package com.osoriw.springboot.webflux.app;

import com.osoriw.springboot.webflux.app.handlers.ProductoHandler;
import com.osoriw.springboot.webflux.app.models.documents.Producto;
import com.osoriw.springboot.webflux.app.models.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class RouterFunctionConfig {

    @Autowired
    private ProductoService service;


    /**
     * Este bean permite registrar las rutas de la API Rest, en el contenedor de Spring.
     */
    @Bean
    public RouterFunction<ServerResponse> routes(ProductoHandler handler) {
        return RouterFunctions.route(
                        // declarando 2 rutas con versiones diferentes para el mismo endpoint
                        RequestPredicates.GET("/api/v2/productos").or(RequestPredicates.GET("/api/v3/productos")), handler::findAll)
                .andRoute(RequestPredicates.GET("/api/v2/productos/{id}"), handler::findById)
                .andRoute(RequestPredicates.POST("/api/v2/productos"), handler::save);

    }

}
