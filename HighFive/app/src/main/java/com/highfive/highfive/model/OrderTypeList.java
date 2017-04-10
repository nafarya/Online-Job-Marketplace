package com.highfive.highfive.model;

import java.util.ArrayList;

/**
 * Created by dan on 09.04.17.
 */

public class OrderTypeList {
    private ArrayList<OrderType> orderTypelist;

    public OrderTypeList(ArrayList<OrderType> list) {
        this.orderTypelist = list;
    }

    public ArrayList<OrderType> getorderTypelist() {
        return orderTypelist;
    }

    public String getOrderTypeNameByTypeId(String typeId) {
        for (int i = 0; i < orderTypelist.size(); i++) {
            if (orderTypelist.get(i).getId().equals(typeId)) {
                return orderTypelist.get(i).getName();
            }
        }
        return "no such orderType";
    }
}
