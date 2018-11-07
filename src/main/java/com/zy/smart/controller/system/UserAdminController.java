package com.zy.smart.controller.system;

import com.github.pagehelper.Page;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.Tuser;
import com.zy.smart.domain.system.TuserRole;
import com.zy.smart.model.JqGridBean;
import com.zy.smart.service.system.UserAdminService;
import com.zy.smart.utils.EncryptUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理用户Controller
 */
@Controller
@RequestMapping("/admin/user")
public class UserAdminController {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);
    
    @Autowired
    private UserAdminService userAdminService;
    
    @RequiresPermissions(value = {"用户管理"})
    @RequestMapping("/toUserManage")
    public String toUserManage() {
        return "power/user";
    }
    
    /**
     * 分页查询用户
     * 
     * @param jqGridBean
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {"用户管理"})
    @RequestMapping("/queryUserPage")
    @ResponseBody
    public Map<String, Object> queryUserPage(JqGridBean jqGridBean)
        throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        HashMap<String, Object> paramMap = new HashMap<>();
        if (StringUtils.isNotEmpty(jqGridBean.getSearchField())) {
            if ("username".equalsIgnoreCase(jqGridBean.getSearchField())) {
                if ("eq".contentEquals(jqGridBean.getSearchOper())) {
                    paramMap.put("userName", jqGridBean.getSearchString());
                }
            }
        }
        if (StringUtils.isNotEmpty(jqGridBean.getSidx()) && StringUtils.isNotEmpty(jqGridBean.getSord())) {
            paramMap.put("orderByClause", jqGridBean.getSidx() + " " + jqGridBean.getSord());
        }
        Page<Tuser> pageResult = userAdminService.queryUserPage(jqGridBean, paramMap);
        // 获取用户的角色
        for (Tuser user : pageResult.getResult()) {
            List<Trole> roleList = userAdminService.queryRolesByAccount(user.getAccount());
            StringBuffer sb = new StringBuffer();
            for (Trole role : roleList) {
                sb.append("," + role.getRoleName());
            }
            user.setRoles(sb.toString().replaceFirst(",", ""));
        }
        resultMap.put("currentPage", String.valueOf(pageResult.getPageNum()));
        resultMap.put("totalPages", String.valueOf(pageResult.getPages()));
        resultMap.put("totalRecords", String.valueOf(pageResult.getTotal()));
        resultMap.put("dataMap", pageResult.getResult());
        return resultMap;
    }
    
    @RequiresPermissions(value = {"用户管理"})
    @RequestMapping("/modifyUser")
    @ResponseBody
    public Map<String, Object> modifyUser(Tuser user) {
        // 密码加密
        EncryptUtil.encryptData(user);
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        try {
            if (user.getId() == null) { // 新建
                // 首先判断用户名是否可用
                List<Tuser> userList = userAdminService.queryUserByName(user.getUserName());
                if (CollectionUtils.isNotEmpty(userList)) {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "当前用户名已存在");
                    return resultMap;
                }
                userAdminService.saveUser(user);
            }
            else { // 修改
                Tuser oldUser = userAdminService.queryUserById(user.getId());
                if (oldUser == null) {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "当前用户名不存在");
                    return resultMap;
                }
                userAdminService.updateUser(user);
            }
            resultMap.put("state", "success");
            resultMap.put("msg", "操作成功");
            return resultMap;
        }
        catch (Exception e) {
            resultMap.put("state", "fail");
            resultMap.put("msg", "操作失败");
            return resultMap;
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "/deleteUser")
    @RequiresPermissions(value = {"用户管理"})
    public Map<String, Object> deleteUser(Tuser tuser) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            if (tuser.getId() != null && !tuser.getId().equals(0)) {
                Tuser user = userAdminService.queryUserById(tuser.getId());
                if (user == null) {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "删除失败,无法找到该记录");
                    return resultMap;
                }
                else {
                    // 还需删除用户角色中间表
                    userAdminService.deleteUserRole(user.getAccount());
                    userAdminService.deleteUser(tuser.getId());
                }
            }
            else {
                resultMap.put("state", "fail");
                resultMap.put("msg", "删除失败");
            }
            resultMap.put("state", "success");
            resultMap.put("msg", "删除成功");
            return resultMap;
        }
        catch (Exception e) {
            LOGGER.error("/toUserManage/deleteUser ERROR", e);
            resultMap.put("state", "fail");
            resultMap.put("msg", "删除失败");
            return resultMap;
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "/selectUserById")
    @RequiresPermissions(value = {"用户管理"})
    public Map<String, Object> selectUserById(Tuser tuser) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            if (tuser.getId() != null && !tuser.getId().equals(0)) {
                tuser = userAdminService.queryUserById(tuser.getId());
                if (tuser == null) {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "无法找到该记录");
                    return resultMap;
                }
            }
            else {
                resultMap.put("state", "fail");
                resultMap.put("msg", "无法找到该记录的id");
                return resultMap;
            }
            
            List<Trole> roleList = userAdminService.queryRolesByAccount(tuser.getAccount());
            StringBuffer sb = new StringBuffer();
            for (Trole r : roleList) {
                sb.append("," + r.getRoleName());
            }
            tuser.setRoles(sb.toString().replaceFirst(",", ""));
            // 所有角色
            List<Trole> allRoleList = userAdminService.queryAllRole();
            resultMap.put("roleList", roleList);// 用户拥有的所有角色
            Iterator<Trole> it = allRoleList.iterator();
            while (it.hasNext()) {
                Trole temp = it.next();
                for (Trole e2 : roleList) {
                    if (temp.getId().compareTo(e2.getId()) == 0) {
                        it.remove();
                    }
                }
            }
            
            List<Trole> notInRoleList = allRoleList;
            
            resultMap.put("notInRoleList", notInRoleList);// 用户不拥有的角色
            
            resultMap.put("tuser", tuser);
            resultMap.put("state", "success");
            resultMap.put("msg", "获取成功");
            return resultMap;
        }
        catch (Exception e) {
            LOGGER.error("/toUserManage/selectUserById ERROR", e);
            resultMap.put("state", "fail");
            resultMap.put("msg", "获取失败");
            return resultMap;
        }
    }
    
    // 设置用户角色
    @ResponseBody
    @RequestMapping(value = "/saveRoleSet")
    @RequiresPermissions(value = {"用户管理"})
    public Map<String, Object> saveRoleSet(Integer[] role, Integer id) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            // 根据用户id删除所有用户角色关联实体
            Tuser user = userAdminService.queryUserById(id);
            userAdminService.deleteUserRole(user.getAccount());
            
            if (role != null && role.length > 0) {
                for (Integer roleid : role) {
                    TuserRole tuserRole = new TuserRole();
                    tuserRole.setRoleId(roleid);
                    tuserRole.setAccount(user.getAccount());
                    userAdminService.saveUserRole(tuserRole);
                }
            }
            resultmap.put("state", "success");
            resultmap.put("msg", "设置成功");
            return resultmap;
        }
        catch (Exception e) {
            LOGGER.error("/toUserManage/saveRoleSet ERROR", e);
            resultmap.put("state", "fail");
            resultmap.put("msg", "设置失败");
            return resultmap;
        }
    }
    
    // 跳转到修改密码页面
    @RequestMapping("/toUpdatePassword")
    @RequiresPermissions(value = {"修改密码"})
    public String toUpdatePassword() {
        return "power/updatePassword";
    }
    
    // 修改密码
    @ResponseBody
    @PostMapping("/updatePassword")
    @RequiresPermissions(value = {"修改密码"})
    public Map<String, Object> updatePassword(Tuser tuser)
        throws Exception {
        // 加密
        EncryptUtil.encryptData(tuser);
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            
            if (tuser == null) {
                resultMap.put("state", "fail");
                resultMap.put("msg", "设置失败，缺乏字段信息");
                return resultMap;
            }
            else {
                if (tuser.getId() != null && tuser.getId().intValue() != 0
                    && StringUtils.isNotEmpty(tuser.getUserName()) && StringUtils.isNotEmpty(tuser.getOldPassword())
                    && StringUtils.isNotEmpty(tuser.getPassword())) {
                    HashMap<String, Object> paramMap = new HashMap<>();
                    paramMap.put("id", tuser.getId());
                    paramMap.put("userName", tuser.getUserName());
                    paramMap.put("password", tuser.getOldPassword());
                    List<Tuser> tuserList = userAdminService.queryUserByParam(paramMap);
                    if (tuserList == null || tuserList.size() == 0) {
                        resultMap.put("state", "fail");
                        resultMap.put("msg", "用户名或密码错误");
                        return resultMap;
                    }
                    else {
                        Tuser newEntity = tuserList.get(0);
                        newEntity.setPassword(tuser.getPassword());
                        userAdminService.updatePassword(tuser);
                    }
                }
                else {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "设置失败，缺乏字段信息");
                    return resultMap;
                }
            }
            
            resultMap.put("state", "success");
            resultMap.put("msg", "密码修改成功");
            return resultMap;
        }
        catch (Exception e) {
            e.printStackTrace();
            resultMap.put("state", "fail");
            resultMap.put("msg", "密码修改失败，系统异常");
            return resultMap;
        }
    }
}
