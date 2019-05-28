package com.centit.product.dbdesign.po;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.product.metadata.po.MetaColumn;
import com.centit.product.metadata.po.MetaTable;
import com.centit.support.database.metadata.SimpleTableInfo;
import com.centit.support.database.metadata.TableInfo;
import com.centit.support.database.metadata.TableReference;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import com.centit.support.database.utils.DBType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * create by scaffold 2016-06-01
 * <p>
 * <p>
 * 未落实表元数据表null
 */
@ApiModel
@Data
@Entity
@Table(name = "F_PENDING_META_TABLE")
public class PendingMetaTable implements
    TableInfo, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 表ID 表单主键
     */
    @ApiModelProperty(value = "表ID", hidden = true)
    @Id
    @Column(name = "TABLE_ID")
    //1.用一张hibernate_sequences表管理主键,需要建hibernate_sequences表
    //@ValueGenerator(strategy = GeneratorType.SEQUENCE, value = "seq_pendingtableid")
    @ValueGenerator(strategy = GeneratorType.UUID22)
     //2.用序列
//    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="seqgen")
//    @SequenceGenerator(sequenceName="SEQ_PENDINGTABLEID",name="seqgen",allocationSize=1,initialValue=1)
    private String tableId;

    /**
     * 所属数据库ID null
     */
    @ApiModelProperty(value = "数据库ID")
    @Column(name = "DATABASE_CODE")
    private String databaseCode;
    /**
     * 表代码 null
     */
    @ApiModelProperty(value = "表代码", required = true)
    @Column(name = "TABLE_NAME")
    @NotBlank(message = "字段不能为空")
    @Length(max = 64, message = "字段长度不能大于{max}")
    private String tableName;
    /**
     * 表名称 null
     */
    @ApiModelProperty(value = "表中文名", required = true)
    @Column(name = "TABLE_LABEL_NAME")
    @NotBlank(message = "字段不能为空")
    @Length(max = 100, message = "字段长度不能大于{max}")
    private String tableLabelName;

    /**
     * 描述 null
     */
    @ApiModelProperty(value = "表描述")
    @Column(name = "TABLE_COMMENT")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String tableComment;

    @ApiModelProperty(value = "表状态（待发布W、已发布S）", required = true)
    @Column(name = "TABLE_STATE")
    @Length(message = "字段长度不能大于{max}")
    private String tableState;

    /**
     * 与流程中业务关联关系
     * 0: 不关联工作流 1：和流程业务关联 2： 和流程过程关联
     * 如果关联会添加外键与工作流表关联
     * 这个字段要有，同时检查 字段中是否有  (1,2)FLOW_INST_ID 和 (2)NODE_INST_ID
     */
    @ApiModelProperty(value = "与流程中业务关联关系(0: 不关联工作流 1：和流程业务关联 2： 和流程过程关联)", required = true)
    @Column(name = "WORKFLOW_OPT_TYPE")
    @Length(max = 1, message = "字段长度不能大于{max}")
    private String workFlowOptType;

    //Y/N 更新时是否校验时间戳 添加 Last_modify_time datetime
    @ApiModelProperty(value = "更新时是否校验时间戳")
    @Column(name = "UPDATE_CHECK_TIMESTAMP")
    @Length(max = 1, message = "字段长度不能大于{max}")
    private String updateCheckTimeStamp;

    /**
     * 更改时间 null
     */
    @Column(name = "LAST_MODIFY_DATE")
    @ApiModelProperty(value = "更改时间", hidden = true)
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE, condition = GeneratorCondition.ALWAYS, value = "today()")
    private Date lastModifyDate;
    /**
     * 更改人员 null
     */
    @Column(name = "RECORDER")
    @Length(max = 64, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName = "recorderName", value = "userCode")
    private String recorder;

    @OneToMany(mappedBy="mdTable",orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "TABLE_ID", referencedColumnName = "TABLE_ID")
    private List<PendingMetaColumn> mdColumns;

    @Transient
    private DBType databaseType;

    public void setDatabaseType(DBType databaseType) {
        this.databaseType = databaseType;
        if (this.mdColumns != null) {
            for (PendingMetaColumn col : this.mdColumns) {
                col.setDatabaseType(databaseType);
            }
        }
    }
    // Constructors

    /**
     * default constructor
     */
    public PendingMetaTable() {
    }

    /**
     * minimal constructor
     *
     * @param workFlowOptType
     * @param updateCheckTimeStamp
     */
    public PendingMetaTable(
        String tableId
        , String tableName, String tableLabelName, String tableState, String isInWorkflow, String workFlowOptType, String updateCheckTimeStamp) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.tableLabelName = tableLabelName;
        this.tableState = tableState;
        this.workFlowOptType = workFlowOptType;
        this.updateCheckTimeStamp = updateCheckTimeStamp;
    }


    /**
     * full constructor
     *
     * @param updateCheckTimeStamp
     * @param workFlowOptType
     */
    public PendingMetaTable(
            String tableId
        , String databaseCode, String tableName, String tableLabelName, String tableType, String tableState, String tableComment, String isInWorkflow, Date lastModifyDate, String recorder, String updateCheckTimeStamp, String workFlowOptType) {


        this.tableId = tableId;

        this.setDatabaseCode(databaseCode);
        this.tableName = tableName;
        this.tableLabelName = tableLabelName;
        this.tableState = tableState;
        this.tableComment = tableComment;
        this.workFlowOptType = workFlowOptType;
        this.updateCheckTimeStamp = updateCheckTimeStamp;
        this.lastModifyDate = lastModifyDate;
        this.recorder = recorder;
    }


    public void addMdColumn(PendingMetaColumn mdColumn) {
        if (mdColumn == null)
            return;
        mdColumn.setTableId(this.tableId);
        if(this.mdColumns ==null){
            this.mdColumns = new ArrayList<>(20);
        }
        this.mdColumns.add(mdColumn);
    }


    public PendingMetaTable copy(PendingMetaTable other) {
        this.setMdColumns(other.getMdColumns());
        this.setTableId(other.getTableId());
        this.setDatabaseCode(other.getDatabaseCode());
        this.tableName = other.getTableName();
        this.tableLabelName = other.getTableLabelName();
        this.tableState = other.getTableState();
        this.tableComment = other.getTableComment();
        this.workFlowOptType = other.getWorkFlowOptType();
        this.updateCheckTimeStamp = other.getUpdateCheckTimeStamp();
        this.lastModifyDate = other.getLastModifyDate();
        this.recorder = other.getRecorder();
        return this;
    }

    public PendingMetaTable copyNotNullProperty(PendingMetaTable other) {

        if (other.getTableId() != null)
            this.setTableId(other.getTableId());
        if (other.getMdColumns() != null)
            this.setMdColumns(other.getMdColumns());
        if (other.getDatabaseCode() != null)
            this.databaseCode = other.getDatabaseCode();
        if (other.getTableName() != null)
            this.tableName = other.getTableName();
        if (other.getTableLabelName() != null)
            this.tableLabelName = other.getTableLabelName();
        if (other.getTableState() != null)
            this.tableState = other.getTableState();
        if (other.getTableComment() != null)
            this.tableComment = other.getTableComment();
        if (other.getWorkFlowOptType() != null)
            this.workFlowOptType = other.getWorkFlowOptType();
        if (other.getUpdateCheckTimeStamp() != null)
            this.updateCheckTimeStamp = other.getUpdateCheckTimeStamp();
        if (other.getLastModifyDate() != null)
            this.lastModifyDate = other.getLastModifyDate();
        if (other.getRecorder() != null)
            this.recorder = other.getRecorder();

        return this;
    }

    public PendingMetaTable clearProperties() {
        this.mdColumns = null;
        this.databaseCode = null;
        this.tableName = null;
        this.tableLabelName = null;
        this.tableState = null;
        this.tableComment = null;
        this.workFlowOptType = null;
        this.updateCheckTimeStamp = null;
        this.lastModifyDate = null;
        this.recorder = null;
        return this;
    }

    @Override
    public String getPkName() {
        return "PK_" + this.tableName;
    }

    @Override
    public String getSchema() {
        return null;
    }

    /**
     * @return 默认排序语句
     */
    @Override
    public String getOrderBy() {
        return null;
    }

    @Override
    public PendingMetaColumn findFieldByName(String name) {
        if (mdColumns == null)
            return null;
        for (PendingMetaColumn c : mdColumns) {
            if (c.getPropertyName().equals(name))
                return c;
        }
        return null;
    }

    @Override
    public PendingMetaColumn findFieldByColumn(String name) {
        if (mdColumns == null)
            return null;
        for (PendingMetaColumn c : mdColumns) {
            if (c.getColumnName().equals(name))
                return c;
        }
        return null;
    }

    @Override
    public boolean isParmaryKey(String colname) {
        if (mdColumns == null)
            return false;
        for (PendingMetaColumn c : mdColumns) {
            if (c.getColumnName().equals(colname)) {
                return c.isPrimaryKey();
            }
        }
        return false;
    }

    @Override
    public List<PendingMetaColumn> getColumns() {
        return mdColumns;
    }

    @Override
    public List<String> getPkColumns() {
        if (mdColumns == null)
            return null;

        List<String> pks = new ArrayList<>();
        for (PendingMetaColumn c : mdColumns) {
            if (c.isPrimaryKey()) {
                pks.add(c.getColumnName());
            }
        }
        return pks;
    }

    @Override
    public List<? extends TableReference> getReferences() {
        return null;
    }

    public MetaTable mapToMetaTable(){
        MetaTable mt = new MetaTable();
        mt.setTableId(this.getTableId());
        mt.setDatabaseCode(this.getDatabaseCode());
        mt.setTableName(this.getTableName());
        mt.setTableLabelName(this.getTableLabelName());
        mt.setTableComment(this.getTableComment());
        mt.setRecordDate(this.getLastModifyDate());
        mt.setWorkFlowOptType(this.getWorkFlowOptType());
        mt.setRecorder(this.getRecorder());
        List<MetaColumn> columns = new ArrayList<>();
        if (this.getColumns() != null && this.getColumns().size() > 0) {
            for (PendingMetaColumn pc : this.getColumns()) {
                columns.add(pc.mapToMetaColumn());
            }
        }
        mt.setMdColumns(columns);
        return mt;
    }

    //将数据库表同步到元数据表
    public PendingMetaTable convertFromPdmTable(SimpleTableInfo tableInfo){

        this.tableName = tableInfo.getTableName();
        if(StringUtils.isNotBlank(tableInfo.getTableLabelName())) {
            this.tableLabelName = tableInfo.getTableLabelName();
        }
        if(StringUtils.isNotBlank(tableInfo.getTableComment())){
            this.tableComment = tableInfo.getTableComment();
        }
        this.tableState = StringUtils.isNotBlank(this.tableState) ? this.tableState : "W";
        this.workFlowOptType = StringUtils.isNotBlank(this.workFlowOptType) ? this.workFlowOptType : "0";
        return this;
    }
}
