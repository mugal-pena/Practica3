package com.example.practica3;

public class Producto {


    double cantidadProducto;
    String nombreComercial, tipoProducto;



    Producto(){
        this.cantidadProducto=0;
        this.nombreComercial="";
        this.tipoProducto="";

    }

    Producto(double cantidadProducto, String nombreComercial, String tipoProducto){
        this.cantidadProducto=cantidadProducto;
        this.nombreComercial=nombreComercial;
        this.tipoProducto=tipoProducto;
    }
}
