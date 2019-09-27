package com.mmall.service;

import com.google.common.base.Preconditions;
import com.mmall.Param.DeptParam;
import com.mmall.common.RequestHolder;
import com.mmall.dao.SysDeptMapper;
import com.mmall.dao.SysUserMapper;
import com.mmall.exception.ParamException;
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

@Service
@Slf4j
public class SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private  SysLogService sysLogService;

    public void save(DeptParam param){
        BeanValidator.check(param);
        log.info("parentId : {},name:{},id:{}",param.getParentId(),param.getName(),param.getId());
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同的名称部门");
        }
        SysDept dept = SysDept.builder().name(param.getName()).parentId(param.getParentId()).seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(LevelUtil.calcuateLevel(getLevel(param.getParentId()),param.getParentId()));
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());
        dept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        dept.setOperateTime(new Date());
        sysDeptMapper.insertSelective(dept);
        sysLogService.saveDeptLog(null,dept);
    }

    public void update(DeptParam param){
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同的名称部门");
        }
        SysDept befor = sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(befor,"待更新的部门不存在");
        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId()).seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calcuateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWhithChid(befor,after);
        sysLogService.saveDeptLog(befor,after);
    }

    // 开启事物 确保下面的部门也可以更新成功
    @Transactional
    public void updateWhithChid(SysDept befor,SysDept after){
        String newLevePrefix = after.getLevel();
        String oldLevePrefix = befor.getLevel();
        if(!newLevePrefix.equals(oldLevePrefix)){
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(befor.getLevel());
            if(CollectionUtils.isNotEmpty(deptList)){
                for (SysDept dept :deptList){
                    String level = dept.getLevel();
                    if(level.indexOf(oldLevePrefix) == 0){
                        level = newLevePrefix + level.substring(oldLevePrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);
    }

    public boolean checkExist(Integer parentId,String deptName,Integer deptId){
        return sysDeptMapper.countByNameAndParentId(parentId,deptName,deptId) > 0;
    }

    private String getLevel(Integer deptId){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept == null){
            return null;
        }
        return dept.getLevel();
    }

    public void deleteDept(int deptId){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        Preconditions.checkNotNull(dept,"待删除的部门不存在，无法删除");
        if(sysDeptMapper.countByParentId(dept.getId()) > 0){
            throw new ParamException("当前部门下面有子部门");
        }
        if(sysUserMapper.countByDeptId(dept.getId()) > 0){
            throw new ParamException("当前部门下面有用户，无法删除");
        }
        sysDeptMapper.deleteByPrimaryKey(deptId);
        sysLogService.saveDeptLog(dept,null);
    }

}
