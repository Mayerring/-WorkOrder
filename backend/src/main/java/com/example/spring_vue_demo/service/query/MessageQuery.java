package com.example.spring_vue_demo.service.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.spring_vue_demo.entity.Message;

/**
 * @author wtt
 * @date 2025/06/07
 */
public class MessageQuery {

    public static LambdaQueryWrapper<Message> getByReceiverIdWrapper(Long receiverId) {
        LambdaQueryWrapper<Message>wrapper=new LambdaQueryWrapper<Message>()
                .eq(true,Message::getReceiverId,receiverId)
                .orderByDesc(Message::getSendTime);
        return wrapper;
    }
}
