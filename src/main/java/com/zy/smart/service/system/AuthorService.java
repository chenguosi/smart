package com.zy.smart.service.system;

import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.Tuser;

import java.util.HashMap;
import java.util.List;

public interface AuthorService {

    Tuser findUserByAccount(String account);

    List<Trole> findRoleByAccount(String account);

    List<Tmenu> findMenuByAccount(String account);

    List<Tmenu> findMenuOneClass();

    List<Tmenu> findMenuOneClassByAccount(String account);

    List<Tmenu> selectByParentIdAndAccount(HashMap<String, Object> paraMap);

    int selectChildCount(int id);

    List<Tmenu> selectMenuByPid(Integer parentId);

    Tmenu selectMenuById(Integer id);

    List<Tmenu> selectMenuByName(String name);

    List<Tmenu> selectMenuByPidDesc(Integer pid);

    void saveMenu(Tmenu tmenu);

    void updateMenu(Tmenu tmenuNew);

    void deleteRoleMenu(Integer id);

    void deleteMenuByPid(Integer id);

    void deleteMenuById(Integer id);
}
