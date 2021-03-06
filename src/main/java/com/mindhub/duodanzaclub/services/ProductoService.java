package com.mindhub.duodanzaclub.services;


import com.mindhub.duodanzaclub.dtos.ProductoDTO;
import com.mindhub.duodanzaclub.models.Productos;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


public interface ProductoService {

    public List<ProductoDTO> getProductsDTO();


    public Productos productoById(@PathVariable Long id);

    public void guardarProducto(Productos producto);


}
