package com.zy.smart.mapper.system;

import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.Tuser;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface AuthorMapper {

    Tuser findUserByAccount(@Param("account") String account);

    List<Trole> findRoleByAccount(@Param("account") String account);

    List<Tmenu> findMenuByAccount(@Param("account") String account);

    List<Tmenu> findMenuOneClass();

    List<Tmenu> selectByParentIdAndAccount(HashMap<String, Object> paraMap);

    int selectChildCount(@Param("id") int id);

    List<Tmenu> selectMenuByPid(@Param("parentId") Integer parentId);

    Tmenu selectMenuById(@Param("id") Integer id);

    List<Tmenu> selectMenuByName(@Param("name") String name);

    List<Tmenu> selectMenuByPidDesc(Integer pid);

    void saveMenu(Tmenu tmenu);

    void updateMenu(Tmenu tmenuNew);

    void deleteRoleMenu(@Param("id") Integer id);

    void deleteMenuByPid(@Param("id") Integer id);

    void deleteMenuById(@Param("id") Integer id);
}
