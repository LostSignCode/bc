package com.zltel.broadcast.um.service;

import java.util.Map;

import com.zltel.broadcast.common.json.R;
import com.zltel.broadcast.um.bean.IntegralConstitute;

public interface IntegralConstituteService {
	int deleteByPrimaryKey(Integer icId);

    int insert(IntegralConstitute record);

    int insertSelective(IntegralConstitute record);

    IntegralConstitute selectByPrimaryKey(Integer icId);

    int updateByPrimaryKeySelective(IntegralConstitute record);

    int updateByPrimaryKey(IntegralConstitute record);
    
    /**
     * 添加组织积分结构
     * @param conditions
     * @return
     */
    public R insertIntegralConstitute(IntegralConstitute ic);
    
    /**
     * 查询拥有积分结构的组织
     * @param conditions
     * @return
     */
    public R queryOrgInfoForIc(Map<String, Object> conditions, int pageNum, int pageSize);
    
    /**
     * 查询该组织拥有的党员，仅为党员积分功能服务
     * @param conditions
     * @return
     */
    public R queryPartyUserInfoAndIcInfo(Map<String, Object> conditions, int pageNum, int pageSize);

}
