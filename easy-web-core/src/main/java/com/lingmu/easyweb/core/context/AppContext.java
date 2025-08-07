package com.lingmu.easyweb.core.context;

import com.lingmu.easyweb.core.constant.Constant;
import com.lingmu.easyweb.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppContext {

    private static final ThreadLocal<UserProfile> userProfile = new ThreadLocal<>();

    /**
     * 获取当前上下文用户信息
     */
    public static UserProfile getUserProfile() {
        return userProfile.get();
    }

    /**
     * 获取当前登录用户工号
     */
    public static String getEmployeeCode() {
        UserProfile profile = userProfile.get();
        return profile == null ? Constant.ANONYMOUS_USER_CODE : profile.getEmployeeCode();
    }

    /**
     * 获取当前登录用户姓名
     */
    public static String getEmployeeName() {
        UserProfile profile = userProfile.get();
        return profile == null ? Constant.ANONYMOUS_USER_NAME : profile.getEmployeeName();
    }

    /**
     * 获取当前登录用户姓名(工号)
     */
    public static String getEmployeeNameJointCode() {
        UserProfile profile = userProfile.get();
        if (profile == null) {
            return Constant.ANONYMOUS_USER_CODE;
        }
        String employeeCode = AppContext.getEmployeeCode();
        String employeeName = AppContext.getEmployeeName();
        // 当上下文中，员工姓名、员工工号同时不为空时才进行拼接字符串
        if (StringUtil.isNotBlank(employeeName)
                && StringUtil.isNotBlank(employeeCode)
                && !Constant.ANONYMOUS_USER_CODE.equals(employeeName)
                && !Constant.ANONYMOUS_USER_NAME.equals(employeeCode)) {
            return String.format("%s(%s)", employeeName, employeeCode);
        }
        // 工号不为空，直接返回
        if (StringUtil.isNotBlank(employeeCode)) {
            return employeeCode;
        }
        return StringUtil.isNotBlank(employeeName) ? employeeName : Constant.ANONYMOUS_USER_NAME;
    }

    /**
     * 获取当前上下文用户组织
     */
    public static String getOrgCode() {
        UserProfile profile = userProfile.get();
        return profile == null ? Constant.EMPTY : profile.getOrgCode();
    }

    /**
     * 获取当前上下文用户角色
     */
    public static List<String> getRoleCodes() {
        UserProfile profile = userProfile.get();
        return profile == null ? new ArrayList<>() : profile.getUserRolesCodes();
    }

    /**
     * 判断是否超级管理员
     */
    public static boolean isSuperAdmin() {
        UserProfile profile = userProfile.get();
        return Objects.nonNull(profile) && profile.getSuperAdmin();
    }

    /**
     * 判断当前用户是否匿名
     */
    public static boolean isAnonymous() {
        return getUserProfile() == null;
    }

    /**
     * 设置当前上下文用户信息
     */
    public static void setUserProfile(UserProfile userProfile) {
        AppContext.userProfile.set(userProfile);
    }

    /**
     * 移除当前上下文用户信息
     */
    public static void removeUserProfile() {
        AppContext.userProfile.remove();
    }

    public static void clear() {
        removeUserProfile();
    }

    /**
     * 当用户无登录访问系统时调用
     */
    public static void setAnonymous() {
        //备用
    }

}
