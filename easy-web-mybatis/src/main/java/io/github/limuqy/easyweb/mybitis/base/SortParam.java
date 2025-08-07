package io.github.limuqy.easyweb.mybitis.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortParam {

    @Schema(title = "排序字段")
    private String name;

    @Schema(title = "true：升序，false：降序")
    private boolean asc;

}
