package com.livestream.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    
    // 服务端错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    
    // 业务错误 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_DISABLED(1003, "用户已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(1004, "用户名或密码错误"),
    
    // 直播业务错误 2xxx
    LIVE_ROOM_NOT_FOUND(2001, "直播间不存在"),
    LIVE_ROOM_CLOSED(2002, "直播间已关闭"),
    LIVE_ROOM_BANNED(2003, "直播间已被封禁"),
    NOT_LIVE_OWNER(2004, "非直播间所有者"),
    
    // 短视频业务错误 3xxx
    VIDEO_NOT_FOUND(3001, "视频不存在"),
    VIDEO_UPLOAD_FAILED(3002, "视频上传失败"),
    VIDEO_AUDIT_REJECTED(3003, "视频审核未通过"),
    
    // 礼物/钱包业务错误 4xxx
    GIFT_NOT_FOUND(4001, "礼物不存在"),
    COIN_INSUFFICIENT(4002, "金币不足"),
    WALLET_NOT_FOUND(4003, "钱包不存在"),
    
    // 参数校验错误 5xxx
    VALIDATION_ERROR(5001, "参数校验失败"),
    
    // 验证码错误 6xxx
    CODE_EXPIRED(6001, "验证码已过期"),
    CODE_ERROR(6002, "验证码错误"),
    
    // 权限错误 7xxx
    TOKEN_EXPIRED(7001, "Token已过期"),
    TOKEN_INVALID(7002, "Token无效"),
    
    // 弹幕错误 8xxx
    DANMAKU_FREQUENT(8001, "发送过于频繁"),
    DANMAKU_BANNED(8002, "已被禁言");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
