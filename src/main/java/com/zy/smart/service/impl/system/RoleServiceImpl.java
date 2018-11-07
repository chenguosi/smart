package com.zy.smart.service.impl.system;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.TroleMenu;
import com.zy.smart.mapper.system.SystemMapper;
import com.zy.smart.model.JqGridBean;
import com.zy.smart.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    
    @Autowired
    private SystemMapper systemMapper;
    
    @Override
    public Page<Trole> queryRolePage(JqGridBean jqGridBean, HashMap<String, Object> paramMap) {
        PageHelper.startPage(jqGridBean.getPage(), jqGridBean.getLength());
        Page<Trole> page = systemMapper.queryRolePage(paramMap);
        return page;
    }
    
    @Override
    public List<Trole> queryRoleByParam(Trole role) {
        List<Trole> list = systemMapper.queryRoleByParam(role);
        return list;
    }
    
    @Override
    public void insertRole(Trole role) {
        systemMapper.insertRole(role);
    }
    
    @Override
    public Trole queryRoleById(Integer id) {
        Trole role = systemMapper.queryRoleById(id);
        return role;
    }
    
    @Override
    public void updateRole(Trole role) {
        systemMapper.updateRole(role);
    }
    
    @Override
    public void deleteUserRoleByRoleId(Integer id) {
        systemMapper.deleteUserRoleByRoleId(id);
    }
    
    @Override
    public void deleteMenuRoleByRoleId(Integer id) {
        systemMapper.deleteMenuRoleByRoleId(id);
    }
    
    @Override
    public void deleteRoleById(Integer id) {
        systemMapper.deleteRoleById(id);
    }
    
    @Override
    public List<Tmenu> queryMenusByRoleId(Integer roleId) {
        List<Tmenu> tmenuList = systemMapper.queryMenusByRoleId(roleId);
        return tmenuList;
    }
    
    @Override
    public int selectMenuCountByPid(int id) {
        int count = systemMapper.selectMenuCountByPid(id);
        return count;
    }
    
    @Override
    public List<Tmenu> queryMenusByPid(Integer parentId) {
        List<Tmenu> menuList = systemMapper.queryMenusByPid(parentId);
        return menuList;
    }
    
    @Override
    public void deleteRoleMenuByParam(Integer roleId, List<Integer> menuIdList) {
        systemMapper.deleteRoleMenuByParam(roleId, menuIdList);
    }
    
    @Override
    public void saveRoleMenu(TroleMenu troleMenu) {
        systemMapper.saveRoleMenu(troleMenu);
    }
    
    @Override
    public List<Tmenu> queryOneMenusByName(String name) {
        List<Tmenu> menuList = systemMapper.queryOneMenusByName(name);
        return menuList;
    }
    
}
