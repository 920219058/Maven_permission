package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.Param.AclModuleParam;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysAclMapper;
import com.mmall.dao.SysAclModuleMapper;
import com.mmall.exception.ParamException;
import com.mmall.model.SysAclModule;
import com.mmall.model.SysDept;
import com.mmall.util.BeanValidator;
import com.mmall.util.IpUtil;
import com.mmall.util.LevelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SysAclModuleService {
    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    public SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysLogService sysLogService;

    public void save(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExis(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下不能存在相同名称的权限模块");
        }
        SysAclModule aclModule = SysAclModule.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        aclModule.setLevel(LevelUtil.calcuateLevel(getLevel(param.getParentId()),param.getParentId()));
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModule.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(aclModule);
        sysLogService.saveAclModuleLog(null, aclModule);
    }

    public void update(AclModuleParam param){
        BeanValidator.check(param);
        if(checkExis(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下不能存在相同名称的权限模块");
        }
        SysAclModule befor = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(befor,"待更新的权限模块不存在");

        SysAclModule after = SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calcuateLevel(getLevel(param.getParentId()),param.getParentId()));

        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(befor,after);
        sysLogService.saveAclModuleLog(befor, after);
    }

    @Transactional
    public void updateWithChild(SysAclModule befor,SysAclModule after){
        String newLevePrefix = after.getLevel();
        String oldLevePrefix = befor.getLevel();
        if(!newLevePrefix.equals(oldLevePrefix)){
            List<SysAclModule> sysAclModuleList = sysAclModuleMapper.getChildAclModuleListByLevel(befor.getLevel());
            if(CollectionUtils.isNotEmpty(sysAclModuleList)){
                for (SysAclModule aclModule :sysAclModuleList){
                    String level = aclModule.getLevel();
                    if(level.indexOf(oldLevePrefix) == 0){
                        level = newLevePrefix + level.substring(oldLevePrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(sysAclModuleList);
            }
        }
        sysAclModuleMapper.updateByPrimaryKey(after);
    }
    private boolean checkExis(Integer parentid,String aclModuleName,Integer deptId){
        return false;
    }

    private String getLevel(Integer aclModuleId){
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if( aclModule == null){
            return null;
        }
        return aclModule.getLevel();
    }

    public void delete(int aclModuleId){
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        Preconditions.checkNotNull(aclModule,"待删除的权限模块不存在，无法删除");
        if(sysAclModuleMapper.countByParentId(aclModule.getId())>0){
            throw new ParamException("当前模块下面有子模块，无法删除");
        }
        if(sysAclMapper.countByAclModuleId(aclModule.getId()) > 0){
            throw new ParamException("当前模块下面由用户，无法删除");
        }
        sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);
        sysLogService.saveAclModuleLog(aclModule, null);
    }
}
