package io.github.limuqy.easyweb.mybitis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.github.limuqy.easyweb.core.context.AppContext;
import io.github.limuqy.easyweb.core.util.DateUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class AutoFillHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Timestamp now = DateUtil.timestamp();
        this.strictInsertFill(metaObject, "createBy", String.class, AppContext.getEmployeeCode());
        this.strictInsertFill(metaObject, "createByName", String.class, AppContext.getEmployeeNameJointCode());
        this.strictInsertFill(metaObject, "updateBy", String.class, AppContext.getEmployeeCode());
        this.strictInsertFill(metaObject, "updateByName", String.class, AppContext.getEmployeeNameJointCode());
        this.strictInsertFill(metaObject, "createTime", Timestamp.class, now);
        this.strictInsertFill(metaObject, "updateTime", Timestamp.class, now);
        this.strictInsertFill(metaObject, "reversion", Long.class, 1L);
        this.strictInsertFill(metaObject, "deleted", Long.class, 0L);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateBy", AppContext.getEmployeeCode());
        metaObject.setValue("updateByName", AppContext.getEmployeeNameJointCode());
        metaObject.setValue("updateTime", DateUtil.timestamp());
    }

}
