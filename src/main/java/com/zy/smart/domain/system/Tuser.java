package com.zy.smart.domain.system;

import java.util.List;

public class Tuser {

    private Integer id;

    private String password;

    private String account;

    private String userName;

    private String remark;

    private List<String> roleStrList;

    private List<String> menuStrList;

    private List<Tmenu> menuOneClassList; // 一级菜单列表

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getRoleStrList() {
        return roleStrList;
    }

    public void setRoleStrList(List<String> roleStrList) {
        this.roleStrList = roleStrList;
    }

    public List<String> getMenuStrList() {
        return menuStrList;
    }

    public void setMenuStrList(List<String> menuStrList) {
        this.menuStrList = menuStrList;
    }

    public List<Tmenu> getMenuOneClassList() {
        return menuOneClassList;
    }

    public void setMenuOneClassList(List<Tmenu> menuOneClassList) {
        this.menuOneClassList = menuOneClassList;
    }

}
