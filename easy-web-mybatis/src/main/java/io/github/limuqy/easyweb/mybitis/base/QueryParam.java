package io.github.limuqy.easyweb.mybitis.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QueryParam {
    /**
     * 字段
     */
    @Schema(title = "字段")
    private String name;
    /**
     * 单值
     */
    @Schema(title = "单值")
    private String value;
    /**
     * 多值：范围/批量匹配时使用
     */
    @Schema(title = "多值", description = "范围/批量匹配时使用")
    private List<String> values;
    /**
     * 匹配类型
     * eq：等于；ne：不等于；like：模糊查询；in：多值匹配(values)；notIn：不包含(values)；
     * gt：大于；ge：大于等于；lt：小于；le：小于等于；between：在XXX之间(values[1],values[2])，用于数值或时间；
     * isNull：为空；isNotNull：不为空；
     */
    @Schema(title = "匹配类型", description = "eq：等于；ne：不等于；like：模糊查询；in：多值匹配(values)；notIn：不包含(values)；\n" +
            "gt：大于；ge：大于等于；lt：小于；le：小于等于；between：在XXX之间(values[1],values[2])，用于数值或时间；\n" +
            "isNull：为空；isNotNull：不为空；")
    private String op;
    /**
     * 数据类型
     */
    @Schema(title = "数据类型")
    private String type;
}
