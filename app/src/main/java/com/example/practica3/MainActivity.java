package com.example.practica3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.webkit.WebView;


import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    ArrayList<Producto> productos = new ArrayList<Producto>();

    Element nodoRaizPalets;
    Element nodoRaizProductos;
    Element nodoRaizTipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //------Definir el objeto webView---//
        WebView web = findViewById(R.id.webview);

        //-----Lo necesario para abrir el documento----//

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {

            InputStreamReader isr = new InputStreamReader(getAssets().open("almacen.xml"));
            InputSource is = new InputSource(isr);
            DocumentBuilder db = factory.newDocumentBuilder();
            Document archivoXml = db.parse(is);
            nodoRaizPalets = archivoXml.getDocumentElement(); //nodo para buscar en las estanterías
            nodoRaizProductos = archivoXml.getDocumentElement(); //nodo para buscar el nombre de producto
            nodoRaizTipos = archivoXml.getDocumentElement(); //nodo para buscar el tipo
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //---Sacar la información----//

        //---Buscamos los elementos palet---//
        NodeList listaPalets = nodoRaizPalets.getElementsByTagName("palet");
        int numPalets = listaPalets.getLength();

        //---Buscamos los elementos producto--//
        NodeList listaProductos = nodoRaizProductos.getElementsByTagName("producto");
        int numProductos = listaProductos.getLength();

        NodeList listaTipos = nodoRaizTipos.getElementsByTagName("tipo");
        int numTipos = listaTipos.getLength();

        String tipoProducto, nombreComercial;
        tipoProducto = "";
        nombreComercial = "";
        double cantidad = 0;

        for (int i = 0; i < numPalets; i++) {
            Element palet = (Element) listaPalets.item(i); //Obtenemos cada elemento Palet y sacamos sus datos
            cantidad = Integer.parseInt(palet.getAttribute("cantidadProducto"));
            nombreComercial = palet.getAttribute("idProducto");

            //Para cada uno de estos elementos iteramos el xml producto y comprobamos si la idProducto es el tipo
            //declarado es igual a la sacada anteriormente.

            for (int j = 0; j < numProductos; j++) {
                Element producto = (Element) listaProductos.item(j);
                if (nombreComercial.equals(producto.getAttribute("idProducto"))) {
                    //Si coinciden, sacamos el tipo de producto que es y salimos del bucle
                    tipoProducto = producto.getAttribute("idTipo");

                }
                //Creamos el objeto y lo añadimos al arrayList

            }
            Producto productoObj = new Producto(cantidad, nombreComercial, tipoProducto);
            productos.add(productoObj);

        }
        String[] tipoProdOrdenado = new String[numTipos];
        String[] nombreProdOrdenado = new String[numProductos];

        for (int i = 0; i < numTipos; i++) {
            Element e = (Element) listaTipos.item(i);
            tipoProdOrdenado[i] = e.getAttribute("idTipo");

        }
        for (int i = 0; i < numProductos; i++) {
            Element e = (Element) listaProductos.item(i);
            nombreProdOrdenado[i] = e.getAttribute("idProducto");
        }
        Arrays.sort(tipoProdOrdenado);
        Arrays.sort(nombreProdOrdenado);

        HashMap<String, Integer> relacionNombreCantidad = new HashMap<String, Integer>();
        HashMap<String, Integer> relacionNombrePalet = new HashMap<String, Integer>();
        HashMap<String, ArrayList<String>> relacionTipoNombres = new HashMap<String, ArrayList<String>>();
        //clave->tipo producto y valor son los productos de ese tipo

        for (String tipo : tipoProdOrdenado) {
            ArrayList<String> prods = new ArrayList<String>();

            for (Producto p : productos) {

                if (p.tipoProducto.equals(tipo)) {
                    //Comprobar si el producto ya está añadido al ArrayList
                    //Por defecto, añadimos el producto nuevo.
                    boolean anadir = true;
                    for (String str : prods) {
                        if (str.equals(p.nombreComercial)) {
                            //Si ya está añadido, no lo volvemos a meter
                            anadir = false;
                        }
                    }
                    if (anadir) {

                        prods.add(p.nombreComercial);
                       anadir=false;
                    }

                }
            }
            Collections.sort(prods);
            relacionTipoNombres.put(tipo, prods);
            //Este diccionario relaciona cada tipo de producto con un ArrayList de strings que son
            //los nombres de ese tipo.

        }

        //Creo dos diccionarios para relacionar el nombre de producto con su cantidad y el numero de palets
        for (String nombre : nombreProdOrdenado) {
            int cantProd = 0;
            int cantPalet = 0;

            for (Producto p : productos) {
                if (nombre.equals(p.nombreComercial)) {
                    cantPalet++;
                    cantProd += p.cantidadProducto;


                }


            }
            relacionNombreCantidad.put(nombre, cantProd);
            relacionNombrePalet.put(nombre, cantPalet);

        }


        //--------Creación de la información de la HTML------//
        String tablas = "";
        for (String tipoProd : tipoProdOrdenado) {
            String cabecera = "<br /><br /><b>" + tipoProd + "</b><br /><br />";
            String cabeceraTabla = "<div align=\"left\"><table border=\"1\" cellspacing=\"0\" cellpadding=\"5\">" + "<thead><tr bgcolor=\"#CCCCCC\"><th>Producto</th><th>Palets</th><th>Cantidad</th></tr></thead>"
                    + "<tbody>";
            String cuerpoTabla = "";

            ArrayList<String> informacion = relacionTipoNombres.get(tipoProd);
            for (String str : informacion) {
                cuerpoTabla += "<tr><td>" + str + "</td>" + "<td>" + relacionNombrePalet.get(str) + "</td>" + "<td>" + relacionNombreCantidad.get(str) + "</td></tr>";

            }


            String finTabla = "</tbody></table></div>";
            tablas += cabecera + cabeceraTabla + cuerpoTabla + finTabla;
        }


        String html = "<html><body style=\"text-align:left\">" +
                "<h1><b>Inventario de productos</b></h1>" + tablas + "</body></html>";
        web.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);


    }
}

