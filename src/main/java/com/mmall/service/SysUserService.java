package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.Param.UserParam;
import com.mmall.beans.CacheKeyConstants;
import com.mmall.beans.PageQuery;
import com.mmall.beans.PageResult;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysUser;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.MD5Util;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysLogService sysLogService;
    @Resource
    private SysCacheService sysCacheService;

    public void save(UserParam param){
        BeanValidator.check(param);
        if(checkEmailExist(param.getTelephone(),param.getId())){
            throw new ParamException("邮箱已被占用");
        }

        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }
        String password = "1234";
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser user = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).password(encryptedPassword).deptId(param.getDeptId())
                .status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        user.setOperateTime(new Date());
        // TODO sendEmail  MailUtil

        sysUserMapper.insertSelective(user);
        sysLogService.saveUserLog(null,user);
    }

    public void update(UserParam param){
        BeanValidator.check(param);
        if(checkEmailExist(param.getTelephone(),param.getId())){
            throw new ParamException("邮箱已被占用");
        }

        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }
        SysUser befor = sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(befor,"待更新的用户不存在");

        SysUser after = SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone())
                .mail(param.getMail()).password(befor.getPassword()).deptId(param.getDeptId())
                .status(param.getStatus()).remark(param.getRemark()).build();

        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveUserLog(befor,after);
    }

    public boolean checkEmailExist(String mail,Integer userId){
        return sysUserMapper.countByMail(mail,userId) > 0;
    }

    public boolean checkTelephoneExist(String telephone,Integer userId){
        return sysUserMapper.countByTelephone(telephone,userId) > 0;
    }

    public SysUser findByKeyword(String keyword){
        return sysUserMapper.findByKeyword(keyword);
    }

    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page){
        BeanValidator.check(page);
        int count = sysUserMapper.countByDeptId(deptId);
        if(count > 0){
            List<SysUser> list = sysUserMapper.getPageByDeptId(deptId,page);
            return PageResult.<SysUser>builder().total(count).data(list).build();
        }
        return PageResult.<SysUser>builder().build();
    }

    // 获取全部用户的方法
    public List<SysUser> getAllUsers(){
        return sysUserMapper.getAllUsers();
    }

    // 获取全部用户的方法
    public List<SysUser> getAllUsersFromcache(){
        List<SysUser> userList = sysCacheService.getAllUserFromCache(CacheKeyConstants.USER_INFO_ALL);
        if(CollectionUtils.isNotEmpty(userList)){
            return userList;
        }
        List<SysUser> usersAll = sysUserMapper.getAllUsers();
        if(CollectionUtils.isNotEmpty(usersAll)){
            sysCacheService.saveAllUserInfoToCache(CacheKeyConstants.USER_INFO_ALL,600,usersAll);
        }
        return sysUserMapper.getAllUsers();
    }
}
