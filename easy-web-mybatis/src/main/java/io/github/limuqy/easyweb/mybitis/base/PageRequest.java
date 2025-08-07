package io.github.limuqy.easyweb.mybitis.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest extends QueryRequest {
    @Schema(title = "页数")
    private Integer page;
    @Schema(title = "每页条数")
    private Integer limit;
    @Schema(title = "滚动分页ID")
    private String scrollId;
    public <T> Page<T> getPage(Class<T> clazz) {
        return Page.of(page, limit);
    }
}
