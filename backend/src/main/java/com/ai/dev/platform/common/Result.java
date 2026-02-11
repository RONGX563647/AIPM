package com.ai.dev.platform.common;

/**
 * 统一响应结果封装类
 * 
 * <p>用于封装API接口的统一响应格式，提供标准化的返回结构：
 * <ul>
 *   <li>状态码(code)：表示请求处理结果的状态</li>
 *   <li>消息(msg)：对处理结果的描述信息</li>
 *   <li>数据(data)：具体的业务数据内容</li>
 * </ul>
 * 
 * <p>该类使用泛型设计，支持任意类型的数据返回，
 * 确保前后端数据交互的一致性和可预测性。
 * 
 * @param <T> 响应数据的类型参数
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public class Result<T> {
    
    /**
     * 响应状态码
     * 
     * <p>用于标识请求处理的结果状态：
     * <ul>
     *   <li>0：成功</li>
     *   <li>-1：失败</li>
     *   <li>其他自定义状态码</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    private int code;
    
    /**
     * 响应消息
     * 
     * <p>对处理结果的文本描述，用于：
     * <ul>
     *   <li>成功时返回"success"</li>
     *   <li>失败时返回具体的错误信息</li>
     *   <li>提供用户友好的提示信息</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    private String msg;
    
    /**
     * 响应数据
     * 
     * <p>具体的业务数据内容，可以是：
     * <ul>
     *   <li>查询结果列表</li>
     *   <li>单个对象详情</li>
     *   <li>分页数据</li>
     *   <li>统计信息</li>
     *   <li>null（无数据返回）</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    private T data;

    /**
     * 无参构造函数
     * 
     * <p>创建空的响应结果对象，各属性需要后续手动设置。
     * 
     * @since 1.0.0
     */
    public Result() {}

    /**
     * 全参构造函数
     * 
     * <p>创建包含完整信息的响应结果对象。
     * 
     * @param code 响应状态码
     * @param msg 响应消息
     * @param data 响应数据
     * @since 1.0.0
     */
    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 创建成功响应
     * 
     * <p>创建表示操作成功的响应结果：
     * <ul>
     *   <li>状态码设置为0</li>
     *   <li>消息设置为"success"</li>
     *   <li>包含具体的业务数据</li>
     * </ul>
     * 
     * @param <T> 数据类型参数
     * @param data 成功时返回的业务数据
     * @return Result<T> 成功响应结果对象
     * @since 1.0.0
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "success", data);
    }

    /**
     * 创建错误响应
     * 
     * <p>创建表示操作失败的响应结果：
     * <ul>
     *   <li>状态码设置为-1</li>
     *   <li>消息设置为传入的错误信息</li>
     *   <li>数据设置为null</li>
     * </ul>
     * 
     * @param <T> 数据类型参数
     * @param msg 错误信息描述
     * @return Result<T> 错误响应结果对象
     * @since 1.0.0
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(-1, msg, null);
    }

    // getters & setters
    
    /**
     * 获取响应状态码
     * 
     * @return int 响应状态码
     * @since 1.0.0
     */
    public int getCode() { return code; }
    
    /**
     * 设置响应状态码
     * 
     * @param code 响应状态码
     * @since 1.0.0
     */
    public void setCode(int code) { this.code = code; }
    
    /**
     * 获取响应消息
     * 
     * @return String 响应消息
     * @since 1.0.0
     */
    public String getMsg() { return msg; }
    
    /**
     * 设置响应消息
     * 
     * @param msg 响应消息
     * @since 1.0.0
     */
    public void setMsg(String msg) { this.msg = msg; }
    
    /**
     * 获取响应数据
     * 
     * @return T 响应数据
     * @since 1.0.0
     */
    public T getData() { return data; }
    
    /**
     * 设置响应数据
     * 
     * @param data 响应数据
     * @since 1.0.0
     */
    public void setData(T data) { this.data = data; }
}
