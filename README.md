## 特性

&#x1F680; `省心` | 避开那些弯弯绕绕的烦人配置, 更快地投入到实际的苦力中去。

&#x1F498; `初衷` | 为自己在板砖的路上步履轻盈，攒下更多的精力去享受其它。

&#x23F0; `未来` | 别再为屎山透支自己的时间, 您值得拥有更美好的未来。

## 安装

### Maven:
~~~xml
<!--spring-boot2-->
<dependency>
    <groupId>io.github.limuqy</groupId>
    <artifactId>easy-web-spring-boot2-starter</artifactId>
    <version>${latest version}</version>
</dependency>
<!--spring-boot3-->
<dependency>
    <groupId>io.github.limuqy</groupId>
    <artifactId>easy-web-spring-boot3-starter</artifactId>
    <version>${latest version}</version>
</dependency>
<!--需要数据库驱动-->
~~~

### Gradle:
~~~gradle
implementation 'io.github.limuqy:easy-web:${latest version}'
// 或者
implementation group: 'io.github.limuqy', name: 'easy-web', version: '${latest version}'
// 需要数据库驱动
~~~

## 文档

### 创建Bean:
~~~sql
create table test_data
(
    id             bigint auto_increment,
    create_by      varchar(255)   null comment '创建人',
    create_by_name varchar(255)   null comment '创建人姓名',
    create_time    timestamp      null comment '创建时间',
    update_by      varchar(255)   null comment '更新人',
    update_by_name varchar(255)   null comment '更新人姓名',
    update_time    timestamp      null comment '更新时间',
    reversion      bigint           null comment '版本号',
    deleted        bigint default 0 null comment '是否删除（>0为删除）',
    code           varchar(255)   null comment '编码',
    name           varchar(255)   null comment '名称',
    constraint t_test_data_pk
        primary key (id)
);
~~~

### 创建Bean:
~~~Java
@Getter
@Setter
@TableName("test_data")
public class TestDataEntity extends BaseEntity {
    @ExcelProperty("编码")
    private String code;

    @ExcelProperty("姓名")
    private String name;
}

@Getter
@Setter
public class TestDataVO extends TestDataEntity {
    @ExcelProperty("姓名(编码)")
    private String codeAndName;
}

@Getter
@Setter
public class TestDataDTO extends TestDataEntity {
}
~~~

### 创建Mapper:
~~~Java
package io.github.limuqy.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.limuqy.demo.model.entity.TestDataEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestDataMapper extends BaseMapper<TestDataEntity> {
}
~~~

### 创建Service:
~~~Java
package io.github.limuqy.demo.service;

import io.github.limuqy.demo.model.dto.TestDataDTO;
import io.github.limuqy.demo.model.entity.TestDataEntity;
import io.github.limuqy.demo.model.vo.TestDataVO;
import io.github.limuqy.easyweb.mybitis.base.PageRequest;
import io.github.limuqy.easyweb.mybitis.service.BaseService;
import jakarta.servlet.http.HttpServletResponse;

public interface TestDataService extends BaseService<TestDataEntity> {

    void export(PageRequest data, HttpServletResponse response);

    TestDataVO details(Long id);

    void saveData(TestDataDTO data);
}
~~~

### 创建ServiceImpl:
~~~Java
package io.github.limuqy.demo.service.impl;

import io.github.limuqy.demo.mapper.TestDataMapper;
import io.github.limuqy.demo.model.dto.TestDataDTO;
import io.github.limuqy.demo.model.entity.TestDataEntity;
import io.github.limuqy.demo.model.vo.TestDataVO;
import io.github.limuqy.demo.service.TestDataService;
import io.github.limuqy.easyweb.core.exception.BusinessException;
import io.github.limuqy.easyweb.core.util.BeanUtil;
import io.github.limuqy.easyweb.core.util.JsonUtil;
import io.github.limuqy.easyweb.core.util.ObjectUtil;
import io.github.limuqy.easyweb.excel.write.QueryExport;
import io.github.limuqy.easyweb.mybitis.base.PageRequest;
import io.github.limuqy.easyweb.mybitis.service.impl.BaseServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestDataServiceImpl extends BaseServiceImpl<TestDataMapper, TestDataEntity> implements TestDataService {

    @Override
    public void export(PageRequest data, HttpServletResponse response) {
        /*
        最简形态
        QueryExport.build(data.doSimpleWrapper(this.entityClass))
                .out(response, "测试导出")
                .doExport();
         */
        QueryExport.build(data.doSimpleWrapper(this.entityClass), TestDataVO.class)
                .out(response, "测试导出")
                // 查询数据，默认使用list查询
                .query(this::list)
                // 可选自定义数据映射，默认使用BeanUtil.copyToList
                .map(list -> BeanUtil.copyToList(list, TestDataVO.class))
                // 可选业务逻辑处理
                .apply(list -> {
                    list.forEach(item -> item.setCodeAndName(String.format("%s-%s", item.getCode(), item.getName())));
                    return list;
                })
                .doExport();
    }

    @Override
    public TestDataVO details(Long id) {
        if (ObjectUtil.isNull(id)) {
            throw new BusinessException("Id不能为空！");
        }
        TestDataVO data = BeanUtil.toBean(getById(id), TestDataVO.class);
        data.setCodeAndName(String.format("%s-%s", data.getCode(), data.getName()));
        log.debug("这是查询详情的数据：{}", JsonUtil.toJSONString(data));
        return data;
    }

    @Override
    public void saveData(TestDataDTO data) {
        log.debug("这是保存的数据：{}", JsonUtil.toJSONString(data));
        // 巴拉巴拉巴拉...
        saveOrUpdate(data);
    }
}
~~~

### 创建Controller:
~~~Java
package io.github.limuqy.demo.controller;

import io.github.limuqy.demo.model.dto.TestDataDTO;
import io.github.limuqy.demo.model.vo.TestDataVO;
import io.github.limuqy.demo.service.TestDataService;
import io.github.limuqy.easyweb.mybitis.base.PageRequest;
import io.github.limuqy.easyweb.mybitis.base.PageResult;
import io.github.limuqy.easyweb.mybitis.base.RestResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestDataController {
    private final TestDataService testDataService;

    @PostMapping("/page")
    public RestResponse<PageResult<Collection<TestDataVO>>> pageQuery(@RequestBody PageRequest data) {
        return RestResponse.ok(testDataService.pageQuery(data, TestDataVO.class));
    }

    @PostMapping("/export")
    public void export(@RequestBody PageRequest data, HttpServletResponse response) {
        testDataService.export(data, response);
    }

    @PostMapping("/save")
    public RestResponse<?> saveData(@RequestBody TestDataDTO data) {
        testDataService.saveData(data);
        return RestResponse.ok(data);
    }

    @GetMapping("/details")
    public RestResponse<TestDataVO> details(@RequestParam Long id) {
        return RestResponse.ok(testDataService.details(id));
    }

    @PostMapping("/batch/delete")
    public RestResponse<?> batchDelete(@RequestBody List<Long> ids) {
        testDataService.removeBatchByIds(ids);
        return RestResponse.ok();
    }
}
~~~

### 调用接口:
~~~http request
POST http://localhost:8080/test/page
Content-Type: application/json
~~~
请求参数
~~~json
{
  "page": 1,
  "limit": 10,
  "params": [
    {
      "name": "name",
      "value": "巴拉",
      "op": "like"
    }
  ]
}
~~~
params.op
~~~
eq：等于；ne：不等于；like：模糊查询；in：多值匹配(values)；notIn：不包含(values)；
gt：大于；ge：大于等于；lt：小于；le：小于等于；between：在XXX之间(values[1],values[2])，用于数值或时间；
isNull：为空；isNotNull：不为空；
~~~

响应参数
~~~json
{
  "code": 200,
  "data": {
    "total": 1,
    "table": [
      {
        "id": 2,
        "createBy": "anonymous",
        "createByName": "anonymous",
        "createTime": 1754470948000,
        "updateBy": "anonymous",
        "updateByName": "anonymous",
        "updateTime": 1754470948000,
        "reversion": 1,
        "deleted": 0,
        "code": "test2",
        "name": "巴拉2"
      }
    ]
  }
}
~~~


