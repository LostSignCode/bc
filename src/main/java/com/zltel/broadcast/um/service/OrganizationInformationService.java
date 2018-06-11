package com.zltel.broadcast.um.service;

import java.util.Map;

import com.zltel.broadcast.common.json.R;
import com.zltel.broadcast.um.bean.OrganizationInformation;

public interface OrganizationInformationService {
	int deleteByPrimaryKey(Integer orgInfoId);

    int insert(OrganizationInformation record);

    int insertSelective(OrganizationInformation record);

    OrganizationInformation selectByPrimaryKey(Integer orgInfoId);

    int updateByPrimaryKeySelective(OrganizationInformation record);

    int updateByPrimaryKeyWithBLOBs(OrganizationInformation record);

    int updateByPrimaryKey(OrganizationInformation record);
    
    /**
     * 根据条件查询组织信息
     * @param organizationInformation 条件
     * @return
     */
    public R queryOrgInfos(OrganizationInformation organizationInformation) throws Exception;
    
    
    /**
     * 根据条件查询组织信息
     * @param organizationInformation 条件
     * @return
     */
    public R queryOrgInfosForMap(Map<String, Object> organizationInformation, int pageNum, int pageSize) throws Exception;
    
    /**
     * 查询当前组织下的所有子组织
     * @param organizationInformation
     * @return
     * @throws Exception
     */
    public R queryThisOrgChildren(Map<String, Object> organizationInformation, int pageNum, int pageSize) throws Exception;
    
    /**
     * 根据条件查询组织信息树
     * @param organizationInformation
     * @return
     * @throws Exception
     */
    public R queryOrgInfosToTree(OrganizationInformation organizationInformation) throws Exception;
    
    /**
     * 查询省份
     * @param organizationInformation
     * @return
     */
    public R queryOrgInfosCommitteeProvince(OrganizationInformation organizationInformation) throws Exception;
    
    /**
     * 查询城市
     * @param organizationInformation
     * @return
     */
    public R queryOrgInfosCommitteeCity(OrganizationInformation organizationInformation) throws Exception;
    
    /**
     * 查询地区
     * @param organizationInformation
     * @return
     */
    public R queryOrgInfosCommitteeArea(OrganizationInformation organizationInformation) throws Exception;
    
    /**
     * 新增组织信息
     * @param OrganizationInformation
     * @return
     * @throws Exception
     */
    public R insertOrgInfo(OrganizationInformation organizationInformation) throws Exception;
    
    /**
     * 修改党员信息
     * @param organizationInformation
     * @return
     * @throws Exception
     */
    public R updateOrgInfo(OrganizationInformation organizationInformation) throws Exception;
    
    /**
     * 删除组织
     * @param organizationInformation
     * @return
     * @throws Exception
     */
    public R deleteOrgInfo(OrganizationInformation organizationInformation) throws Exception;
}