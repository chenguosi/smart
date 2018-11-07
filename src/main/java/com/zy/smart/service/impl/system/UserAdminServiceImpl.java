package com.zy.smart.service.impl.system;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.Tuser;
import com.zy.smart.domain.system.TuserRole;
import com.zy.smart.mapper.system.SystemMapper;
import com.zy.smart.model.JqGridBean;
import com.zy.smart.service.system.UserAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class UserAdminServiceImpl implements UserAdminService {

    @Autowired
    private SystemMapper systemMapper;


    @Override
    public Page<Tuser> queryUserPage(JqGridBean jqGridBean, HashMap<String, Object> paramMap) {
        PageHelper.startPage(jqGridBean.getPage(),jqGridBean.getLength());
        Page<Tuser> result = systemMapper.queryUserPage(paramMap);
        return result;
    }

    @Override
    public List<Trole> queryRolesByAccount(String account) {
        List<Trole> roleList = systemMapper.queryRolesByAccount(account);
        return roleList;
    }

    @Override
    public List<Tuser> queryUserByName(String userName) {
        List<Tuser> userList = systemMapper.queryUserByName(userName);
        return userList;
    }

    @Override
    public void saveUser(Tuser user) {
        systemMapper.saveUser(user);
    }

    @Override
    public Tuser queryUserById(Integer id) {
        Tuser user = systemMapper.queryUserById(id);
        return user;
    }

    @Override
    public void updateUser(Tuser user) {
        systemMapper.updateUser(user);
    }

    @Override
    public void deleteUserRole(String account) {
        systemMapper.deleteUserRole(account);
    }

    @Override
    public void deleteUser(Integer id) {
        systemMapper.deleteUser(id);
    }

    @Override
    public List<Trole> queryAllRole() {
        List<Trole> list = systemMapper.queryAllRole();
        return list;
    }

    @Override
    public void saveUserRole(TuserRole tuserRole) {
        systemMapper.insertUserRole(tuserRole);
    }

    @Override
    public List<Tuser> queryUserByParam(HashMap<String, Object> paramMap) {
        List<Tuser> list = systemMapper.queryUserByParam(paramMap);
        return list;
    }

    @Override
    public void updatePassword(Tuser tuser) {
        systemMapper.updatePassword(tuser);
    }
}
