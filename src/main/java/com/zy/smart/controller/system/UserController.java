package com.zy.smart.controller.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Tuser;
import com.zy.smart.service.system.AuthorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private AuthorService authorService;

    /**
     * 加载权限菜单
     *
     * @return
     */
    @GetMapping("/loadMenuInfo")
    @ResponseBody
    public String loadMenuInfo(HttpSession session, Integer parentId) throws Exception {
        List<Tmenu> menuOneClassList = authorService.findMenuOneClass();
        session.setAttribute("tmenuOneClassList", menuOneClassList);
        Tuser user = (Tuser) session.getAttribute("currentUser");
        //根据当前用户的账号和父节点id查询所有菜单及子集json
        String json = getAllMenuByParentId(parentId, user.getAccount()).toString();
        return json;
    }

    private JsonObject getAllMenuByParentId(Integer parentId, String account) {
        JsonObject resultObject = new JsonObject();
        // 根据父节点和账号获取所有一级菜单
        JsonArray jsonArray = this.getMenuByParentId(parentId, account);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
            //判断该节点下时候还有子节点
            int childNum = authorService.selectChildCount(jsonObject.get("id").getAsInt());
            //if("true".equals(jsonObject.get("spread").getAsString())){
            if (childNum == 0) {
                continue;
            } else {
                //由于后台模板的规定，一级菜单以title作为json的key
                resultObject.add(jsonObject.get("title").getAsString(),
                        getAllMenuJsonArrayByParentId(jsonObject.get("id").getAsInt(), account));
            }
        }
        return resultObject;
    }

    private JsonArray getAllMenuJsonArrayByParentId(int id, String account) {
        JsonArray jsonArray = this.getMenuByParentId(id, account);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
            //判断该节点下是否还有子节点
            int childNum = authorService.selectChildCount(jsonObject.get("id").getAsInt());
            //if("true".equals(jsonObject.get("spread").getAsString())){
            if (childNum == 0) {
                continue;
            } else {
                //二级或三级菜单
                jsonObject.add("children", getAllMenuJsonArrayByParentId(jsonObject.get("id").getAsInt(), account));
            }
        }
        return jsonArray;
    }

    private JsonArray getMenuByParentId(Integer parentId, String account) {
        HashMap<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("pid", parentId);
        paraMap.put("account", account);
        List<Tmenu> menuList = authorService.selectByParentIdAndAccount(paraMap);
        JsonArray jsonArray = new JsonArray();
        for (Tmenu menu : menuList) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", menu.getId()); // 节点id
            jsonObject.addProperty("title", menu.getName()); // 节点名称
            jsonObject.addProperty("spread", false); // 不展开
            jsonObject.addProperty("icon", menu.getIcon());
            if (StringUtils.isNotEmpty(menu.getUrl())) {
                jsonObject.addProperty("href", menu.getUrl()); // 菜单请求地址
            }
            jsonArray.add(jsonObject);

        }
        return jsonArray;
    }

}
