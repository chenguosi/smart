package com.zy.smart.controller.system;

import com.github.pagehelper.Page;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.TroleMenu;
import com.zy.smart.model.JqGridBean;
import com.zy.smart.service.system.RoleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 */
@Controller
@RequestMapping("admin/role")
public class RoleController {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(RoleController.class);
    
    @Autowired
    private RoleService roleService;
    
    @RequestMapping("/toRoleManage")
    @RequiresPermissions(value = {"角色管理"})
    public String toRoleManager() {
        return "power/role";
    }
    
    @RequiresPermissions(value = {"角色管理"})
    @RequestMapping("/getRoleList")
    @ResponseBody
    public Map<String, Object> getRoleList(JqGridBean jqGridBean)
        throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        HashMap<String, Object> paramMap = new HashMap<>();
        if (StringUtils.isNotBlank(jqGridBean.getSearchField())) {
            if ("name".equals(jqGridBean.getSearchField())) {
                if ("eq".equals(jqGridBean.getSearchOper())) {
                    paramMap.put("name", jqGridBean.getSearchString());
                }
            }
        }
        Page<Trole> pageResult = roleService.queryRolePage(jqGridBean, paramMap);
        resultMap.put("currentPage", String.valueOf(pageResult.getPageNum()));
        resultMap.put("totalPages", String.valueOf(pageResult.getPages()));
        resultMap.put("totalRecords", String.valueOf(pageResult.getTotal()));
        resultMap.put("dataMap", pageResult.getResult());
        return resultMap;
    }
    
    @RequiresPermissions(value = {"角色管理"})
    @RequestMapping("/modifyRole")
    @ResponseBody
    public Map<String, Object> modifyRole(Trole role) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        try {
            if (role.getId() == null) { // 新建
                // 判断修改的角色名是否已存在，存在不允许修改，不存在允许修改
                List<Trole> roleList = roleService.queryRoleByParam(role);
                if (CollectionUtils.isNotEmpty(roleList)) {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "当前角色名已存在");
                    return resultMap;
                }
                // 新增
                roleService.insertRole(role);
            }
            else { // 修改
                Trole oldRole = roleService.queryRoleById(role.getId());
                if (oldRole == null) {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "当前角色不存在");
                    return resultMap;
                }
                roleService.updateRole(role);
            }
            resultMap.put("state", "success");
            resultMap.put("msg", "操作成功");
            return resultMap;
        }
        catch (Exception e) {
            LOGGER.error("/toRoleManager/modifyRole ERROR", e);
            resultMap.put("state", "fail");
            resultMap.put("msg", "操作失败");
            return resultMap;
        }
    }
    
    @RequiresPermissions(value = {"角色管理"})
    @RequestMapping("/deleteRole")
    @ResponseBody
    public Map<String, Object> deleteRole(Trole role) {
        HashMap<String, Object> resultMap = new HashMap<>();
        try {
            if (role.getId() != null && !role.getId().equals(0)) {
                Trole oldRole = roleService.queryRoleById(role.getId());
                if (oldRole == null) {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "删除失败，无法找到该记录");
                    return resultMap;
                }
                // 删除用户-角色、角色-菜单表数据
                roleService.deleteUserRoleByRoleId(role.getId());
                roleService.deleteMenuRoleByRoleId(role.getId());
                roleService.deleteRoleById(role.getId());
                resultMap.put("state", "success");
                resultMap.put("msg", "删除成功");
                return resultMap;
            }
            else {
                resultMap.put("state", "fail");
                resultMap.put("msg", "删除失败");
                return resultMap;
            }
        }
        catch (Exception e) {
            LOGGER.error("/toRoleManager/deleteRole ERROR", e);
            resultMap.put("state", "fail");
            resultMap.put("msg", "删除失败");
            return resultMap;
        }
    }
    
    @RequiresPermissions(value = {"角色管理"})
    @RequestMapping("/selectRoleById")
    @ResponseBody
    public Map<String, Object> selectRoleById(Trole role) {
        HashMap<String, Object> resultMap = new HashMap<>();
        try {
            if (role.getId() != null && !role.getId().equals(0)) {
                role = roleService.queryRoleById(role.getId());
                if (role == null) {
                    resultMap.put("state", "fail");
                    resultMap.put("msg", "无法找到该记录");
                    return resultMap;
                }
                resultMap.put("state", "success");
                resultMap.put("msg", "获取成功");
                resultMap.put("trole", role);
                return resultMap;
            }
            else {
                resultMap.put("state", "fail");
                resultMap.put("msg", "无法找到该记录的ID");
                return resultMap;
            }
            
        }
        catch (Exception e) {
            LOGGER.error("/toRoleManager/selectRoleById ERROR", e);
            resultMap.put("state", "fail");
            resultMap.put("msg", "获取失败");
            return resultMap;
        }
    }
    
    /**
     * 根据父节点获取所有复选框权限菜单树
     * 
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {"角色管理"})
    @RequestMapping("/loadCheckMenuInfo")
    @ResponseBody
    public String loadCheckMenuInfo(Integer parentId, Integer roleId)
        throws Exception {
        List<Tmenu> tmenuList = roleService.queryMenusByRoleId(roleId); // 根据角色查询所有权限菜单信息
        // 移除所有没有PID的menu
        Iterator<Tmenu> iterator = tmenuList.iterator();
        while (iterator.hasNext()) {
            Tmenu tmenu = iterator.next();
            if (tmenu.getId() == null) {
                iterator.remove();
            }
        }
        List<Integer> menuIdList = new LinkedList<>();
        for (Tmenu tmenu : tmenuList) {
            menuIdList.add(tmenu.getId());
        }
        String json = this.getAllCheckedMenuByParentId(parentId, menuIdList).toString();
        return json;
    }
    
    /**
     * 根据父节点ID和权限菜单ID集合获取复选框菜单节点
     *
     * @param parentId
     * @param menuIdList
     * @return
     */
    private JsonArray getAllCheckedMenuByParentId(Integer parentId, List<Integer> menuIdList) {
        JsonArray jsonArray = this.getCheckedMenuByParentId(parentId, menuIdList);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject)jsonArray.get(i);
            // 判断该节点下是否还有子节点
            int count = roleService.selectMenuCountByPid(jsonObject.get("id").getAsInt());
            if (count == 0) {
                continue;
            }
            else {
                jsonObject.add("children", getAllCheckedMenuByParentId(jsonObject.get("id").getAsInt(), menuIdList));
            }
        }
        return jsonArray;
    }
    
    /**
     * 根据父节点ID和权限菜单ID集合获取复选框菜单节点
     *
     * @param parentId
     * @param menuIdList
     * @return
     */
    private JsonArray getCheckedMenuByParentId(Integer parentId, List<Integer> menuIdList) {
        List<Tmenu> menuList = roleService.queryMenusByPid(parentId);
        JsonArray jsonArray = new JsonArray();
        for (Tmenu tmenu : menuList) {
            JsonObject jsonObject = new JsonObject();
            Integer menuId = tmenu.getId();
            jsonObject.addProperty("id", menuId); // 节点id
            jsonObject.addProperty("name", tmenu.getName()); // 节点名称
            // 判断该节点下是否还有子节点
            int count = roleService.selectMenuCountByPid(jsonObject.get("id").getAsInt());
            if (count == 0) {
                jsonObject.addProperty("open", "true"); // 无子节点
            }
            else {
                jsonObject.addProperty("open", "false"); // 有子节点
            }
            if (menuIdList.contains(menuId)) {
                jsonObject.addProperty("checked", true);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
    
    /**
     * 保存角色权限设置
     *
     * @param menuIds
     * @param roleId
     * @return
     * @throws Exception
     */
    @RequiresPermissions(value = {"角色管理"})
    @RequestMapping("/saveMenuSet")
    @ResponseBody
    public Map<String, Object> saveMenuSet(String menuIds, Integer roleId)
        throws Exception {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        
        if (StringUtils.isNotEmpty(menuIds)) {
            // 先根据roleId查询出原有的对应的所有menuId集合
            List<Tmenu> menuList = roleService.queryMenusByRoleId(roleId);
            // 移除所有没有pid的menuId
            Iterator<Tmenu> it = menuList.iterator();
            while (it.hasNext()) {
                Tmenu tmenu = it.next();
                if (tmenu.getpId() == null) {
                    it.remove();
                }
            }
            List<Integer> menuIdList = new LinkedList<Integer>();
            for (Tmenu menu : menuList) {
                menuIdList.add(menu.getId());
            }
            
            if (menuIdList != null && menuIdList.size() > 0) {
                // 删除角色权限表
                roleService.deleteRoleMenuByParam(roleId, menuIdList);
            }
            
            String idsStr[] = menuIds.split(",");
            for (int i = 0; i < idsStr.length; i++) { // 然后添加所有角色权限关联实体
                TroleMenu troleMenu = new TroleMenu();
                troleMenu.setRoleId(roleId);
                troleMenu.setMenuId(Integer.parseInt(idsStr[i]));
                roleService.saveRoleMenu(troleMenu);
            }
        }
        else {
            resultMap.put("state", "fail");
            resultMap.put("msg", "操作失败，未获取选中记录，请重新选择");
            return resultMap;
        }
        resultMap.put("state", "success");
        resultMap.put("msg", "操作成功");
        return resultMap;
    }
    
}
