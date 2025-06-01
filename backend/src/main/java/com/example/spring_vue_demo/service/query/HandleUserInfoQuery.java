package com.example.spring_vue_demo.service.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.param.HandleUserInfoParam;

import java.util.List;
import java.util.Objects;

/**
 * @author wtt
 * @date 2025/05/31
 */
public class HandleUserInfoQuery {
    public static LambdaQueryWrapper<HandleUserInfo> getQueryHandleUserInfoWrapper(List<HandleUserInfoParam> handleUserInfos, Integer type) {
        List<Long>userIds = handleUserInfos.stream().map(HandleUserInfoParam::getUserId).toList();
        List<Long>departmentIds=handleUserInfos.stream().map(HandleUserInfoParam::getDepartmentId).toList();
        List<Long>companyIds=handleUserInfos.stream().map(HandleUserInfoParam::getCompanyId).toList();
        LambdaQueryWrapper<HandleUserInfo>wrapper=new LambdaQueryWrapper<HandleUserInfo>()
                .eq(true,HandleUserInfo::getHandleType,type)
                .in(CollectionUtils.isNotEmpty(userIds),HandleUserInfo::getUserId,userIds)
                .in(CollectionUtils.isNotEmpty(departmentIds),HandleUserInfo::getDepartmentId,departmentIds)
                .in(CollectionUtils.isNotEmpty(companyIds),HandleUserInfo::getCompanyId,departmentIds);
        return wrapper;
    }

    public static LambdaQueryWrapper<HandleUserInfo> getPageHandleUserInfoWrapper(List<Long>orderIds) {
        LambdaQueryWrapper<HandleUserInfo>wrapper=new LambdaQueryWrapper<HandleUserInfo>();
        if(CollectionUtils.isEmpty(orderIds)) {
            // 添加一个不可能满足的条件
            wrapper.apply("1=0");
            return wrapper;
        }
        wrapper.in(HandleUserInfo::getOrderId,orderIds);
        return wrapper;
    }

    public static LambdaQueryWrapper<HandleUserInfo> getDetailHandleUserInfoWrapper(Long orderId) {
        LambdaQueryWrapper<HandleUserInfo>wrapper=new LambdaQueryWrapper<HandleUserInfo>()
                .eq(Objects.nonNull(orderId),HandleUserInfo::getOrderId,orderId);
        return wrapper;
    }
}
