package cn.mafangui.hotel.controller.user;

import cn.mafangui.hotel.entity.Order;
import cn.mafangui.hotel.enums.OrderStatus;
import cn.mafangui.hotel.response.AjaxResult;
import cn.mafangui.hotel.response.ResponseUtil;
import cn.mafangui.hotel.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 订单接口
 */
@RestController
@RequestMapping(value = "/user/order")
public class UserOrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 添加预订
     * 订单状态默认为未付款状态
     * @param orderTypeId
     * @param orderType
     * @param userId
     * @param name
     * @param phone
     * @param roomTypeId
     * @param roomType
     * @param orderDate
     * @param orderDays
     * @param orderCost
     * @return
     */
    @RequestMapping(value = "/add")
    public AjaxResult addOrder(int orderTypeId,String orderType, int userId,String name, String phone,int roomTypeId, String roomType,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate, Integer orderDays, Double orderCost){
        Order order = new Order(orderTypeId,orderType,userId,name,phone,roomTypeId,
                roomType,orderDate,orderDays, OrderStatus.UNPAID.getCode(),orderCost);
        if(orderService.addOrder(order)!=1)
            return ResponseUtil.failed("创建订单失败");
        return ResponseUtil.success("预订成功");
    }

    /**
     * 客户删除订单
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/deleteByUser")
    public AjaxResult deleteOrderByUser(int orderId){
        Order order = new Order(orderId,OrderStatus.WAS_DELETED.getCode());
        if(orderService.update(order)!=1)
            return ResponseUtil.failed("删除失败");
        return ResponseUtil.success("删除成功");
    }

    /**
     * 修改订单
     * @param orderId
     * @param orderTypeId
     * @param orderType
     * @param userId
     * @param name
     * @param phone
     * @param roomTypeId
     * @param roomType
     * @param orderDate
     * @param orderDays
     * @param orderCost
     * @return
     */
    @RequestMapping(value = "/update")
    public int updateOrder(int orderId,int orderTypeId,String orderType, int userId,String name, String phone,int roomTypeId, String roomType,
                           @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate, Integer orderDays, Double orderCost){
        Order order = new Order(orderTypeId,orderType,userId,name,phone,roomTypeId,
                roomType,orderDate,orderDays, OrderStatus.UNPAID.getCode(),orderCost);
        return orderService.update(order);
    }

    /**
     * 订单支付
     * @param orderId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST,value = "/pay")
    public int payOrder(int orderId){
        return orderService.payOrder(orderId);
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/cancel")
    public int cancelOrder(int orderId){
        return orderService.cancelOrder(orderId);
    }

    /**
     * 订单超时
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/overtime")
    public int orderOver(int orderId){
        Order order = new Order(orderId,OrderStatus.OVERTIME.getCode());
        return orderService.update(order);
    }


    /**
     * 所有订单
     * @return
     */
    @RequestMapping(value = "/all")
    public List<Order> getAllOrder(){
        return orderService.AllOrders();
    }

    /**
     * 根据userID查询所有订单
     * @param userId
     * @return
     */
    @RequestMapping(value = "/withUserId")
    public List<Order> getByUser(int userId){
        return orderService.selectByUserId(userId);
    }

    /**
     * 客户查询个人所有订单（不包括被自己删除的）
     * @return
     */
    @RequestMapping(value = "")
    public AjaxResult getAllByUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        return ResponseUtil.success(orderService.UsersAllOrders(userId));
    }

    /**
     * 根据订单号查询订单
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/withId")
    public Order getById(int orderId){
        return orderService.selectById(orderId);
    }

    /**
     * 根据姓名、预留手机号查找订单
     * 主要用于客户入住
     * @param name
     * @param phone
     * @return
     */
    @RequestMapping(value = "/withNameAndPhone")
    public Order getByNameAndPhone(String name,String phone){
        return orderService.selectByNameAndPhone(name,phone);
    }
}
