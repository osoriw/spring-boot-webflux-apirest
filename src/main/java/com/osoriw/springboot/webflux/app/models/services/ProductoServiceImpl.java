package com.osoriw.springboot.webflux.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.osoriw.springboot.webflux.app.models.repository.CategoriaRepository;
import com.osoriw.springboot.webflux.app.models.repository.ProductoRepository;
import com.osoriw.springboot.webflux.app.models.documents.Categoria;
import com.osoriw.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoDao;

    @Autowired
    private CategoriaRepository categoriaDao;

    @Override
    public Flux<Producto> findAll() {

        return productoDao.findAll();
    }

    @Override
    public Mono<Producto> findById(String id) {
        return productoDao.findById(id);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return productoDao.save(producto);
    }

    @Override
    public Mono<Void> delete(Producto producto) {
        return productoDao.delete(producto);
    }

    @Override
    public Flux<Producto> findAllConNombreUpperCase() {
        return productoDao.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
    }

    @Override
    public Flux<Producto> findAllConNombreUpperCaseRepeat() {
        return findAllConNombreUpperCase().repeat(5000);
    }

    @Override
    public Flux<Categoria> findAllCategoria() {
        return categoriaDao.findAll();
    }

    @Override
    public Mono<Categoria> findCategoriaById(String id) {
        return categoriaDao.findById(id);
    }

    @Override
    public Mono<Categoria> saveCategoria(Categoria categoria) {
        return categoriaDao.save(categoria);
    }

    @Override
    public Mono<Producto> findByNombre(String nombre) {
        return productoDao.findByNombre(nombre);
    }

}
