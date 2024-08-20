package com.lyf.crm24.dao;

import com.lyf.crm24.base.BaseMapper;
import com.lyf.crm24.dto.TreeDto;
import com.lyf.crm24.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module, Integer> {

    List<TreeDto> queryAllModules();

    List<Module> queryModules();

    // 通过层级与模块名查询资源对象
    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade, @Param("moduleName") String moduleName);
    // 通过层级与url查询资源对象
    Module queryModuleByGradeAndUrl(@Param("grade") Integer grade, @Param("url") String url);
    // 通过权限码查询资源对象
    Module queryModuleByOptValue(String optValue);

    // 查询指定的资源是否存在子记录
    Integer countSubModuleByParentId(Integer mid);
}