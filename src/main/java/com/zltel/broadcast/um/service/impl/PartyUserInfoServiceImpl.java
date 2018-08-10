package com.zltel.broadcast.um.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.zltel.broadcast.common.json.R;
import com.zltel.broadcast.common.support.BaseDao;
import com.zltel.broadcast.common.support.BaseDaoImpl;
import com.zltel.broadcast.common.util.AdminRoleUtil;
import com.zltel.broadcast.common.util.CacheUtil;
import com.zltel.broadcast.common.util.PasswordHelper;
import com.zltel.broadcast.um.bean.BaseUserInfo;
import com.zltel.broadcast.um.bean.DeedsType;
import com.zltel.broadcast.um.bean.OrganizationRelation;
import com.zltel.broadcast.um.bean.PartyUserInfo;
import com.zltel.broadcast.um.bean.SysRole;
import com.zltel.broadcast.um.bean.SysUser;
import com.zltel.broadcast.um.bean.SysUserRole;
import com.zltel.broadcast.um.dao.BaseUserInfoMapper;
import com.zltel.broadcast.um.dao.DeedsTypeMapper;
import com.zltel.broadcast.um.dao.DeedsUserMapper;
import com.zltel.broadcast.um.dao.PartyUserInfoMapper;
import com.zltel.broadcast.um.dao.SysRoleMapper;
import com.zltel.broadcast.um.dao.SysUserMapper;
import com.zltel.broadcast.um.dao.SysUserRoleMapper;
import com.zltel.broadcast.um.service.OrganizationRelationService;
import com.zltel.broadcast.um.service.PartyUserInfoService;
import com.zltel.broadcast.um.util.DateUtil;
import com.zltel.broadcast.um.util.FileUtil;

/**
 * 党员信息
 * @author imzhy
 * @since jdk 1.8.0_172
 * Date: 2018.05.29
 */
@Service
@ConfigurationProperties(prefix = "party.user")
public class PartyUserInfoServiceImpl extends BaseDaoImpl<PartyUserInfo> implements PartyUserInfoService {
	
	@Resource
	private PartyUserInfoMapper partyUserInfoMapper;
	
	@Resource
	private BaseUserInfoMapper baseUserInfoMapper;
	
	@Resource
	private OrganizationRelationService organizationRelationService;
	
	@Resource
	private SysUserMapper sysUserMapper;
	@Resource
    private DeedsUserMapper deedsUserMapper;
	@Resource
	private SysRoleMapper sysRoleMapper;
	@Resource
    private DeedsTypeMapper deedsTypeMapper;
	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	
	@Override
    public BaseDao<PartyUserInfo> getInstince() {
        return this.partyUserInfoMapper;
    }
	
	private String uploadIdPhotoTempPath = "";
	private String uploadIdPhotoPath = "";
	
	/**
	 * 查询党员信息
	 * @param partyUserMap
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor=java.lang.Exception.class)
	public R queryPartyUserInfos(Map<String, Object> partyUserMap, int pageNum, int pageSize) throws Exception {
		Subject subject = SecurityUtils.getSubject();
        SysUser sysUser = (SysUser) subject.getPrincipal();
        if (partyUserMap == null) partyUserMap = new HashMap<>();
        
    	if (AdminRoleUtil.isPlantAdmin()) {	//如果是平台管理员
        	//不做任何处理
        } else if (AdminRoleUtil.isOrgAdmin()) {	//如果是组织管理员
        	if (sysUser.getOrgId() == null) {
        		return R.ok().setCode(100).setMsg("组织管理员请设置所属的组织，如果是党员请加入组织");
        	}
        	partyUserMap.put("orgInfoId", sysUser.getOrgId());
        } else {	//如果是个人账户
        	if (sysUser.getUserType() == 0) {
        		return R.ok().setCode(100).setMsg("非党员账户请先设置管理员类型");
        	} else if (sysUser.getUserType() == 1) {
            	partyUserMap.put("idCard", sysUser.getUsername());
            }
        }
        
        
		
		PageHelper.startPage(pageNum, pageSize);
		List<Map<String, Object>> partyUserInfos = partyUserInfoMapper.queryPartyUserInfos(partyUserMap);	//开始查询，没有条件则查询所有组织关系
		PageInfo<Map<String, Object>> partyUserInfosPageInfo = new PageInfo<>(partyUserInfos);
		if (partyUserInfosPageInfo != null && partyUserInfosPageInfo.getList() != null && partyUserInfosPageInfo.getList().size() > 0) {	//是否查询到数据
			List<DeedsType> dts = deedsTypeMapper.queryDeedsTypes(null);
			for (Map<String, Object> partyUserInfo : partyUserInfos) {
				partyUserInfo.put("birthDate", 
						DateUtil.formatDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("birthDate") == null ||
						partyUserInfo.get("birthDate") == "" ? null : DateUtil.toDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("birthDate").toString())));
				partyUserInfo.put("enrolmentTime", 
						DateUtil.formatDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("enrolmentTime") == null ||
						partyUserInfo.get("enrolmentTime") == "" ? null : (Date)partyUserInfo.get("enrolmentTime")));
				partyUserInfo.put("graduationTime", 
						DateUtil.formatDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("graduationTime") == null ||
						partyUserInfo.get("graduationTime") == "" ? null : (Date)partyUserInfo.get("graduationTime")));
				partyUserInfo.put("joinDateFormal", 
						DateUtil.formatDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("joinDateFormal") == null ||
						partyUserInfo.get("joinDateFormal") == "" ? null : (Date)partyUserInfo.get("joinDateFormal")));
				partyUserInfo.put("joinDateReserve", 
						DateUtil.formatDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("joinDateReserve") == null ||
						partyUserInfo.get("joinDateReserve") == "" ? null : (Date)partyUserInfo.get("joinDateReserve")));
				partyUserInfo.put("joinWorkDate", 
						DateUtil.formatDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("joinWorkDate") == null ||
						partyUserInfo.get("joinWorkDate") == "" ? null : (Date)partyUserInfo.get("joinWorkDate")));
				partyUserInfo.put("typeName", partyUserInfo.get("type") == null || partyUserInfo.get("type") == "" ? 
						null : (int)partyUserInfo.get("type") == 1 ? "正式党员" : "预备党员");
				partyUserInfo.put("statusName", partyUserInfo.get("type") == null || partyUserInfo.get("type") == "" ? 
						null : (int)partyUserInfo.get("status") == 1 ? "正常" : "停止党籍");
				partyUserInfo.put("partyStaffName", partyUserInfo.get("partyStaff") == null || partyUserInfo.get("partyStaff") == "" ? 
						null : (int)partyUserInfo.get("partyStaff") == 1 ? "是" : "否");
				partyUserInfo.put("partyRepresentativeName", partyUserInfo.get("partyRepresentative") == null || partyUserInfo.get("partyRepresentative") == "" ?
						null : (int)partyUserInfo.get("partyRepresentative") == 1 ? "是" : "否");
				partyUserInfo.put("volunteerName", partyUserInfo.get("volunteer") == null || partyUserInfo.get("volunteer") == "" ?
						null : (int)partyUserInfo.get("volunteer") == 1 ? "是" : "否");
				partyUserInfo.put("difficultUserName", partyUserInfo.get("difficultUser") == null || partyUserInfo.get("difficultUser") == "" ?
						null : (int)partyUserInfo.get("difficultUser") == 1 ? "是" : "否");
				partyUserInfo.put("advancedUserName", partyUserInfo.get("advancedUser") == null || partyUserInfo.get("advancedUser") == "" ?
						null : (int)partyUserInfo.get("advancedUser") == 1 ? "是" : "否");
				partyUserInfo.put("positiveUserName", partyUserInfo.get("positiveUser") == null || partyUserInfo.get("positiveUser") == "" ?
						null : (int)partyUserInfo.get("positiveUser") == 1 ? "是" : "否");
				partyUserInfo.put("developUserName", partyUserInfo.get("developUser") == null || partyUserInfo.get("developUser") == "" ?
						null : (int)partyUserInfo.get("developUser") == 1 ? "是" : "否");
				partyUserInfo.put("missingUserName", partyUserInfo.get("missingUser") == null || partyUserInfo.get("missingUser") == "" ?
						null : (int)partyUserInfo.get("missingUser") == 1 ? "是" : "否");
				Date birthDay = DateUtil.toDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("birthDate") == null || partyUserInfo.get("birthDate") == "" ?
						null : partyUserInfo.get("birthDate").toString());
				partyUserInfo.put("age", getPartyUserAge(birthDay));
				Date joinDateFormal = DateUtil.toDate(DateUtil.YYYY_MM_DD, partyUserInfo.get("joinDateFormal") == null || partyUserInfo.get("joinDateFormal") == "" ?
						null : partyUserInfo.get("joinDateFormal").toString());
				partyUserInfo.put("joinDateFormalAge", getPartyUserAge(joinDateFormal));
				
				
				if (dts != null && dts.size() > 0) {
					Map<String, Object> conditions = new HashMap<>();
					for (DeedsType deedsType : dts) {
						conditions.put("deedsTypeId", deedsType.getId());
						conditions.put("userId", partyUserInfo.get("id"));
						List<Map<String, Object>> dusMap = deedsUserMapper.queryDeedsUsers(conditions);
						if (dusMap != null && dusMap.size() > 0 && "个人荣誉".equals(deedsType.getName())) {
							for (Map<String, Object> map : dusMap) {
								map.put("occurrenceTime", 
										DateUtil.formatDate(DateUtil.YYYY_MM_DD, map.get("occurrenceTime") == null ||
												map.get("occurrenceTime") == "" ? null : (Date)map.get("occurrenceTime")));
							}
						}
						partyUserInfo.put(deedsType.getName(), dusMap);
					}
				}
			}
			return R.ok().setData(partyUserInfosPageInfo).setMsg("查询党员信息成功");
		} else {
			return R.ok().setMsg("没有查询到党员信息");
		}
	}
	
	/**
	 * 得到党员证件照
	 * @param idPhotoPath 参数
	 * @return
	 */
    public void getPartyUserInfoIdPhoto(HttpServletResponse response, int partyId) throws Exception {
    	Map<String, Object> partyIdMap = new HashMap<String, Object>();
    	partyIdMap.put("id", partyId);
    	List<Map<String, Object>> partyUserInfos = partyUserInfoMapper.queryPartyUserInfos(partyIdMap);
    	if (partyUserInfos != null && !partyUserInfos.isEmpty()) {
    		response.setHeader("Content-Type","image/jped");
        	
    		byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            InputStream bis = null;
            if (StringUtil.isEmpty((String)partyUserInfos.get(0).get("idPhoto"))) {
            	bis = this.getClass().getResourceAsStream("/noFoundIdPhoto.jpg");
            } else {            	
            	bis = new FileInputStream(new File(uploadIdPhotoPath + File.separator + (String)partyUserInfos.get(0).get("idPhoto")));
            }
            try {
    		int i = bis.read(buff);
    		while (i != -1) {
    			os.write(buff, 0, i);
    			os.flush();
    			i = bis.read(buff);
    		}
            }finally {
                IOUtils.closeQuietly(bis);
                IOUtils.closeQuietly(os);
            }
    	}
    }
    
    /**
     * 根据党员生日得到当前年龄
     * @param birthDay
     * @return
     */
    public static Integer getPartyUserAge(Date birthDay) throws Exception {
    	if (birthDay == null) return 0;
    	Calendar cal = Calendar.getInstance();  
        int yearNow = cal.get(Calendar.YEAR);  
        int monthNow = cal.get(Calendar.MONTH);  
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
        
        cal.setTime(birthDay);  
  
        int yearBirth = cal.get(Calendar.YEAR);  
        int monthBirth = cal.get(Calendar.MONTH);  
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
  
        int age = yearNow - yearBirth;  
  
        if (monthNow <= monthBirth) {  
            if (monthNow == monthBirth) {  
                if (dayOfMonthNow < dayOfMonthBirth) age--;  
            }else{  
                age--;  
            }  
        }  
        return age;  
    }
    
    /**
     * 暂时存储用户图片
     * @param request
     * @param file
     * @throws Exception
     */
    public R savePartyUserInfoIdPhoto(HttpServletRequest request, MultipartFile file) throws Exception {
    	if (file == null) return R.error().setMsg("未选择图片");
    	String partyUserIdPhotoTempFileName = UUID.randomUUID().toString() + "." + FileUtil.getFileSuffix(file.getOriginalFilename());	//临时文件文件名
    	FileUtil.writeFile(file.getInputStream(), uploadIdPhotoTempPath, partyUserIdPhotoTempFileName);
    	request.getSession().setAttribute("partyUserIdPhotoFileName", file.getOriginalFilename());
    	return R.ok().setData(partyUserIdPhotoTempFileName);	//返回文件名
    }
    
    /**
     * 添加党员
     * @param request
     * @param partyUser
     * @throws Exception
     */
    @Override
	@Transactional(rollbackFor=java.lang.Exception.class)
    public R insertPartyUserInfo(HttpServletRequest request, Map<String, Object> partyUser) throws Exception {
    	if (partyUser == null) 
    		return R.error().setMsg("发生错误");
    	BaseUserInfo baseUserInfo = new BaseUserInfo();
    	PartyUserInfo partyUserInfo = new PartyUserInfo();
    	//照片地址
    	baseUserInfo.setName((String)partyUser.get("name"));
    	baseUserInfo.setSex((String)partyUser.get("sex"));
    	baseUserInfo.setNativePlace((String)partyUser.get("nativePlace"));
    	baseUserInfo.setBirthDate(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("birthDate")));
    	baseUserInfo.setNation(StringUtil.isEmpty((String)partyUser.get("nation")) ? 
    			null : Integer.parseInt((String)partyUser.get("nation")));
    	baseUserInfo.setIdCard((String)partyUser.get("idCard"));
    	baseUserInfo.setMobilePhone((String)partyUser.get("mobilePhone"));
    	baseUserInfo.setEmail((String)partyUser.get("email"));
    	baseUserInfo.setQq((String)partyUser.get("qq"));
    	baseUserInfo.setWechat((String)partyUser.get("wechat"));
    	baseUserInfo.setEducation(StringUtil.isEmpty((String)partyUser.get("education")) ? 
    			null : Integer.parseInt((String)partyUser.get("education")));
    	baseUserInfo.setAcademicDegree(StringUtil.isEmpty((String)partyUser.get("academicDegree")) ? 
    			null : Integer.parseInt((String)partyUser.get("academicDegree")));
    	baseUserInfo.setEnrolmentTime(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("enrolmentTime")));
    	baseUserInfo.setGraduationTime(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("graduationTime")));
    	baseUserInfo.setGraduationSchool((String)partyUser.get("graduationSchool"));
    	baseUserInfo.setMajor((String)partyUser.get("major"));
    	baseUserInfo.setSpecialityLiterature((String)partyUser.get("specialityLiterature"));
    	baseUserInfo.setSpecialityMajor((String)partyUser.get("specialityMajor"));
    	baseUserInfo.setMarriageStatus((String)partyUser.get("marriageStatus"));
    	baseUserInfo.setChildrenStatus((String)partyUser.get("childrenStatus"));
		baseUserInfo.setHomeAddressProvince((String)partyUser.get("homeAddressProvince"));
		baseUserInfo.setHomeAddressCity((String)partyUser.get("homeAddressCity"));
		baseUserInfo.setHomeAddressArea((String)partyUser.get("homeAddressArea"));
    	baseUserInfo.setHomeAddressDetail((String)partyUser.get("homeAddressDetail"));
		baseUserInfo.setPresentAddressProvince((String)partyUser.get("presentAddressProvince"));
		baseUserInfo.setPresentAddressCity((String)partyUser.get("presentAddressCity"));
		baseUserInfo.setPresentAddressArea((String)partyUser.get("presentAddressArea"));
    	baseUserInfo.setPresentAddressDetail((String)partyUser.get("presentAddressDetail"));
    	baseUserInfo.setIsParty(StringUtil.isEmpty((String)partyUser.get("isParty")) ? 
    			null : Integer.parseInt((String)partyUser.get("isParty")));
    	baseUserInfo.setPositiveUser(StringUtil.isEmpty((String)partyUser.get("positiveUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("positiveUser")));
    	baseUserInfo.setWorkUnit((String)partyUser.get("workUnit"));
    	baseUserInfo.setWorkNature(StringUtil.isEmpty((String)partyUser.get("workNature")) ? 
    			null : Integer.parseInt((String)partyUser.get("workNature")));
    	baseUserInfo.setJoinWorkDate(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("joinWorkDate")));
    	baseUserInfo.setAppointmentTimeLength(StringUtil.isEmpty((String)partyUser.get("appointmentTimeLength")) ? 
    			null : Integer.parseInt((String)partyUser.get("appointmentTimeLength")));
    	baseUserInfo.setFirstLineSituation(StringUtil.isEmpty((String)partyUser.get("firstLineTypeName")) ? 
    			null : Integer.parseInt((String)partyUser.get("firstLineTypeName")));
    	
    	partyUserInfo.setType(StringUtil.isEmpty((String)partyUser.get("type")) ? 
    			null : Integer.parseInt((String)partyUser.get("type")));
    	partyUserInfo.setStatus(StringUtil.isEmpty((String)partyUser.get("status")) ? 
    			null : Integer.parseInt((String)partyUser.get("status")));
    	partyUserInfo.setJoinDateFormal(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("joinDateFormal")));
    	partyUserInfo.setJoinDateReserve(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("joinDateReserve")));
    	partyUserInfo.setJoinPartyBranchTypeId(StringUtil.isEmpty((String)partyUser.get("joinPartyBranchType")) ? 
    			null : Integer.parseInt((String)partyUser.get("joinPartyBranchType")));
    	partyUserInfo.setPartyStaff(StringUtil.isEmpty((String)partyUser.get("partyStaff")) ? 
    			null : Integer.parseInt((String)partyUser.get("partyStaff")));
    	partyUserInfo.setPartyRepresentative(StringUtil.isEmpty((String)partyUser.get("partyRepresentative")) ? 
    			null : Integer.parseInt((String)partyUser.get("partyRepresentative")));
    	partyUserInfo.setVolunteer(StringUtil.isEmpty((String)partyUser.get("volunteer")) ? 
    			null : Integer.parseInt((String)partyUser.get("volunteer")));
    	partyUserInfo.setDifficultUser(StringUtil.isEmpty((String)partyUser.get("difficultUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("difficultUser")));
    	partyUserInfo.setAdvancedUser(StringUtil.isEmpty((String)partyUser.get("advancedUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("advancedUser")));
    	partyUserInfo.setDevelopUser(StringUtil.isEmpty((String)partyUser.get("developUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("developUser")));
    	partyUserInfo.setMissingUser(StringUtil.isEmpty((String)partyUser.get("missingUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("missingUser")));
    	partyUserInfo.setIntroduce((String)partyUser.get("introduce"));
    	
    	String partyUserIdPhotoTempFileName = partyUser.get("partyUserIdPhotoTempFileName") == null ? null : partyUser.get("partyUserIdPhotoTempFileName").toString();	//临时照片文件名
    	if (StringUtil.isEmpty(partyUserIdPhotoTempFileName)) {	//没有用户证件照
    		throw new Exception();
    	}
    	
    	String idPhotoPathTemp = uploadIdPhotoTempPath + File.separator + partyUserIdPhotoTempFileName;	//临时照片全路径
    	String idPhotoPath = uploadIdPhotoPath;	//上传照片文件夹路径
    	String idPhotoName = baseUserInfo.getIdCard() + File.separator + request.getSession().getAttribute("partyUserIdPhotoFileName").toString();	//上传照片文件名
    	baseUserInfo.setIdPhoto(idPhotoName);	//上传照片全路径
    	int insertBaseUserInfoCount = baseUserInfoMapper.insertSelective(baseUserInfo);	//保存基础信息
    	List<BaseUserInfo> baseUserInfos = baseUserInfoMapper.queryBaseUserInfos(baseUserInfo);	//查询基础信息id
    	if (insertBaseUserInfoCount != 1 || baseUserInfos == null ? true : baseUserInfos.size() != 1 ? true : false) {	//插入失败，抛异常回滚
    		throw new Exception();
    	}
    	if (baseUserInfo.getIsParty() == 1) {
    		partyUserInfo.setPartyUserId(baseUserInfos.get(0).getBaseUserId());	//设置id
        	int insertPartyUserInfoCount = partyUserInfoMapper.insertSelective(partyUserInfo);	//保存党员信息
        	if (insertPartyUserInfoCount != 1) {	//插入失败，抛异常回滚
        		throw new Exception();
        	}
        	
        	SysUser su = new SysUser();
        	su.setUsername(baseUserInfo.getIdCard());
        	su.setPassword(baseUserInfo.getIdCard().substring(baseUserInfo.getIdCard().length() - 6));
        	String salt = UUID.randomUUID().toString();
        	su.setSalt(salt);	//保存盐
        	su.setPassword(PasswordHelper.encryptPassword(su.getPassword(), salt));	//加密
        	su.setEmail(baseUserInfo.getEmail());
        	su.setMobile(baseUserInfo.getMobilePhone());
        	su.setStatus(true);
        	su.setUserType(1);
        	su.setCreateTime(new Date());
			int count = sysUserMapper.insertSelective(su);
			if (count != 1) {
				throw new Exception();
			}
			//赋予默认角色
			SysRole sysRole = new SysRole();
			sysRole.setRoleName("party_role");
			List<SysRole> srs = sysRoleMapper.querySysRoles(sysRole);
			if (srs != null && srs.size() == 1) {
				SysUserRole sur = new SysUserRole();
				sur.setUserId((long)su.getUserId());
				sur.setRoleId(srs.get(0).getRoleId());
				count = sysUserRoleMapper.insertSelective(sur);
				if (count != 1) {
					throw new Exception();
				}
			} else {
				throw new Exception();
			}
    	}
    	FileUtil.writeFile(new FileInputStream(new File(idPhotoPathTemp)), idPhotoPath, idPhotoName);	//保存证件照
    	return R.ok().setMsg("党员信息注册成功");
    }
    
    /**
     * 修改党员证件照
     */
    @Override
	@Transactional(rollbackFor=java.lang.Exception.class)
    public R updatePartyUserIdPhoto(HttpServletRequest request, MultipartFile file, Map<String, Object> partyUser) throws Exception {
    	if (file == null) 
    		return R.error().setMsg("为选择图片");
    	List<Map<String, Object>> puiMaps = partyUserInfoMapper.queryPartyUserInfos(partyUser);
    	if (puiMaps != null && puiMaps.size() == 1) {
    		String idPhotoPath = uploadIdPhotoPath;	//上传照片文件夹路径
    		String idPhotoName = puiMaps.get(0).get("idCard") + File.separator + file.getOriginalFilename();	//上传照片文件名
    		FileUtil.writeFile(file.getInputStream(), idPhotoPath, idPhotoName);	//保存证件照
    		BaseUserInfo bui = new BaseUserInfo();
    		bui.setBaseUserId(Integer.parseInt(puiMaps.get(0).get("id").toString()));
    		bui.setIdPhoto(idPhotoName);
    		int updateBaseUserInfoCount = baseUserInfoMapper.updateByPrimaryKeySelective(bui);
    		if (updateBaseUserInfoCount != 1) {	//更新失败，抛异常回滚
        		throw new Exception();
        	}
    		return R.ok().setMsg("证件照更新成功");
    	} else {
    		throw new Exception();
    	}
    }
    
    /**
     * 修改党员信息
     * @param request
     * @param partyUser
     * @throws Exception
     */
    @Override
	@Transactional(rollbackFor=java.lang.Exception.class)
    public R updatePartyUserInfo(HttpServletRequest request, Map<String, Object> partyUser) throws Exception {
    	if (partyUser == null) 
    		return R.error().setMsg("未选择人员");
    	BaseUserInfo baseUserInfo = new BaseUserInfo();
    	PartyUserInfo partyUserInfo = new PartyUserInfo();
    	//照片地址
    	baseUserInfo.setBaseUserId(StringUtil.isEmpty((String)partyUser.get("id")) ? 
    			null : Integer.parseInt((String)partyUser.get("id")));
    	baseUserInfo.setName((String)partyUser.get("name"));
    	baseUserInfo.setMobilePhone((String)partyUser.get("mobilePhone"));
    	baseUserInfo.setEmail((String)partyUser.get("email"));
    	baseUserInfo.setQq((String)partyUser.get("qq"));
    	baseUserInfo.setWechat((String)partyUser.get("wechat"));
    	baseUserInfo.setIdCard((String)partyUser.get("idCard"));
    	baseUserInfo.setEducation(StringUtil.isEmpty((String)partyUser.get("education")) ? 
    			null : Integer.parseInt((String)partyUser.get("education")));
    	baseUserInfo.setAcademicDegree(StringUtil.isEmpty((String)partyUser.get("academicDegree")) ? 
    			null : Integer.parseInt((String)partyUser.get("academicDegree")));
    	baseUserInfo.setEnrolmentTime(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("enrolmentTime")));
    	baseUserInfo.setGraduationTime(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("graduationTime")));
    	baseUserInfo.setGraduationSchool((String)partyUser.get("graduationSchool"));
    	baseUserInfo.setMajor((String)partyUser.get("major"));
    	baseUserInfo.setSpecialityLiterature((String)partyUser.get("specialityLiterature"));
    	baseUserInfo.setSpecialityMajor((String)partyUser.get("specialityMajor"));
    	baseUserInfo.setMarriageStatus((String)partyUser.get("marriageStatus"));
    	baseUserInfo.setChildrenStatus((String)partyUser.get("childrenStatus"));
		baseUserInfo.setPresentAddressProvince((String)partyUser.get("presentAddressProvince"));
		baseUserInfo.setPresentAddressCity((String)partyUser.get("presentAddressCity"));
		baseUserInfo.setPresentAddressArea((String)partyUser.get("presentAddressArea"));
    	baseUserInfo.setPresentAddressDetail((String)partyUser.get("presentAddressDetail"));
    	baseUserInfo.setIsParty(StringUtil.isEmpty((String)partyUser.get("isParty")) ? 
    			null : Integer.parseInt((String)partyUser.get("isParty")));
    	baseUserInfo.setPositiveUser(StringUtil.isEmpty((String)partyUser.get("positiveUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("positiveUser")));
    	baseUserInfo.setWorkUnit((String)partyUser.get("workUnit"));
    	baseUserInfo.setWorkNature(StringUtil.isEmpty((String)partyUser.get("workNature")) ? 
    			null : Integer.parseInt((String)partyUser.get("workNature")));
    	baseUserInfo.setJoinWorkDate(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("joinWorkDate")));
    	baseUserInfo.setAppointmentTimeLength(StringUtil.isEmpty((String)partyUser.get("appointmentTimeLength")) ? 
    			null : Integer.parseInt((String)partyUser.get("appointmentTimeLength")));
    	baseUserInfo.setFirstLineSituation(StringUtil.isEmpty((String)partyUser.get("firstLineTypeName")) ? 
    			null : Integer.parseInt((String)partyUser.get("firstLineTypeName")));
    	
    	partyUserInfo.setPartyUserId(baseUserInfo.getBaseUserId());
    	partyUserInfo.setType(StringUtil.isEmpty((String)partyUser.get("type")) ? 
    			null : Integer.parseInt((String)partyUser.get("type")));
    	partyUserInfo.setStatus(StringUtil.isEmpty((String)partyUser.get("status")) ? 
    			null : Integer.parseInt((String)partyUser.get("status")));
    	partyUserInfo.setJoinDateFormal(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("joinDateFormal")));
    	partyUserInfo.setJoinDateReserve(DateUtil.toDate(DateUtil.YYYY_MM_DD, (String)partyUser.get("joinDateReserve")));
    	partyUserInfo.setJoinPartyBranchTypeId(StringUtil.isEmpty((String)partyUser.get("joinPartyBranchType")) ? 
    			null : Integer.parseInt((String)partyUser.get("joinPartyBranchType")));
    	partyUserInfo.setPartyStaff(StringUtil.isEmpty((String)partyUser.get("partyStaff")) ? 
    			null : Integer.parseInt((String)partyUser.get("partyStaff")));
    	partyUserInfo.setPartyRepresentative(StringUtil.isEmpty((String)partyUser.get("partyRepresentative")) ? 
    			null : Integer.parseInt((String)partyUser.get("partyRepresentative")));
    	partyUserInfo.setVolunteer(StringUtil.isEmpty((String)partyUser.get("volunteer")) ? 
    			null : Integer.parseInt((String)partyUser.get("volunteer")));
    	partyUserInfo.setDifficultUser(StringUtil.isEmpty((String)partyUser.get("difficultUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("difficultUser")));
    	partyUserInfo.setAdvancedUser(StringUtil.isEmpty((String)partyUser.get("advancedUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("advancedUser")));
    	partyUserInfo.setDevelopUser(StringUtil.isEmpty((String)partyUser.get("developUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("developUser")));
    	partyUserInfo.setMissingUser(StringUtil.isEmpty((String)partyUser.get("missingUser")) ? 
    			null : Integer.parseInt((String)partyUser.get("missingUser")));
    	partyUserInfo.setIntroduce((String)partyUser.get("introduce"));


    	int insertBaseUserInfoCount = baseUserInfoMapper.updateByPrimaryKeySelective(baseUserInfo);	//更新基础信息
    	if (insertBaseUserInfoCount != 1) {	//更新失败，抛异常回滚
    		throw new Exception();
    	}
    	if (baseUserInfo.getIsParty() == 1) {
    		if (partyUserInfoMapper.selectByPrimaryKey(baseUserInfo.getBaseUserId()) == null) {
    			int insertPartyUserInfoCount = partyUserInfoMapper.insertSelective(partyUserInfo);
    			if (insertPartyUserInfoCount != 1) {	//更新失败，抛异常回滚
            		throw new Exception();
            	}
    			
    			SysUser su = new SysUser();
            	su.setUsername(baseUserInfo.getIdCard());
            	su.setPassword(baseUserInfo.getIdCard().substring(baseUserInfo.getIdCard().length() - 6));
            	String salt = UUID.randomUUID().toString();
            	su.setSalt(salt);	//保存盐
            	su.setPassword(PasswordHelper.encryptPassword(su.getPassword(), salt));	//加密
            	su.setEmail(baseUserInfo.getEmail());
            	su.setMobile(baseUserInfo.getMobilePhone());
            	su.setStatus(true);
            	su.setUserType(1);
            	su.setCreateTime(new Date());
    			int count = sysUserMapper.insertSelective(su);
    			if (count != 1) {
    				throw new Exception();
    			}
    			//赋予默认角色
    			SysRole sysRole = new SysRole();
    			sysRole.setRoleName("party_role");
    			List<SysRole> srs = sysRoleMapper.querySysRoles(sysRole);
    			if (srs != null && srs.size() == 1) {
    				SysUserRole sur = new SysUserRole();
    				sur.setUserId((long)su.getUserId());
    				sur.setRoleId(srs.get(0).getRoleId());
    				count = sysUserRoleMapper.insertSelective(sur);
    				if (count != 1) {
    					throw new Exception();
    				}
    			} else {
    				throw new Exception();
    			}
    		} else {
    			int updatePartyUserInfoCount = partyUserInfoMapper.updateByPrimaryKeySelective(partyUserInfo);	//更新党员信息
            	if (updatePartyUserInfoCount != 1) {	//更新失败，抛异常回滚
            		throw new Exception();
            	}
    		}
    	} else {
    		partyUserInfoMapper.deleteByPrimaryKey(baseUserInfo.getBaseUserId());
    		//同时删除登录账户
    		baseUserInfo = baseUserInfoMapper.selectByPrimaryKey(baseUserInfo.getBaseUserId());
			if (StringUtil.isNotEmpty(baseUserInfo.getIdCard())) {
				SysUser su = new SysUser();
				su.setUsername(baseUserInfo.getIdCard());
				sysUserMapper.deleteByUserName(su);
			}
    	}
    	
    	return R.ok().setMsg("党员信息更新成功");
    }
    
    /**
     * 删除党员
     */
    @Override
    @Transactional(rollbackFor=java.lang.Exception.class)
    public R deletePartyUserInfo(BaseUserInfo baseUserInfo) throws Exception {
    	if(baseUserInfo != null) {
    		baseUserInfo = baseUserInfoMapper.selectByPrimaryKey(baseUserInfo.getBaseUserId());
			int countpui = this.deleteByPrimaryKey(baseUserInfo.getBaseUserId());	//开始删除党员用户信息
			int countBui = baseUserInfoMapper.deleteByPrimaryKey(baseUserInfo.getBaseUserId()); //删除基础用户信息
			OrganizationRelation or = new OrganizationRelation();
			or.setOrgRltUserId(baseUserInfo.getBaseUserId());
			organizationRelationService.deleteOrgRelationByUserId(or).get("data");
			if (StringUtil.isNotEmpty(baseUserInfo.getIdCard())) {
				SysUser su = new SysUser();
				su.setUsername(baseUserInfo.getIdCard());
				sysUserMapper.deleteByUserName(su);
				CacheUtil.clearAuthenticationCache(su.getUsername());
			}
			
			
			if (countpui == 1 && countBui == 1) {	//受影响的行数，判断是否全部删除
				return R.ok().setMsg("党员删除成功！");
			} else {	//没有受影响行数或者受影响行数与要删除的用户数量不匹配表示删除失败
				throw new Exception();
			}
		} else {	//删除用户一定需要一个用户信息
			throw new Exception();
		}
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

	public String getUploadIdPhotoTempPath() {
		return uploadIdPhotoTempPath;
	}

	public void setUploadIdPhotoTempPath(String uploadIdPhotoTempPath) {
		this.uploadIdPhotoTempPath = uploadIdPhotoTempPath;
	}

	public String getUploadIdPhotoPath() {
		return uploadIdPhotoPath;
	}

	public void setUploadIdPhotoPath(String uploadIdPhotoPath) {
		this.uploadIdPhotoPath = uploadIdPhotoPath;
	}
}
