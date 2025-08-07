package io.github.limuqy.easyweb.model.core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserProfile {

    /**
     * 员工工号
     */
    private String employeeCode;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 全路径组织编号
     */
    private List<String> fullOrgCodes = new ArrayList<>();

    /**
     * 组织编号
     */
    private String orgCode;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 角色编码
     */
    private List<String> userRolesCodes = new ArrayList<>();

    private Boolean superAdmin = false;

    /**
     * 最近session刷新时间
     */
    private Long lastSessionRefreshTime;

    /**
     * token
     */
    private String token;

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;
}
