package com.zy.smart.mapper.system;

import com.github.pagehelper.Page;
import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.TroleMenu;
import com.zy.smart.domain.system.Tuser;
import com.zy.smart.domain.system.TuserRole;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface SystemMapper {
    Page<Trole> queryRolePage(HashMap<String, Object> paramMap);

    List<Trole> queryRoleByParam(Trole role);

    void insertRole(Trole role);

    Trole queryRoleById(@Param("id") Integer id);

    void updateRole(Trole role);

    void deleteUserRoleByRoleId(@Param("id") Integer id);

    void deleteMenuRoleByRoleId(@Param("id") Integer id);

    void deleteRoleById(@Param("id") Integer id);

    List<Tmenu> queryMenusByRoleId(@Param("roleId") Integer roleId);

    int selectMenuCountByPid(@Param("id") int id);

    List<Tmenu> queryMenusByPid(@Param("parentId") Integer parentId);

    void deleteRoleMenuByParam(@Param("roleId") Integer roleId, @Param("menuIdList") List<Integer> menuIdList);

    void saveRoleMenu(TroleMenu roleMenu);

    List<Tmenu> queryOneMenusByName(@Param("name") String name);

    Page<Tuser> queryUserPage(HashMap<String, Object> paramMap);

    List<Trole> queryRolesByAccount(@Param("account") String account);

    List<Tuser> queryUserByName(@Param("userName") String userName);

    void saveUser(Tuser user);

    Tuser queryUserById(@Param("id") Integer id);

    void updateUser(Tuser user);

    void deleteUserRole(@Param("account") String account);

    void deleteUser(@Param("id") Integer id);

    List<Trole> queryAllRole();

    void insertUserRole(TuserRole tuserRole);

    List<Tuser> queryUserByParam(HashMap<String, Object> paramMap);

    void updatePassword(Tuser tuser);
}
