package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.Param.AclParam;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAcl;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclService {
    @Resource
    private SysLogService sysLogService;
    @Resource
    private SysAclMapper sysAclMapper;

    public void save(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl acl = SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId())
                .url(param.getUrl()).type(param.getType()).status(param.getStatus()).seq(param.getSeq())
                .remark(param.getRemark()).build();
        acl.setCode(generateCode());
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        acl.setOperateTime(new Date());
        sysAclMapper.insertSelective(acl);
        sysLogService.saveAclLog(null,acl);
    }

    public void update(AclParam param){
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl aclBefor = sysAclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(aclBefor,"待更新的权限点不存在");

        SysAcl aclAfter = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId())
                .url(param.getUrl()).type(param.getType()).status(param.getStatus()).seq(param.getSeq())
                .remark(param.getRemark()).build();
        aclAfter.setCode(generateCode());
        aclAfter.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclAfter.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclAfter.setOperateTime(new Date());
        sysAclMapper.updateByPrimaryKey(aclAfter);
        sysLogService.saveAclLog(aclBefor,aclAfter);
    }

    public boolean checkExist(int aclMouleId,String name, Integer id){
        return sysAclMapper.countByNameAndAclModuleId(aclMouleId,name,id) > 0;
    }

    public String generateCode(){
        SimpleDateFormat dateForm = new SimpleDateFormat();
        return dateForm.format(new Date()) + "_" + (int)(Math.random() * 100);
    }

    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery page){
        BeanValidator.check(page);
        int count = sysAclMapper.countByAclModuleId(aclModuleId);
        if(count > 0){
            List<SysAcl> aclList = sysAclMapper.getPageByAclModuleId(aclModuleId,page);
            return PageResult.<SysAcl>builder().data(aclList).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }
}
