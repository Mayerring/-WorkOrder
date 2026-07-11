package com.example.spring_vue_demo.service.convert;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spring_vue_demo.entity.Message;
import com.example.spring_vue_demo.vo.MessageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


/**
 * @author wtt
 * @date 2025/06/07
 */
@Mapper
public interface MessageConverter {
    MessageConverter INSTANCE= Mappers.getMapper(MessageConverter.class);
    Page<MessageVO> toPageMessageVOS(IPage<Message> messageIPage);
    @Mapping(target="typeDesc",expression = "java( com.example.spring_vue_demo.enums.WorkOrderStatusEnum.getByValue(message.getType()).getDesc() )")
    MessageVO toMessageVO(Message message);
}
