package com.example.tomatomall.exception;

/**
 * @Author: DingXiaoyu
 * @Date: 0:26 2023/11/26
 * 你可以在这里自定义Exception
*/

//TODO 在每个类型里添加错误码
public class TomatoMallException extends RuntimeException{

    public TomatoMallException(String message){
        super(message);
    }
    public static TomatoMallException usernameAlreadyExists(){
        return new TomatoMallException("用户名已存在");
    }

    public static TomatoMallException usernameNotFind(){return new TomatoMallException("用户不存在");}

    public static TomatoMallException passwordError(){return new TomatoMallException("密码错误!");}

    public static TomatoMallException notLogin(){
        return new TomatoMallException("未登录!");
    }

    public static TomatoMallException lackOfUsername(){
        return new TomatoMallException("缺少用户名!");
    }

    public static TomatoMallException phoneOrPasswordError(){
        return new TomatoMallException("手机号或密码错误!");
    }

    public static TomatoMallException paramError(){
        return new TomatoMallException("参数错误");
    }

    public static TomatoMallException storeAlreadyExists(){
        return new TomatoMallException("该商店已经存在!");
    }

    public static TomatoMallException storeNotFind(){
        return new TomatoMallException("未找到商店!");
    }

    public static TomatoMallException productAlreadyExists(){
        return new TomatoMallException("该商品已经存在!");
    }

    public static TomatoMallException productNotFind(){
        return new TomatoMallException("商品不存在");
    }

    public static TomatoMallException stockpileNotFind(){
        return new TomatoMallException("库存不存在");
    }

    public static TomatoMallException cartItemQuantityOutOfStock(){
        return new TomatoMallException("购物车商品超出库存");
    }

    public static TomatoMallException cartItemNotFind(){return new TomatoMallException("购物车中商品不存在");}

    public static TomatoMallException cartItemAlreadyExists(){return new TomatoMallException("购物车中已存在该商品");}

    public static TomatoMallException advertisementNotFind(){return new TomatoMallException("该广告不存在");}

    public static TomatoMallException orderNotFound()
    {
        return new TomatoMallException("订单不存在");
    }

    public static TomatoMallException invalidRole()
    {
        return new TomatoMallException("无效的角色类型!");
    }
    
    public static TomatoMallException permissionDenied()
    {
        return new TomatoMallException("权限不足，无法执行此操作!");
    }
    
    public static TomatoMallException notificationNotFind() {
        return new TomatoMallException("消息不存在");
    }

    public static TomatoMallException schoolNotVerified() {
        return new TomatoMallException("用户未通过学校认证");
    }

    public static TomatoMallException chatSessionNotFound() {
        return new TomatoMallException("聊天会话不存在");
    }

    public static TomatoMallException chatMessageNotFound() {
        return new TomatoMallException("聊天消息不存在");
    }
}
