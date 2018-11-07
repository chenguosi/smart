package com.zy.smart.sys;

import com.zy.smart.domain.system.Tmenu;
import com.zy.smart.domain.system.Trole;
import com.zy.smart.domain.system.Tuser;
import com.zy.smart.service.system.AuthorService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取用户的角色和权限信息
 */
public class ShiroRealm extends AuthorizingRealm {


    @Autowired
    private AuthorService authorService;

    /**
     * 登录验证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        // 查出是否有此用户
        Tuser user = authorService.findUserByAccount(token.getUsername());
        if (user != null) {
            // 若存在，将此用户存放到登录认证info中，无需自己坐密码比对，Shiro会为我们进行密码对比校验
            List<Trole> roleList = authorService.findRoleByAccount(user.getAccount());
            List<Tmenu> menuList = authorService.findMenuByAccount(user.getAccount());
            // 获取一级菜单
            List<Tmenu> menuOneClassList = authorService.findMenuOneClassByAccount(user.getAccount());
            user.setMenuOneClassList(menuOneClassList);
            ArrayList<String> roleStrList = new ArrayList<>();
            ArrayList<String> menuStrList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(roleList)) {
                for (Trole role : roleList) {
                    roleStrList.add(role.getRoleName());
                }
            }
            if (CollectionUtils.isNotEmpty(menuList)) {
                for (Tmenu menu : menuList) {
                    menuStrList.add(menu.getName());
                }
            }
            user.setRoleStrList(roleStrList);
            user.setMenuStrList(menuStrList);
            // 当前realm对象的name
            String realmName = getName();
            // 盐值
            ByteSource credentialsSalt = ByteSource.Util.bytes(user.getAccount());
            // 封装用户信息，构建AuthenticationInfo对象并返回
            AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, user.getPassword(),
                    credentialsSalt, realmName);
            return authenticationInfo;
        }
        return null;
    }

    /**
     * 权限认证
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取当前登录输入的用户名
        Tuser user = (Tuser) principalCollection.getPrimaryPrincipal();
        if (user != null) {
            // 权限信息对象info，用来存放查出来的用户的所有的角色(role)及权限(permission)
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            // 用户的角色集合
            info.addRoles(user.getRoleStrList());
            // 用户的权限集合
            info.addStringPermissions(user.getMenuStrList());
            return info;
        }
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }
}
