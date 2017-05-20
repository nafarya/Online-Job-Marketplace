package com.highfive.highfive;

import com.highfive.highfive.model.Bid;
import com.highfive.highfive.model.BidComment;
import com.highfive.highfive.model.Order;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

/**
 * Created by dan on 23.01.17.
 */

public interface Navigator {

    void navigateToProfile();

    void navigateToHelp();

    void navigateToChooseOrder();

    void navigateToOrderDetail(Order order);

    void navigateToBidsList(ArrayList<Bid> bids, String orderId, String bidStatus);

    void navigateToBidListComments(ArrayList<BidComment> bidComments, String creatorId, String bidId);

    void navigateToAddOrder();

    void navigateToPayment();

    void navigateToLenta();

    void navigateToChatList();

    void navigateToChat(Order order);

    void pickPhoto();

    void pickDocsForChat(Socket socket);
}
