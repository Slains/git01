package com.lyf.crm24.service;

import com.lyf.crm24.dao.ModuleMapper;
import com.lyf.crm24.dao.PermissionMapper;
import com.lyf.crm24.utils.AssertUtil;
import com.lyf.crm24.vo.Module;
import com.lyf.crm24.dto.TreeDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService {

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModuleById(Integer mid){
        Module temp = moduleMapper.selectByPrimaryKey(mid);
        AssertUtil.isTrue(null == mid || null == temp,"待删除记录不存在!");
        /**
         * 如果存在⼦菜单 不允许删除
         */
        int count = moduleMapper.countSubModuleByParentId(mid);
        AssertUtil.isTrue(count>0,"存在⼦菜单，不⽀持删除操作!");
        // 权限表
        count =permissionMapper.countPermissionsByModuleId(mid);
        if(count > 0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByModuleId(mid)<count,"菜单删除失败!");
        }
        // 删除module记录
        temp.setIsValid((byte) 0);
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp)<1,"菜单删除失败!");
    }

    /*
    * 添加资源：
    * 1.参数校验
    *   模块名称    moduleName
    *       非空，同一层级下模块名称唯一
    *   地址  url
    *       grade=1，非空且同一层级下不可重复
    *   父级菜单    parentId
    *       grade=0，为null
    *       grade=2/3，非空，且父级菜单id必须在表中存在
    *   层级  grade
    *       非空，取值：0，1，2
    *   权限码 optValue
    *       非空，不可重复
    * 2.设置参数的默认值
    *   是否有效
    *   创建时间
    *   修改时间
    * 3.执行添加操作，判断受影响行数
    *
    * */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveModule(Module module){
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请输⼊菜单名!");
        Integer grade =module.getGrade();
        AssertUtil.isTrue(null== grade|| !(grade==0||grade==1||grade==2),"菜单层级不合法!");
        AssertUtil.isTrue(null !=moduleMapper.queryModuleByGradeAndModuleName(module.getGrade(),module.getModuleName()),"该层级下菜单重复!");
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定⼆级菜单url值");
            AssertUtil.isTrue(null !=moduleMapper.queryModuleByGradeAndUrl(module.getGrade(),module.getUrl()),"⼆级菜单url不可重复!");
        }
        if(grade !=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId || null==moduleMapper.selectByPrimaryKey(parentId),"请指定上级菜单!");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输⼊权限码!");
        AssertUtil.isTrue(null !=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码重复!");
        module.setIsValid((byte)1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.insertSelective(module)<1,"菜单添加失败!");
    }

    /**
     * 1.参数校验
     * id ⾮空 记录存在
     * 模块名-module_name
     * ⾮空 同⼀层级下模块名唯⼀
     * url
     * ⼆级菜单 ⾮空 不可重复
     * 上级菜单-parent_id
     * ⼆级|三级菜单 parent_id ⾮空 必须存在
     * 层级-grade
     * ⾮空 0|1|2
     * 权限码 optValue
     * ⾮空 不可重复
     * 2.参数默认值设置
     * update_date
     * 3.执⾏更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module){
        AssertUtil.isTrue(null == module.getId() || null == moduleMapper.selectByPrimaryKey(module.getId()),"待更新记录不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请指定菜单名称!");
        Integer grade =module.getGrade();
        AssertUtil.isTrue(null== grade|| !(grade==0||grade==1||grade==2),"菜单层级不合法!");
        Module temp =moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName());
        if(null !=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下菜单已存在!");
        }
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定⼆级菜单url值");
            temp =moduleMapper.queryModuleByGradeAndUrl(grade,module.getUrl());
            if(null !=temp){
                AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下url已存在!");
            }
        }
        if(grade !=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId || null == moduleMapper.selectByPrimaryKey(parentId),"请指定上级菜单!");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输⼊权限码!");
        temp =moduleMapper.queryModuleByOptValue(module.getOptValue());
        if(null !=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"权限码已存在!");
        }
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)<1,"菜单更新失败!");
    }

    public Map<String,Object> moduleList(){
        Map<String,Object> result = new HashMap<String,Object>();
        List<Module> modules =moduleMapper.queryModules();
        result.put("count",modules.size());
        result.put("data",modules);
        result.put("code",0);
        result.put("msg","");
        return result;
    }

    // 按照角色id查询该角色的所有资源
    public List<TreeDto> queryAllModules02(Integer roleId) {
        List<TreeDto> treeDtos=moduleMapper.queryAllModules();
        // 根据⻆⾊id 查询⻆⾊拥有的菜单id List<Integer>
        List<Integer> roleHasMids=permissionMapper.queryRoleHasAllModuleIdsByRoleId(roleId);
        if(null !=roleHasMids && roleHasMids.size()>0){
            treeDtos.forEach(treeDto -> {
                if(roleHasMids.contains(treeDto.getId())){
                    // 说明当前⻆⾊ 分配了该菜单
                    treeDto.setChecked(true);
                }
            });
        }
        return treeDtos;
    }

    // 查询所有资源
    public List<TreeDto> queryAllModules(){
        return moduleMapper.queryAllModules();
    }

    public Module selectByPrimaryKey(Integer id) {
        return moduleMapper.selectByPrimaryKey(id);
    }
}
