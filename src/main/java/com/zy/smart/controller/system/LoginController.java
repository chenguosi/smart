package com.zy.smart.controller.system;

import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Tuser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    
    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
        return "login";
    }
    
    /**
     * 未授权处理
     * 
     * @return
     */
    @RequestMapping("/unauthorized")
    public String unAuthorized() {
        return "error/unauthorized";
    }
    
    /**
     * 用户登录
     *
     * @return
     */
    @ResponseBody
    @PostMapping("/loginUser")
    public Map<String, Object> loginUser(String imageCode, @Valid Tuser user, BindingResult bindingResult,
        HttpSession session) {
        String checkCode = (String)session.getAttribute("checkcode");
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isEmpty(imageCode)) {
            map.put("success", false);
            map.put("errorInfo", "请输入验证码！");
            return map;
        }
        if (!checkCode.toUpperCase().equals(imageCode.toUpperCase())) {
            map.put("success", false);
            map.put("errorInfo", "验证码输入错误！");
            return map;
        }
        if (bindingResult.hasErrors()) {
            map.put("success", false);
            map.put("errorInfo", bindingResult.getFieldError().getDefaultMessage());
            return map;
        }
        UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token); // 登录验证
            user = (Tuser)subject.getPrincipal();
            session.setAttribute("currentUser", user);
            List<String> roleStrList = user.getRoleStrList();
            map.put("roleList", roleStrList);
            if (CollectionUtils.isNotEmpty(roleStrList)) {
                map.put("roleSize", roleStrList.size());
            }
            // 获取所有一级菜单
            List<Tmenu> tmenuOneClassList = user.getMenuOneClassList();
            session.setAttribute("tmenuOneClassList", tmenuOneClassList);
            map.put("success", true);
            return map;
        }
        catch (Exception e) {
            LOGGER.error("登录异常", e);
            map.put("success", false);
            map.put("errorInfo", "用户名或者密码错误！");
            return map;
        }
    }
    
}
