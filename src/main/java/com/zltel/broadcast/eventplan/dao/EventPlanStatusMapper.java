package com.zltel.broadcast.eventplan.dao;

import com.zltel.broadcast.eventplan.bean.EventPlanStatus;

public interface EventPlanStatusMapper {
    int deleteByPrimaryKey(Integer status);

    int insert(EventPlanStatus record);

    int insertSelective(EventPlanStatus record);

    EventPlanStatus selectByPrimaryKey(Integer status);

    int updateByPrimaryKeySelective(EventPlanStatus record);

    int updateByPrimaryKey(EventPlanStatus record);
}