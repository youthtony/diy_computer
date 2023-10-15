package com.diy.computer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diy.computer.entity.OrderDetail;
import com.diy.computer.entity.Orders;

import java.util.List;

public interface OrderService extends IService<Orders> {
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId);

    void submit(Orders orders);
}
