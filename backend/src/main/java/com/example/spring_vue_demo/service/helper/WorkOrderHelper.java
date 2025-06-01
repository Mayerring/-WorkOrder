package com.example.spring_vue_demo.service.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.enums.HandleUserInfoHandleTypeEnum;
import com.example.spring_vue_demo.param.HandleUserInfoParam;
import com.example.spring_vue_demo.service.HandleUserInfoService;
import com.example.spring_vue_demo.service.query.HandleUserInfoQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wtt
 * @date 2025/05/31
 */
@Component
@RequiredArgsConstructor
public class WorkOrderHelper {
    private final HandleUserInfoService iHandleUserInfoService;
    public void setQueryWorkOrderIds(List<HandleUserInfo> handleUserInfos, List<HandleUserInfoParam>userInfoParams, HandleUserInfoHandleTypeEnum userInfoType){
        if(CollectionUtils.isEmpty(userInfoParams)){
            return;
        }
        LambdaQueryWrapper<HandleUserInfo> checkerWrapper= HandleUserInfoQuery.getQueryHandleUserInfoWrapper(userInfoParams, userInfoType.getValue());
        List<HandleUserInfo> submitterInfolist = iHandleUserInfoService.list(checkerWrapper);
        handleUserInfos.addAll(submitterInfolist);
    }
}
