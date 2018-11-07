package com.zy.smart.service.system;

import com.github.pagehelper.Page;
import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.TroleMenu;
import com.zy.smart.model.JqGridBean;

import java.util.HashMap;
import java.util.List;

public interface RoleService {
    Page<Trole> queryRolePage(JqGridBean jqGridBean, HashMap<String, Object> paramMap);

    List<Trole> queryRoleByParam(Trole role);

    void insertRole(Trole role);

    Trole queryRoleById(Integer id);

    void updateRole(Trole role);

    void deleteUserRoleByRoleId(Integer id);

    void deleteMenuRoleByRoleId(Integer id);

    void deleteRoleById(Integer id);

    List<Tmenu> queryMenusByRoleId(Integer roleId);

    int selectMenuCountByPid(int id);

    List<Tmenu> queryMenusByPid(Integer parentId);

    void deleteRoleMenuByParam(Integer roleId, List<Integer> menuIdList);

    void saveRoleMenu(TroleMenu troleMenu);

    List<Tmenu> queryOneMenusByName(String name);

}
