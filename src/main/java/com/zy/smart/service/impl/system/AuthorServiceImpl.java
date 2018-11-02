package com.zy.smart.service.impl.system;

import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.Tuser;
import com.zy.smart.mapper.system.AuthorMapper;
import com.zy.smart.service.system.AuthorService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorMapper authorMapper;

    @Override
    public Tuser findUserByAccount(String account) {
        Tuser tuser = null;
        if(StringUtils.isNotBlank(account)){
            tuser = authorMapper.findUserByAccount(account);
        }
        return tuser;
    }

    @Override
    public List<Trole> findRoleByAccount(String account) {
        List<Trole> list = null;
        if(StringUtils.isNotBlank(account)){
            list = authorMapper.findRoleByAccount(account);
        }
        return list;
    }

    @Override
    public List<Tmenu> findMenuByAccount(String account) {
        List<Tmenu> list = null;
        if(StringUtils.isNotBlank(account)){
            list = authorMapper.findMenuByAccount(account);
        }
        return list;
    }

    @Override
    public List<Tmenu> findMenuOneClass() {
        List<Tmenu> list = authorMapper.findMenuOneClass();
        return list;
    }

    @Override
    public List<Tmenu> selectByParentIdAndAccount(HashMap<String, Object> paraMap) {
        List<Tmenu> list = authorMapper.selectByParentIdAndAccount(paraMap);
        return list;
    }

    @Override
    public int selectChildCount(int id) {
        int count = authorMapper.selectChildCount(id);
        return count;
    }

}
