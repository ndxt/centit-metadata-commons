package com.centit.product.metadata.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpContentType;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.PageQueryResult;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.product.metadata.po.MetaColumn;
import com.centit.product.metadata.po.MetaRelation;
import com.centit.product.metadata.po.MetaTable;
import com.centit.product.metadata.service.MetaDataService;
import com.centit.product.metadata.vo.MetaTableCascade;
import com.centit.support.database.metadata.SimpleTableInfo;
import com.centit.support.database.utils.PageDesc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(value = "数据库元数据查询", tags = "元数据查询")
@RestController
@RequestMapping(value = "query")
public class MetadataQueryController {

    @Autowired
    private MetaDataService metaDataService;

    @ApiOperation(value = "数据库列表")
    @GetMapping(value = "/databases")
    @WrapUpResponseBody
    public List<DatabaseInfo> databases(){
        return metaDataService.listDatabase();
    }

    @ApiOperation(value = "数据库中表分页查询")
    @ApiImplicitParam(name = "databaseCode", value = "数据库代码")
    @GetMapping(value = "/{databaseCode}/tables")
    @WrapUpResponseBody
    public PageQueryResult metaTables(@PathVariable String databaseCode, PageDesc pageDesc, HttpServletRequest request){
        Map<String, Object> searchColumn = BaseController.convertSearchColumn(request);
        searchColumn.put("databaseCode",databaseCode);
        JSONArray list = metaDataService.listMetaTables(searchColumn, pageDesc);
        return PageQueryResult.createJSONArrayResult(list,pageDesc,MetaTable.class);
    }

    @ApiOperation(value = "数据库中的表（JDBC元数据）前段应该不需要访问这个接口")
    @ApiImplicitParam(name = "databaseCode", value = "数据库ID")
    @GetMapping(value = "/{databaseCode}/dbtables")
    public List<SimpleTableInfo> databaseTables(@PathVariable String databaseCode){
        return metaDataService.listRealTables(databaseCode);
    }


    @ApiOperation(value = "查询单个表元数据")
    @ApiImplicitParam(name = "tableId", value = "表ID")
    @GetMapping(value = "/table/{tableId}")
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    public MetaTable getMetaTable(@PathVariable String tableId){
        return metaDataService.getMetaTable(tableId);
    }

    @ApiOperation(value = "查询单个表元数据(包括字段信息和关联表信息)")
    @ApiImplicitParam(name = "tableId", value = "表ID")
    @GetMapping(value = "/table/{tableId}/all")
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    public MetaTable getMetaTableWithRelations(@PathVariable String tableId){
        return metaDataService.getMetaTableWithRelations(tableId);
    }

    @ApiOperation(value = "查询列元数据")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(name = "tableId", value = "表ID")
    })
    @GetMapping(value = "/{tableId}/columns")
    @WrapUpResponseBody
    public PageQueryResult<MetaColumn> listColumns(@PathVariable String tableId, PageDesc pageDesc){
        List<MetaColumn> list = metaDataService.listMetaColumns(tableId, pageDesc);
        return PageQueryResult.createResult(list, pageDesc);
    }

    @ApiOperation(value = "查询单个列元数据")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(name = "tableId", value = "表元数据ID"),
        @ApiImplicitParam(name = "fieldLabelName", value = "列名")
    })
    @GetMapping(value = "/{tableId}/{columnName}")
    @WrapUpResponseBody(contentType = WrapUpContentType.MAP_DICT)
    public MetaColumn getColumn(@PathVariable String tableId, @PathVariable String columnName){
        return metaDataService.getMetaColumn(tableId, columnName);
    }

    @ApiOperation(value = "查询关联关系元数据")
    @GetMapping(value = "/{tableId}/relations")
    @WrapUpResponseBody
    public PageQueryResult<MetaRelation> metaRelation(@PathVariable String tableId, PageDesc pageDesc){
        List<MetaRelation> list = metaDataService.listMetaRelation(tableId, pageDesc);
        return PageQueryResult.createResultMapDict(list, pageDesc);
    }

    @ApiOperation(value = "元数据级联字段，只查询一层")
    @GetMapping(value = "/tablecascade/{tableId}/{tableAlias}")
    @WrapUpResponseBody
    public MetaTableCascade getMetaTableCascade(@PathVariable String tableId, @PathVariable String tableAlias){
        return metaDataService.getMetaTableCascade(tableId, tableAlias);
    }
}
