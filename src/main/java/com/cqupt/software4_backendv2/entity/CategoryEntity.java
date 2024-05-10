package com.cqupt.software4_backendv2.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// TODO 公共模块新增类

@TableName("category")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryEntity {
    @TableId
    private String id;
    private Integer catLevel;
    private String label;
    private String parentId;
    private Integer isLeafs;
    private Integer isDelete;
    private String uid;
    private String status;
    private String username;
    private String isUpload;
    private String isFilter;
    private String icdCode;
    // 新增可共享用户列表
    private String uidList;
    @TableField(exist = false)
    private List<CategoryEntity> children;

    @TableField(exist = false)
    private int tableNum0;
    @TableField(exist = false)
    private int tableNum1;
    @TableField(exist = false)
    private int tableNum2;

    // 新增可共享用户列表
    public CategoryEntity(String id, int catLevel, String label, String parentId, int isLeafs, int isDelete, String uid, String status, String username,String isUpload,String isFilter,String icdCode,String uidList) {
        this.id = id;
        this.catLevel = catLevel;
        this.label = label;
        this.parentId = parentId;
        this.isLeafs = isLeafs;
        this.isDelete = isDelete;
        this.uid = uid;
        this.status = status;
        this.username = username;
        this.isUpload = isUpload;
        this.isFilter = isFilter;
        this.icdCode = icdCode;
        this.uidList = uidList;
        this.children = new ArrayList<>();
    }

    public void addChild(CategoryEntity child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }
    // 新增可共享用户列表
    // 递归复制符合条件的节点
    public static CategoryEntity copyPrivareTreeStructure(CategoryEntity node,String uid) {
        if (node.isLeafs == 0 || (node.isLeafs == 1 && "0".equals(node.status) && uid.equals(node.uid))) {
            CategoryEntity newNode = new CategoryEntity(node.id, node.catLevel, node.label, node.parentId, node.isLeafs, node.isDelete, node.uid, "0", node.username,node.isUpload,node.isFilter,node.icdCode,node.uidList);
            if (node.children != null) {
                for (CategoryEntity child : node.children) {
                    CategoryEntity copiedChild = copyPrivareTreeStructure(child,uid);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }

    public static CategoryEntity copyShareTreeStructure(CategoryEntity node,String uid) {
        if(node.uidList == null){
            node.uidList = "";
        }
        if (node.isLeafs == 0 || (node.isLeafs == 1 && "1".equals(node.status) && (node.uidList.contains(uid)||uid.equals(node.uid)) )) {
            CategoryEntity newNode = new CategoryEntity(node.id, node.catLevel, node.label, node.parentId, node.isLeafs, node.isDelete, node.uid, "1", node.username,node.isUpload,node.isFilter,node.icdCode,node.uidList);
            if (node.children != null) {
                for (CategoryEntity child : node.children) {
                    CategoryEntity copiedChild = copyShareTreeStructure(child,uid);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }

    public static CategoryEntity copyCommonTreeStructure(CategoryEntity node) {
        if (node.isLeafs == 0 || (node.isLeafs == 1 && "2".equals(node.status))) {
            CategoryEntity newNode = new CategoryEntity(node.id, node.catLevel, node.label, node.parentId, node.isLeafs, node.isDelete, node.uid, "2", node.username,node.isUpload,node.isFilter,node.icdCode,node.uidList);
            if (node.children != null) {
                for (CategoryEntity child : node.children) {
                    CategoryEntity copiedChild = copyCommonTreeStructure(child);
                    if (copiedChild != null) {
                        newNode.addChild(copiedChild);
                    }
                }
            }
            return newNode;
        } else {
            return null;
        }
    }
}
