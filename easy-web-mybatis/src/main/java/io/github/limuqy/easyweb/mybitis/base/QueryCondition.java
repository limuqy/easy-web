package io.github.limuqy.easyweb.mybitis.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QueryCondition extends QueryParam {

    @Schema(title = "条件集关系是否为或")
    private boolean or = false;
    @Schema(title = "条件集")
    private List<QueryCondition> conditions;

    /**
     * 是否是集合条件
     */
    public boolean isCollection() {
        return conditions.size() > 1;
    }
}
