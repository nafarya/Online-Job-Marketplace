package com.highfive.highfive;

import com.highfive.highfive.model.Order;

/**
 * Created by dan on 23.01.17.
 */

public interface Navigator {
    void navigateToProfile();

    void navigateToOrderDetail(Order order);

}
