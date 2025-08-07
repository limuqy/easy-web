package io.github.limuqy.easyweb.model.mybatis;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class BaseEntity {

    /**
     * 主键字段，主键默认采用SEQUENCE方式生成
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建人（取工号字段）
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @Schema(title = "创建人", description = "创建人（取工号字段）")
    private String createBy;

    /**
     * 创建人姓名
     * 该字段为冗余字段，目的在于
     * 1. 界面上姓名是基础字段，大部分情况下都要显示，冗余姓名后可以减少表关联查询
     * 2. 姓名冗余可以增加在没有用户数据（离职等）情况下的可读性
     * 如果用户姓名更新，冗余字段不进行更新，理由是
     * 1. 姓名作为基础字段，一般很少更新，但开发量不小，投入产出比不高
     * 2. 该字段没有业务意义，只是做界面展示，用户在界面上也可以通过点击姓名获取用户实时信息，所以对业务影响很小，更新必要性不强
     */
    @TableField(value = "create_by_name", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @Schema(title = "创建人姓名")
    private String createByName;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @Schema(title = "创建时间")
    private Timestamp createTime;

    /**
     * 最近更新人（取工号字段）
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    @Schema(title = "最近更新人", description = "最近更新人（取工号字段）")
    private String updateBy;

    /**
     * 最近更新人姓名（冗余字段，原因见createByName）
     */
    @TableField(value = "update_by_name", fill = FieldFill.INSERT_UPDATE)
    @Schema(title = "最近更新人姓名")
    private String updateByName;

    /**
     * 最近更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(title = "最近更新时间")
    private Timestamp updateTime;

    /**
     * 版本号
     * 该字段用于乐观锁，更新数据时需要带上版本号，只有版本号一致时才能更新成功，否则会抛出异常
     */
    @TableField(value = "reversion", fill = FieldFill.INSERT_UPDATE)
    @Version
    @Schema(title = "版本号")
    private Long reversion;

    /**
     * 逻辑删除标识
     * 0 -> 逻辑未删除值 1 -> 逻辑已删除值
     */
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Long deleted = 0L;
}
