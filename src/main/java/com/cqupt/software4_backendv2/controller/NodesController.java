package com.cqupt.software4_backendv2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.software4_backendv2.common.Result;
import com.cqupt.software4_backendv2.entity.Node;
import com.cqupt.software4_backendv2.entity.Nodes;
import com.cqupt.software4_backendv2.entity.Relationships;
import com.cqupt.software4_backendv2.entity.User;
import com.cqupt.software4_backendv2.service.NodesService;
import com.fasterxml.jackson.databind.JsonNode;
import jdk.nashorn.internal.ir.ObjectNode;
import org.python.antlr.op.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/nodes")
public class NodesController {

    @Autowired
    private NodesService nodesService;


    private static void addMapping(Map<Integer, List<Integer>> pidCidMap, int pid, int cid) {
        if (!pidCidMap.containsKey(pid)) {
            pidCidMap.put(pid, new ArrayList<>());
        }
        pidCidMap.get(pid).add(cid);
    }

    private void setChildren(Node node, Map<Integer, Node> nodeMap, Map<Integer, List<Integer>> pidCidMap) {
        Integer nodeId = node.getId();
        if (pidCidMap.containsKey(nodeId)) {
            List<Node> children = new ArrayList<>();
            for (Integer childId : pidCidMap.get(nodeId)) {
                Node childNode = nodeMap.get(childId);
                if (childNode != null) {
                    children.add(childNode);
                    setChildren(childNode, nodeMap, pidCidMap); // 递归设置子节点的子节点
                }
            }
            node.setChildren(children);
        }
    }

    @GetMapping("/all")
    public Result getAllNodes(){
        //获得所有节点
        List<Nodes> nodes = nodesService.getAllNodes();
        Map<Integer, Node> nodeMap = new HashMap<>();
        //将字符串转换为json对象
        for (int i = 0; i < nodes.size(); i++) {
            JsonNode jsonNode = (nodes.get(i).parseJsonString(nodes.get(i).getData()));
            Node node = new Node();
            node.setId(jsonNode.get("id").asInt());
            node.setPath(jsonNode.get("path").asText());
            node.setLabel(jsonNode.get("label").asText());
            node.setLeafs(jsonNode.get("isLeafs").asBoolean());
            node.setCommon(jsonNode.get("isCommon").asBoolean());
            if(node.isLeafs() == false){
                node.setChildren(new ArrayList<>());
            }
            nodeMap.put(node.getId(), node);
        }
        //拼装树形结构
        List<Relationships> relationships = nodesService.getRelationships();
        Map<Integer, List<Integer>> pidCidMap = new HashMap<>();
        for (Relationships relationship : relationships) {
            Integer pid = relationship.getParentId();
            Integer cid = relationship.getChildId();
            addMapping(pidCidMap, pid, cid);
        }

        // 根据映射关系更新节点的 children 列表
        for (Map.Entry<Integer, List<Integer>> entry : pidCidMap.entrySet()) {
            Integer parentId = entry.getKey();
            List<Integer> childIds = entry.getValue();
            Node parentNode = nodeMap.get(parentId);
            //System.out.println(parentId + " -> " + childIds);
            //System.out.println(parentNode);
            if (parentNode != null) {
                List<Node> children = new ArrayList<>();
                for (Integer childId : childIds) {
                    Node childNode = nodeMap.get(childId);
                    if (childNode != null) {
                        children.add(childNode);
                    }
                }
                parentNode.setChildren(children);
            }
            //System.out.println(parentNode);
        }


        // 遍历 nodeMap，找出所有的根节点
        List<Node> rootNodes = new ArrayList<>();
        for (Node node : nodeMap.values()) {
            boolean isRoot = true;
            for (Integer parentId : pidCidMap.keySet()) {
                if (pidCidMap.get(parentId).contains(node.getId())) {
                    isRoot = false;
                    break;
                }
            }
            if (isRoot) {
                rootNodes.add(node);
            }
        }

// 为每个节点设置其子节点列表
        for (Node rootNode : rootNodes) {
            setChildren(rootNode, nodeMap, pidCidMap);
        }

// 返回根节点列表
        return Result.success(rootNodes);

    }

}
