package com.mindhub.duodanzaclub.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String titulo;
    private String descripcion;
    private Double precio;
    private String imagen;
    private Estilos estilo;
    private TipoProducto tipoProducto;
    private int stock;




    public Productos() {
    }

    public Productos(String titulo, String descripcion, Double precio, String imagen, Estilos estilo, TipoProducto tipoProducto, int stock) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.estilo = estilo;
        this.tipoProducto = tipoProducto;
        this.stock = stock;
    }




    public long getId() {return id;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public Double getPrecio() {return precio;}
    public void setPrecio(Double precio) {this.precio = precio;}

    public String getImagen() {return imagen;}
    public void setImagen(String imagen) {this.imagen = imagen;}

    public Estilos getEstilo() {return estilo;}
    public void setEstilo(Estilos estilo) {this.estilo = estilo;}

    public TipoProducto getTipoProducto() {return tipoProducto;}
    public void setTipoProducto(TipoProducto tipoProducto) {this.tipoProducto = tipoProducto;}

    public int getStock() {return stock;}
    public void setStock(int stock) {this.stock = stock;}

}
