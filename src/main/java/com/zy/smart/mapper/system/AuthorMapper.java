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
}
