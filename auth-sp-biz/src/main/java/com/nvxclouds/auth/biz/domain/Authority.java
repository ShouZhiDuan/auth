package com.nvxclouds.auth.biz.domain;

import com.nvxclouds.common.base.domain.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "authority")
public class Authority extends BaseDO implements Serializable {

    @Column(name = "model_name")
    private String modelName;
    @Column(name = "icon")
    private String icon;
    @Column(name = "function_name")
    private String functionName;
    @Column(name = "type")
    private Integer type;
    @Column(name = "parent_ID")
    private Long parentId;
    @Column(name = "introduction")
    private String introduction;
    @Column(name = "tree_sort")
    private Long treeSort;
    @Column(name = "tree_level")
    private Integer treeLevel;
    @Column(name = "tree_leaf")
    private Integer treeLeaf;
    @Column(name = "tree_root")
    private Long treeRoot;
    @Column(name = "tree_names")
    private String treeNames;
    @Column(name = "tree_nodes")
    private String treeNodes;
    @Column(name = "permission")
    private String permission;

}
