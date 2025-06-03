package com.example.spring_vue_demo.service.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum;
import com.example.spring_vue_demo.param.HandleUserInfoParam;

import java.util.List;

/**
 * @author wtt
 * @date 2025/05/31
 */
public class HandleUserInfoQuery {
    public static LambdaQueryWrapper<HandleUserInfo> getWorkOrderPageWrapper(List<HandleUserInfoParam> handleUserInfos, Integer type) {
        List<Long>userIds = handleUserInfos.stream().map(HandleUserInfoParam::getUserId).toList();
        List<String>departmentCodes=handleUserInfos.stream().map(HandleUserInfoParam::getDepartmentCode).toList();
        List<String>companyCodes=handleUserInfos.stream().map(HandleUserInfoParam::getCompanyCode).toList();
        LambdaQueryWrapper<HandleUserInfo>wrapper=new LambdaQueryWrapper<HandleUserInfo>()
                .eq(true,HandleUserInfo::getHandleType,type)
                .in(CollectionUtils.isNotEmpty(userIds),HandleUserInfo::getUserId,userIds)
                .in(CollectionUtils.isNotEmpty(departmentCodes),HandleUserInfo::getDepartmentCode,departmentCodes)
                .in(CollectionUtils.isNotEmpty(companyCodes),HandleUserInfo::getCompanyCode,departmentCodes);
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

    public static LambdaQueryWrapper<HandleUserInfo> getOrderIdWrapper(Long orderId) {
        LambdaQueryWrapper<HandleUserInfo>wrapper=new LambdaQueryWrapper<HandleUserInfo>()
                .eq(true,HandleUserInfo::getOrderId,orderId);
        return wrapper;
    }

    public static LambdaUpdateWrapper<HandleUserInfo> getUpdateStatusWrapper(Long orderId, List<Long> staffIds,Integer handleType,Boolean finished) {
        LambdaUpdateWrapper<HandleUserInfo>wrapper=new LambdaUpdateWrapper<HandleUserInfo>();
        if(CollectionUtils.isEmpty(staffIds)) {
            // 添加一个不可能满足的条件
            wrapper.apply("1=0");
            return wrapper;
        }
        wrapper.eq(true,HandleUserInfo::getOrderId,orderId)
                .in(true,HandleUserInfo::getUserId,staffIds)
                .eq(true,HandleUserInfo::getHandleType,handleType)
                .set(HandleUserInfo::getFinished,finished);
        return wrapper;
    }

    public static LambdaQueryWrapper<HandleUserInfo> getHandleTypeWrapper(Long orderId, Integer handleType) {
        LambdaQueryWrapper<HandleUserInfo>wrapper=new LambdaQueryWrapper<HandleUserInfo>()
                .eq(true,HandleUserInfo::getOrderId,orderId)
                .eq(true,HandleUserInfo::getHandleType,handleType);
        return wrapper;
    }

    public static LambdaQueryWrapper<HandleUserInfo> getHandleTypeUserIdWrapper(Long orderId, Integer handleType, Long staffId) {
        LambdaQueryWrapper<HandleUserInfo>wrapper=new LambdaQueryWrapper<HandleUserInfo>()
                .eq(true,HandleUserInfo::getOrderId,orderId)
                .eq(true,HandleUserInfo::getHandleType,handleType)
                .eq(true,HandleUserInfo::getUserId,staffId);
        return wrapper;
    }
}
