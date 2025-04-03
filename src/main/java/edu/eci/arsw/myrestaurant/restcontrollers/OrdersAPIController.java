/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.parser.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */
@RequestMapping("/orders")
@RestController
public class OrdersAPIController {

    @Autowired
    private  static RestaurantOrderServicesStub restaurantOrderServicesStub;

    
    @GetMapping("/{table}")
    public ResponseEntity<?> getOrder(@RequestParam int order){
        Map<Integer,Order> buscandoOrder = restaurantOrderServicesStub.getOrders();
        for(Order probandoOrder : buscandoOrder.values()){
            if(probandoOrder.equals(restaurantOrderServicesStub.getTableOrder(order))){
                return new ResponseEntity<>("la orden existe y es" + probandoOrder ,HttpStatus.OK);
            }
        }
        
        return new ResponseEntity<>("La orden no existe o la mesa dada es incorrecta",HttpStatus.NOT_FOUND);
    }
    

    @GetMapping("/{order}/total")
    public ResponseEntity<?> getTotalOrder(@RequestParam int order) throws OrderServicesException{
        Map<Integer,Order> buscandoOrder = restaurantOrderServicesStub.getOrders();
        for(Order probandoOrder : buscandoOrder.values()){
            if(probandoOrder.equals(restaurantOrderServicesStub.getTableOrder(order))){
                Map<String, Integer> productos = probandoOrder.getOrderAmountsMap();
                int total= 0;
                for(Integer precio : productos.values()){
                    total += precio;
                }
                return new ResponseEntity<>("el total de la orden "+probandoOrder+ " es de = " + total ,HttpStatus.OK);
            }
        }
        return  new ResponseEntity<>("Orden no encontrada",HttpStatus.OK);
    }

    @PostMapping("/orden")
    public ResponseEntity<?> setOrder(@RequestParam String nameOrder){
        restaurantOrderServicesStub.agregarOrder(nameOrder);
        return new ResponseEntity<>("Se agregó", HttpStatus.OK);
    }

    @PostMapping("/orden/producto")
    public ResponseEntity<?> agreagarProductoAOrden(@RequestParam String nameOrder, String productoAgregar,int cantidad) throws OrderServicesException{
        RestaurantProduct producto = restaurantOrderServicesStub.getProductByName(productoAgregar);
        
        return new ResponseEntity<>("se agregó el producto",HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getTodasOrdersYTotales()  {
        Map<Integer, Order> ordenes = restaurantOrderServicesStub.getOrders();
        
        Map<Order, Integer> totalizando = new HashMap<Order,Integer>();
        for(Order orden: ordenes.values()){
            Map<String, Integer> productos = orden.getOrderAmountsMap();
                int total= 0;
                for(Integer precio : productos.values()){
                    total += precio;
                }
            totalizando.put(orden, total);
        }
       return new ResponseEntity<>(totalizando,HttpStatus.OK);
    }
}
