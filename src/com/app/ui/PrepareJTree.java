package com.app.ui;

import com.app.db.MysqlDbOperation;
import com.app.objects.Column;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alicanb on 04.06.2018.
 */
public class PrepareJTree {
    private static final int JTREE_DEFAULT_WIDTH = 500;
    private static final int ROW_SIZE = 20;
    HashMap<String, ArrayList<Column>> tableColumnsMap = new HashMap<>();
    private int jtreeHeight;
    private ArrayList<String> jtreeNodes;
    private MysqlDbOperation mysqlDbOperation;
    private JTree jtree;
    private DefaultTreeModel model;
    private DefaultMutableTreeNode root;

    public PrepareJTree() {
    }

    public PrepareJTree(String rootNode, MysqlDbOperation mysqlDbOperation, JTree jtree) {
        this.mysqlDbOperation = mysqlDbOperation;
        this.jtree = jtree;
        this.model = (DefaultTreeModel) jtree.getModel();
        this.root = (DefaultMutableTreeNode) model.getRoot();
        this.root.setUserObject(rootNode);
        this.adjustJTree();
        this.prepareJTreeNodes();
    }

    public void setDefaultJTreeSize() {
        jtreeHeight = jtreeNodes.size() * ROW_SIZE;
        jtree.setPreferredSize(new Dimension(JTREE_DEFAULT_WIDTH, this.jtreeHeight));
    }

    public void increaseJTreeSize(int childNodeCount) {
        jtreeHeight = this.jtreeHeight + (childNodeCount * ROW_SIZE);
        jtree.setPreferredSize(new Dimension(JTREE_DEFAULT_WIDTH, this.jtreeHeight));
    }

    public void decreaseJTreeSize(int childNodeCount) {
        jtreeHeight = this.jtreeHeight - (childNodeCount * ROW_SIZE);
        jtree.setPreferredSize(new Dimension(JTREE_DEFAULT_WIDTH, this.jtreeHeight));
    }

    private void prepareJTreeNodes() {
        jtreeNodes = mysqlDbOperation.getDbTableNames();
        jtreeNodes.forEach(childNode -> {
            DefaultMutableTreeNode childRoot = new DefaultMutableTreeNode(childNode);
            this.root.add(childRoot);
            ArrayList<Column> childElements = mysqlDbOperation.getDbTableColumns(childNode);
            this.tableColumnsMap.put(childNode, childElements);
            childElements.forEach(childElement -> childRoot.add(new DefaultMutableTreeNode(childElement.getName() + " (" + childElement.getType() + " )")));
        });
    }

    public ArrayList<Column> getTableColumns(String key) {
        return tableColumnsMap.get(key);
    }

    private void adjustJTree() {
        root.removeAllChildren();
        model.reload(root);
        jtree.setVisible(true);
    }

}

//Not delete!!!! because for dynamic child elements
//    public void addChildNode(String selectedNodeName, TreePath selectedNodePath) {
//        MutableTreeNode node = (MutableTreeNode) selectedNodePath.getLastPathComponent();
//
//        MutableTreeNode newNode = new DefaultMutableTreeNode("green");
//
//        this.model.insertNodeInto(newNode, node, node.getChildCount());
//    }