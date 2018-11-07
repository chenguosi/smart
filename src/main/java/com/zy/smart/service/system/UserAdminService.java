package com.zy.smart.service.system;

import com.github.pagehelper.Page;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.Tuser;
import com.zy.smart.domain.system.TuserRole;
import com.zy.smart.model.JqGridBean;

import java.util.HashMap;
import java.util.List;

public interface UserAdminService {

    Page<Tuser> queryUserPage(JqGridBean jqGridBean, HashMap<String, Object> paramMap);

    List<Trole> queryRolesByAccount(String account);

    List<Tuser> queryUserByName(String userName);

    void saveUser(Tuser user);

    Tuser queryUserById(Integer id);

    void updateUser(Tuser user);

    void deleteUserRole(String account);

    void deleteUser(Integer id);

    List<Trole> queryAllRole();

    void saveUserRole(TuserRole tuserRole);

    List<Tuser> queryUserByParam(HashMap<String, Object> paramMap);

    void updatePassword(Tuser tuser);
}
