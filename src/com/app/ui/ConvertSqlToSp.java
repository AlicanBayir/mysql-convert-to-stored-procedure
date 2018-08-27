package com.app.ui;

import com.app.db.MysqlDbOperation;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by alicanb on 04.06.2018.
 */
public class ConvertSqlToSp {
    private  MysqlDbOperation mysqlDbOperation;

    private PrepareJTree prepareJTree;
    private JPanel convertSqlToSpPanel;
    private JPanel topMenuPanel;
    private JLabel dbNameLabel;
    private JTextField dbNameField;
    private JLabel urlLabel;
    private JTextField urlField;
    private JTextField userNameField;
    private JTextField passwordField;
    private JLabel passwordLabel;
    private JButton connectionButton;
    private JTree jtree;
    private JScrollPane leftMenuPanel;
    private JScrollPane spResultPanel;
    private JTextArea spResultTextArea;
    private JButton spGenerateButton;
    private JCheckBox readCheckBox;
    private JCheckBox createCheckBox;
    private JCheckBox updateCheckBox;
    private JCheckBox deleteCheckBox;
    private JCheckBox jsTypeCheckBox;
    private JLabel userNameLabel;
    private JButton clearTextFieldButton;

    public ConvertSqlToSp() {
        connectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectionButton.setText("Connecting...");
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (validationDbConnectionInfo()) {
                            mysqlDbOperation = new MysqlDbOperation(dbNameField.getText(), urlField.getText(), userNameField.getText(), passwordField.getText());
                            if (validateDbConnection(mysqlDbOperation)) {
                                prepareJTree = new PrepareJTree(dbNameField.getText(), mysqlDbOperation, jtree);
                                prepareJTree.setDefaultJTreeSize();
                            }
                        }
                        connectionButton.setText("Connect");
                    }
                });
            }

            private boolean validationDbConnectionInfo() {
                if (dbNameField.getText().isEmpty() || urlField.getText().isEmpty() || userNameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(new JFrame("ConvertSqlToSp"),
                            "!!! DB, Url, User Name and Password fields required.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                return true;
            }

            private boolean validateDbConnection(MysqlDbOperation mysqlDbOperation) {
                if (mysqlDbOperation.getConnection() == null) {
                    JOptionPane.showMessageDialog(new JFrame("ConvertSqlToSp"),
                            "!!! An error occurred while db connection is provided.\n Please, check connection information.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                return true;
            }
        });
        spGenerateButton.addActionListener(new ActionListener() {
            ArrayList<String> selectedOperations = new ArrayList<>();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkSelectedNodeName()) {
                    setSelectedOperations();
                    if (!selectedOperations.isEmpty()) {
                        String spOutput = new PrepareSpOutput(prepareJTree, mysqlDbOperation).prepareAndGetSpOutput(jtree.getSelectionPath().getLastPathComponent().toString(), selectedOperations);
                        spResultTextArea.setText(spOutput);
                        selectedOperations = new ArrayList<>();
                    }
                }
            }

            private void setSelectedOperations() {
                if (readCheckBox.isSelected()) {
                    selectedOperations.add(readCheckBox.getText());
                }
                if (createCheckBox.isSelected()) {
                    selectedOperations.add(createCheckBox.getText());
                }
                if (updateCheckBox.isSelected()) {
                    selectedOperations.add(updateCheckBox.getText());
                }
                if (deleteCheckBox.isSelected()) {
                    selectedOperations.add(deleteCheckBox.getText());
                }
                if (jsTypeCheckBox.isSelected()) {
                    selectedOperations.add(jsTypeCheckBox.getText());
                }
                if (selectedOperations.isEmpty()) {
                    JOptionPane.showMessageDialog(new JFrame("ConvertSqlToSp"),
                            "!!! Please, select only one operation to generate",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        clearTextFieldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spResultTextArea.setText("");
            }
        });
        jtree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                if (checkSelectedNodeName()) {
                    spGenerateButton.setEnabled(true);
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) jtree.getSelectionPath().getLastPathComponent();
                    prepareJTree.increaseJTreeSize(defaultMutableTreeNode.getChildCount());
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                if (checkSelectedNodeName()) {
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) jtree.getSelectionPath().getLastPathComponent();
                    prepareJTree.decreaseJTreeSize(defaultMutableTreeNode.getChildCount());
                }
                if (!checkSelectedNodeName()) {
                    spGenerateButton.setEnabled(false);
                }
            }
        });
    }

    public JPanel getConvertSqlToSpPanel() {
        return convertSqlToSpPanel;
    }

    private boolean checkSelectedNodeName() {
        String selectedNodeName = jtree.getSelectionPath().getLastPathComponent().toString();
        return !selectedNodeName.equals(dbNameField.getText());
    }
}
