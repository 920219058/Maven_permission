package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.Param.SearchLogParam;
import com.mmall.beans.LogType;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.*;
import com.mmall.dto.SearchLogDto;
import com.mmall.exception.ParamException;
import com.mmall.model.*;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.JsonMapper;
import com.mmall.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SysLogService {

    @Resource
    private SysLogMapper sysLogMapper;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysRoleAclService sysRoleAclService;
    @Resource
    private SysRoleUserService sysRoleUserService;

    public void recover(int id){
        SysLogWithBLOBs sysLog = sysLogMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(sysLog,"带还原的记录不存在");
        switch (sysLog.getType()){
            case LogType.TYPE_DEPT:
                SysDept befor = sysDeptMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(befor,"待还原的部门已经不存在");
                if(StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysDept after = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysDept>() {
                });
                after.setOperator(RequestHolder.getCurrentUser().getUsername());
                after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                after.setOperateTime(new Date());
                sysDeptMapper.updateByPrimaryKeySelective(after);
                saveDeptLog(befor,after);
                break;
            case LogType.TYPE_USER:
                SysUser beforUser = sysUserMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforUser,"待还原的用户已经不存在");
                if(StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysUser afterUser = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysUser>() {
                });
                afterUser.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterUser.setOperateTime(new Date());
                sysUserMapper.updateByPrimaryKeySelective(afterUser);
                saveUserLog(beforUser,afterUser);
                break;
            case LogType.TYPE_ACL_MODULE:
                SysAclModule beforAclModule = sysAclModuleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforAclModule,"待还原的权限模块已经不存在");
                if(StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysAclModule afterAclModule = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysAclModule>() {
                });
                afterAclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterAclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterAclModule.setOperateTime(new Date());
                sysAclModuleMapper.updateByPrimaryKeySelective(afterAclModule);
                saveAclModuleLog(beforAclModule,afterAclModule);
                break;
            case LogType.TYPE_ACL:
                SysAcl beforAcl = sysAclMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforAcl,"待还原的权限点已经不存在");
                if(StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysAcl afterAcl = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysAcl>() {
                });
                afterAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterAcl.setOperateTime(new Date());
                sysAclMapper.updateByPrimaryKeySelective(afterAcl);
                saveAclLog(beforAcl,afterAcl);
                break;
            case LogType.TYPE_ROLE:
                SysRole beforRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(beforRole,"待还原的角色已经不存在");
                if(StringUtils.isBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作不做还原");
                }
                SysRole afterRole = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysRole>() {
                });
                afterRole.setOperator(RequestHolder.getCurrentUser().getUsername());
                afterRole.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                afterRole.setOperateTime(new Date());
                sysRoleMapper.updateByPrimaryKeySelective(afterRole);
                saveRoleLog(beforRole,afterRole);
                break;
            case LogType.TYPE_ROLE_ACL:
                SysRole aclRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(aclRole,"角色已经不存在");
                sysRoleAclService.changRoleAcls(sysLog.getTargetId(),JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<List<Integer>>() {
                }));
                break;
            case LogType.TYPE_ROLE_USER:
                SysRole userRole = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(userRole,"角色已经不存在");
                sysRoleUserService.changeRoleUsers(sysLog.getTargetId(),JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<List<Integer>>() {
                }));

                break;
            default:;
        }
    }

    public PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery page){
        BeanValidator.check(page);
        SearchLogDto dto = new SearchLogDto();
        dto.setType(param.getType());
        if(StringUtils.isNotBlank(param.getBeforeSeq())){
            dto.setBeforeSeq("%" + param.getBeforeSeq()+ "%");
        }
        if(StringUtils.isNotBlank(param.getAfterSeq())){
            dto.setAfterSeq("%" + param.getAfterSeq() + "%");
        }
        if(StringUtils.isNotBlank(param.getOperator())){
            dto.setOperator("%" + param.getOperator() + "%");
        }
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            if(StringUtils.isNotBlank(param.getFromTime())){
                dto.setFromTime(dateFormat.parse(param.getFromTime()));
            }
        }catch(Exception e){
            log.error("时间格式有问题，正确的格式：yyy-MM-dd HH：mm：ss");
            throw new ParamException("传入的日期格式有问题，正确格式为：yyyy-MM-dd HH:mm:ss");
        }
        int count = sysLogMapper.countBySearchDto(dto);
        if(count > 0){
            List<SysLogWithBLOBs> logList = sysLogMapper.getPageListBySearchDto(dto,page);
            return PageResult.<SysLogWithBLOBs>builder().total(count).data(logList).build();
        }
        return PageResult.<SysLogWithBLOBs>builder().build();
    }

    public void saveDeptLog(SysDept befor, SysDept after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_DEPT);
        sysLog.setTargetId(after == null ? befor.getId() : after.getId());
        sysLog.setOldValue(befor == null ? "" : JsonMapper.obj2String(befor));
        sysLog.setNewValue(after == null ? "" :JsonMapper.obj2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1); //表示没有操作过
        sysLogMapper.insertSelective(sysLog);
    }

    public void saveUserLog(SysUser befor, SysUser after){
        SysLogWithBLOBs userLog = new SysLogWithBLOBs();
        userLog.setType(LogType.TYPE_USER);
        userLog.setTargetId(after == null ? befor.getId() : after.getId());
        userLog.setOldValue(befor == null ? "" : JsonMapper.obj2String(befor));
        userLog.setNewValue(after == null ? "" :JsonMapper.obj2String(after));
        userLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        userLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        userLog.setStatus(1); //表示没有操作过
        sysLogMapper.insertSelective(userLog);
    }

    public void saveAclModuleLog(SysAclModule befor, SysAclModule after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL_MODULE);
        sysLog.setTargetId(after == null ? befor.getId() : after.getId());
        sysLog.setOldValue(befor == null ? "" : JsonMapper.obj2String(befor));
        sysLog.setNewValue(after == null ? "" :JsonMapper.obj2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1); //表示没有操作过
        sysLogMapper.insertSelective(sysLog);
    }

    public void saveAclLog(SysAcl befor, SysAcl after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL);
        sysLog.setTargetId(after == null ? befor.getId() : after.getId());
        sysLog.setOldValue(befor == null ? "" : JsonMapper.obj2String(befor));
        sysLog.setNewValue(after == null ? "" :JsonMapper.obj2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1); //表示没有操作过
        sysLogMapper.insertSelective(sysLog);
    }

    public void saveRoleLog(SysRole befor, SysRole after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE);
        sysLog.setTargetId(after == null ? befor.getId() : after.getId());
        sysLog.setOldValue(befor == null ? "" : JsonMapper.obj2String(befor));
        sysLog.setNewValue(after == null ? "" :JsonMapper.obj2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setStatus(1); //表示没有操作过
        sysLogMapper.insertSelective(sysLog);
    }
}
