package com.diy.computer.dto;

import com.diy.computer.entity.OrderDetail;
import com.diy.computer.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {


    private List<OrderDetail> orderDetails;
}
