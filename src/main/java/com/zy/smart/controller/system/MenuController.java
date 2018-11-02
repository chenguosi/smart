package com.zy.smart.controller.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.service.system.AuthorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@Controller
@RequestMapping("admin/menu")
public class MenuController {

    @Autowired
    private AuthorService authorService;

    @RequestMapping("/toMenuManage")
    @RequiresPermissions(value = {"菜单管理"})
    public String toMenuManage() {
        return "power/menu";
    }

    /**
     * 查询parentId为1的所有菜单及子菜单集合
     * @param parentId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/loadCheckMenuInfo")
    @RequiresPermissions(value = {"菜单管理"})
    public String loadCheckMenuInfo(Integer parentId) throws Exception {
        JsonArray json = getAllMenuByParentId(parentId);
        String result = json.toString();
        return result;
    }

    private JsonArray getAllMenuByParentId(Integer parentId) {
        JsonArray jsonArray = this.getMenuByParentId(parentId);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
            //判断该节点下时候还有子节点
            int count = authorService.selectChildCount(jsonObject.get("id").getAsInt());
            if (count == 0) {
                continue;
            } else {
                jsonObject.add("children", getAllMenuByParentId(jsonObject.get("id").getAsInt()));
            }
        }
        return jsonArray;
    }

    private JsonArray getMenuByParentId(Integer parentId) {
        List<Tmenu> menuList = authorService.selectMenuByPid(parentId);
        JsonArray jsonArray = new JsonArray();
        for (Tmenu menu : menuList) {
            JsonObject jsonObject = new JsonObject();
            Integer menuId = menu.getId();
            jsonObject.addProperty("id", menuId); // 节点id
            jsonObject.addProperty("name", menu.getName()); // 节点名称
            //判断该节点下是否还有子节点
            int count = authorService.selectChildCount(jsonObject.get("id").getAsInt());
            if (count == 0) {
                jsonObject.addProperty("open", "false"); // 无子节点
            } else {
                jsonObject.addProperty("open", "true"); // 有子节点
            }
            jsonObject.addProperty("state", String.valueOf(menu.getState()));
            jsonObject.addProperty("iconValue", menu.getIcon());
            jsonObject.addProperty("pId", String.valueOf(menu.getpId()));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 编辑节点之前将该节点select出来
     * @return
     */
    @ResponseBody
    @RequiresPermissions(value = {"菜单管理"})
    @RequestMapping("/selectMenuById")
    public Map<String,Object> selectMenuById(Integer id){
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if(id==null||id==0){
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法获取节点id");
                return resultmap;
            }else{
                Tmenu tmenu=authorService.selectMenuById(id);
                if(tmenu==null){
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该节点对象");
                    return resultmap;
                }else{
                    resultmap.put("state", "success");
                    resultmap.put("mesg", "获取该节点对象成功");
                    resultmap.put("tmenu", tmenu);
                    return resultmap;
                }
            }

        } catch (Exception e) {
            resultmap.put("state", "fail");
            resultmap.put("mesg", "操作失败，系统异常");
            return resultmap;
        }
    }

    @ResponseBody
    @RequestMapping("modifyMenu")
    @RequiresPermissions(value = {"菜单管理"})
    public Map<String,Object> modifyMenu(HttpSession session, Tmenu tmenu){
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if (tmenu.getId() == null) {//新建
                //首先校验本次新增操作提交的菜单对象中的name属性的值是否存在
                List<Tmenu> menuList = authorService.selectMenuByName(tmenu.getName());
                if (menuList != null && menuList.size() > 0) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "当前菜单名已存在");
                    return resultmap;
                }

                //校验是否提交了pId
                if(tmenu.getpId()==null||tmenu.getpId()==0){
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法获取父级id");
                    return resultmap;
                }else{
                    Tmenu pmenu=authorService.selectMenuById(tmenu.getpId());//父节点对象
                    if(pmenu.getState()==3){
                        resultmap.put("state", "fail");
                        resultmap.put("mesg", "3级菜单不可再添加子菜单");
                        return resultmap;
                    }
                    if("-1".equalsIgnoreCase(String.valueOf(pmenu.getpId()))
                            &&"1".equalsIgnoreCase(String.valueOf(pmenu.getState()))){//如果父节点是最顶级那一个，则本次新增为一级菜单

                        //一级菜单的名字不可为纯数字
                        if(isNumeric(tmenu.getName())){
                            resultmap.put("state", "fail");
                            resultmap.put("mesg", "1级菜单的名字不可为纯数字");
                            return resultmap;
                        }

                        tmenu.setState(1);
                    }else if("1".equalsIgnoreCase(String.valueOf(pmenu.getpId()))
                            &&"1".equalsIgnoreCase(String.valueOf(pmenu.getState()))){//如果父节点是一级菜单，本次新增为2级菜单
                        tmenu.setState(2);
                    }else if(!"1".equalsIgnoreCase(String.valueOf(pmenu.getpId()))
                            &&"2".equalsIgnoreCase(String.valueOf(pmenu.getState()))){//如果父节点是二级菜单，本次新增为3级菜单
                        tmenu.setState(3);
                    }

                    //指定pid的值，根据id倒序查询同级菜单集合
                    List<Tmenu> list = authorService.selectMenuByPidDesc(tmenu.getpId());
                    if(list!=null&&list.size()>0){//如果本次新增的菜单实体的同一级菜单集合不为空
                        tmenu.setId(list.get(0).getId()+1);//获取已经存在的同级菜单的id的最大值+1
                    }else{//如果本次新增的菜单实体还没有同一级的菜单的话，则根据父节点生成子节点id
                        if("1".equalsIgnoreCase(String.valueOf(tmenu.getpId()))){
                            tmenu.setId(tmenu.getpId()*10);//第一个一级菜单id为1*10
                        }else{
                            tmenu.setId(tmenu.getpId()*100);//二级三级菜单id生成策略为根据父菜单id*100
                        }
                    }

                }
                authorService.saveMenu(tmenu);
            } else {//编辑(对于节点的编辑只允许编辑icon、name、url)
                //首先校验本次编辑操作提交的菜单对象中的name属性的值是否存在
                List<Tmenu> menuList = authorService.selectMenuByName(tmenu.getName());
                if (menuList.size() > 0
                        && menuList.get(0).getId() != tmenu.getId()) {//如果本次提交的名字在本次修改的节点之外已经存在
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "当前菜单名已存在");
                    return resultmap;
                }else{
                    Tmenu tmenuNew=new Tmenu();
                    tmenuNew.setId(tmenu.getId());
                    if(StringUtils.isNotEmpty(tmenu.getIcon())){
                        tmenuNew.setIcon(tmenu.getIcon());
                    }
                    if(StringUtils.isNotEmpty(tmenu.getName())){
                        tmenuNew.setName(tmenu.getName());
                    }
                    if(StringUtils.isNotEmpty(tmenu.getUrl())){
                        tmenuNew.setUrl(tmenu.getUrl());
                    }
                    authorService.updateMenu(tmenuNew);
                }

            }
            resultmap.put("state", "success");
            resultmap.put("mesg", "操作成功");
            resultmap.put("id", String.valueOf(tmenu.getId()));


            return resultmap;
        } catch (Exception e) {
            resultmap.put("state", "fail");
            resultmap.put("mesg", "操作失败，系统异常");
            return resultmap;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/deleteMenu")
    @RequiresPermissions(value = {"菜单管理"})
    public Map<String, Object> deleteMenu(HttpSession session,Tmenu tmenu) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if(tmenu.getId()!=null&&!tmenu.getId().equals(0)){
                Tmenu menu=authorService.selectMenuById(tmenu.getId());
                if(menu==null){
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "删除失败,无法找到该记录");
                    return resultmap;
                }else{
                    //还需删除中间表
                    if(true){
                        authorService.deleteRoleMenu(tmenu.getId());
                    }
                    //删除该节点的所有子节点
                    if(true){
                        authorService.deleteMenuByPid(tmenu.getId());
                    }
                    //删除该节点
                    authorService.deleteMenuById(tmenu.getId());
                }
            }else{
                resultmap.put("state", "fail");
                resultmap.put("mesg", "删除失败");
            }


            resultmap.put("state", "success");
            resultmap.put("mesg", "删除成功");
            return resultmap;
        } catch (Exception e) {
            resultmap.put("state", "fail");
            resultmap.put("mesg", "删除失败，系统异常");
            return resultmap;
        }
    }

}
