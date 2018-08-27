package com.app.ui;

import com.app.db.MysqlDbOperation;
import com.app.objects.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alicanb on 07.06.2018.
 */
public class PrepareSpOutput {
    private String spDropString = "DROP PROCEDURE IF EXISTS sp_";
    private String spOutputString = "DELIMITER $$ \n" +
            "CREATE PROCEDURE sp_";
    private PrepareJTree prepareJTree;
    private MysqlDbOperation mysqlDbOperation;
    private ArrayList<String> primaryKeys;

    public PrepareSpOutput() {
    }

    public PrepareSpOutput(PrepareJTree prepareJTree, MysqlDbOperation mysqlDbOperation) {
        this.prepareJTree = prepareJTree;
        this.mysqlDbOperation = mysqlDbOperation;
    }

    public String prepareAndGetSpOutput(String selectedTableName, ArrayList<String> selectedOperations) {
        ArrayList<Column> selectedTableColumns = this.prepareJTree.getTableColumns(selectedTableName);
        this.primaryKeys = mysqlDbOperation.getDbTablePrimary(selectedTableName);
        String readSpString = "";
        String createSpString = "";
        String updateSpString = "";
        String deleteSpString = "";
        String typeJsFileString = "";
        if (selectedOperations.contains("Read")) {
            readSpString = "## ******************** READ ******************** \n" + spDropString + selectedTableName + "_read; \n" + this.spOutputString + selectedTableName +
                    getReadSpOutPut(selectedTableName, selectedTableColumns)
                    + "\n" + "END$$\n" +
                    "DELIMITER ;\n\n";
        }
        if (selectedOperations.contains("Create")) {
            createSpString = "## ******************** CREATE ******************** \n" + spDropString + selectedTableName + "_create; \n" + this.spOutputString + selectedTableName +
                    getCreateSpOutPut(selectedTableName, selectedTableColumns)
                    + "\n" + "END$$\n" +
                    "DELIMITER ;\n\n";
        }
        if (selectedOperations.contains("Update")) {
            updateSpString = "## ******************** UPDATE ******************* \n" + spDropString + selectedTableName + "_update; \n" + this.spOutputString + selectedTableName +
                    getUpdateSpOutPut(selectedTableName, selectedTableColumns)
                    + "\n" + "END$$\n" +
                    "DELIMITER ;\n\n";
        }
        if (selectedOperations.contains("Delete")) {
            deleteSpString = "## ******************** DELETE ******************* \n" + spDropString + selectedTableName + "_delete; \n" + this.spOutputString + selectedTableName +
                    getDeleteSpOutPut(selectedTableName, selectedTableColumns)
                    + "\n" + "END$$\n" +
                    "DELIMITER ;\n\n";
        }
        if (selectedOperations.contains("Type JS File")) {
            typeJsFileString = "## ******************** Type JS File ******************* \n" + getTypeJsOutput(selectedTableName, selectedTableColumns);
        }
        return readSpString + createSpString + updateSpString + deleteSpString + typeJsFileString;
    }

    private String getReadSpOutPut(String selectedTableName, ArrayList<Column> selectedTableColumns) {
        boolean isIncludeStatus = isIncludeStatus(selectedTableColumns);
        String readSpOutput = "_read (" + getInContentString(selectedTableColumns) + ")\n";
        if (isIncludeStatus) {
            readSpOutput += "BEGIN " +
                    "SELECT * FROM " + selectedTableName + " WHERE status != 'deleted' AND " + getWhereConditionString() + ";";
        } else {
            readSpOutput += "BEGIN " +
                    "SELECT * FROM " + selectedTableName + " WHERE " + getWhereConditionString() + ";";
        }
        return readSpOutput;
    }

    private String getCreateSpOutPut(String selectedTableName, ArrayList<Column> selectedTableColumns) {
        List<Column> clonedList = getClonedColumnList(selectedTableColumns);
        String createSpOutput;
        boolean isAutoIncrementId;
        int startIndex;
        createSpOutput = "_create (OUT id" + " " + clonedList.get(0).getType() + ",\n";
        if (clonedList.get(0).getType().toLowerCase().contains("int")) {
            isAutoIncrementId = true;
            startIndex = 1;
        } else {
            isAutoIncrementId = false;
            startIndex = 0;
        }
        for (int index = startIndex; index < clonedList.size(); ++index) {
            if (clonedList.get(index).getType().contains("enum")) {
                clonedList.get(index).setType("varchar(255)");
            }
            createSpOutput += "IN _" + clonedList.get(index).getName() + " " + clonedList.get(index).getType();
            if (index == clonedList.size() - 1) {
                createSpOutput += "\n";
            } else {
                createSpOutput += ",\n";
            }
        }
        createSpOutput += ")\n BEGIN \n INSERT INTO " + selectedTableName + "\n (";
        for (int index = startIndex; index < clonedList.size(); ++index) {
            createSpOutput += clonedList.get(index).getName();
            if (index == clonedList.size() - 1) {
                createSpOutput += "\n";
            } else {
                createSpOutput += ",\n";
            }
        }
        createSpOutput += ") \n VALUES \n (";
        for (int index = startIndex; index < clonedList.size(); ++index) {
            createSpOutput += "_" + clonedList.get(index).getName();
            if (index == clonedList.size() - 1) {
                createSpOutput += "\n";
            } else {
                createSpOutput += ",\n";
            }
        }
        createSpOutput += "); \n SET id = ";
        if (isAutoIncrementId) {
            createSpOutput += "LAST_INSERT_ID(); \n";
        } else {
            createSpOutput += "_" + clonedList.get(0).getName() + ";\n";
        }
        return createSpOutput;
    }

    private String getUpdateSpOutPut(String selectedTableName, ArrayList<Column> selectedTableColumns) {
        List<Column> clonedList = getClonedColumnList(selectedTableColumns);
        String updateSpOutput = "_update (";
        for (int index = 0; index < clonedList.size(); ++index) {
            if (clonedList.get(index).getType().contains("enum")) {
                clonedList.get(index).setType("varchar(255)");
            }
            updateSpOutput += "IN _" + clonedList.get(index).getName() + " " + clonedList.get(index).getType();
            if (!this.primaryKeys.contains(clonedList.get(index).getName())) {
                updateSpOutput += " ,\n IN _" + clonedList.get(index).getName() + "_SET " + "boolean";
            }
            if (index == clonedList.size() - 1) {
                updateSpOutput += "\n";
            } else {
                updateSpOutput += ",\n";
            }
        }
        updateSpOutput += ")\n BEGIN \n UPDATE " + selectedTableName + "\n SET ";
        for (int index = this.primaryKeys.size(); index < clonedList.size(); ++index) {
            updateSpOutput += clonedList.get(index).getName() + " = " + "CASE _" + clonedList.get(index).getName() +
                    "_SET WHEN FALSE THEN " + clonedList.get(index).getName()
                    + " ELSE " + "_" + clonedList.get(index).getName() + " END";
            if (index == clonedList.size() - 1) {
                updateSpOutput += "\n";
            } else {
                updateSpOutput += ",\n";
            }
        }
        updateSpOutput += "WHERE " + getWhereConditionString() + ";\n";
        return updateSpOutput;
    }

    private String getDeleteSpOutPut(String selectedTableName, ArrayList<Column> selectedTableColumns) {
        boolean includeStatus = isIncludeStatus(selectedTableColumns);
        String deleteSpOutput = "_delete (" + getInContentString(selectedTableColumns) + ")\n";
        if (includeStatus) {
            deleteSpOutput += "BEGIN\n" +
                    "UPDATE " + selectedTableName + " SET status = 'deleted'" + " WHERE " + getWhereConditionString() + ";";
        } else {
            deleteSpOutput += "BEGIN\n" +
                    "DELETE FROM " + selectedTableName + " WHERE " + getWhereConditionString() + ";";
        }
        return deleteSpOutput;
    }

    private String getTypeJsOutput(String selectedTableName, ArrayList<Column> selectedTableColumns) {
        String className = getClassNameForJsOutput(selectedTableName);
        String typeJsFileString = "class " + className + "{\n" +
                "constructor() {\n" +
                "this.tableName = " + "\"" + selectedTableName + "\";\n" +
                "this.primaryKey = {\n " +
                "fields : " + getJsOutputFields() + ",\n" +
                "autoIncrement : " + (selectedTableColumns.get(0).getType().toLowerCase().contains("int") ? "true\n" : "false\n") + "};\n" +
                "this.fields = [\n";
        for (int index = 0; index < selectedTableColumns.size(); ++index) {
            typeJsFileString += "\"" + selectedTableColumns.get(index).getName() + "\"";
            if (index == selectedTableColumns.size() - 1) {
                typeJsFileString += "\n";
            } else {
                typeJsFileString += ",\n";
            }
        }
        typeJsFileString += "];\n}\n}\n module.exports = " + getClassNameForJsOutput(selectedTableName) + ";";
        return typeJsFileString;
    }

    private String getClassNameForJsOutput(String selectedTableName) {
        String[] classNameArray = selectedTableName.split("_");
        String className = "";
        for (int i = 0; i < classNameArray.length; i++) {
            className += classNameArray[i].substring(0, 1).toUpperCase() + classNameArray[i].substring(1);
        }
        return className;
    }

    private String getJsOutputFields() {
        String fields = "[";
        for (int index = 0; index < this.primaryKeys.size(); index++) {
            fields += "\"" + primaryKeys.get(index) + "\"";
            if (index != primaryKeys.size() - 1) {
                fields += ", ";
            }
        }
        fields += "]";
        return fields;
    }

    private String getWhereConditionString() {
        String whereConditionString = "";
        for (int index = 0; index < this.primaryKeys.size(); index++) {
            whereConditionString += primaryKeys.get(index) + "= _" + primaryKeys.get(index);
            if (index != primaryKeys.size() - 1) {
                whereConditionString += " AND ";
            }
        }
        return whereConditionString;
    }

    private String getInContentString(ArrayList<Column> selectedTableColumns) {
        String readInContentString = "";
        for (int index = 0; index < this.primaryKeys.size(); index++) {
            readInContentString += "IN _" + primaryKeys.get(index) + " " + findPrimaryKeyType(primaryKeys.get(index), selectedTableColumns);
            if (index != primaryKeys.size() - 1) {
                readInContentString += ", ";
            }
        }
        return readInContentString;
    }

    private String findPrimaryKeyType(String primaryKey, ArrayList<Column> selectedTableColumns) {
        final String[] type = {""};
        selectedTableColumns.forEach(item -> {
            if (item.getName().equals(primaryKey)) {
                type[0] = item.getType();
            }
        });
        return type[0];
    }

    private boolean isIncludeStatus(ArrayList<Column> selectedTableColumns) {
        final boolean[] isIncludeStatus = {false};
        selectedTableColumns.forEach(item -> {
            if (item.getName().contains("status") && item.getType().contains("deleted")) {
                isIncludeStatus[0] = true;
            }
        });
        return isIncludeStatus[0];
    }

    private List<Column> getClonedColumnList(ArrayList<Column> selectedTableColumns) {
        List<Column> clonedList = new ArrayList<>();
        for (Column item : selectedTableColumns) {
            try {
                clonedList.add(item.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return clonedList;
    }

}
