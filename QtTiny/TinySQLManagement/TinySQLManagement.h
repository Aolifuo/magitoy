/*
* TinySQLManagement.h
* 数据库管理主窗口
* 
*/

#pragma once

#include "Data.h"
#include <QtWidgets/QMainWindow>
#include <QTreeWidget>

namespace Ui {
    class TinySQLManagementClass;
}

class TinySQLManagement : public QMainWindow
{
    Q_OBJECT

    enum ItemLevel
    {
        CONNECTION, DATABASE, SCHEMA, TABLE
    };

public:
    TinySQLManagement(QWidget *parent = Q_NULLPTR);
    ~TinySQLManagement();

private:
    void initToolBar(); 
    void initConnect();
    void addNavigationNewConnection(QString); //保存连接数据
    void showDataBase(const QPoint&); //在导航栏展示数据库结构
    void showNewQuery(); //新建查询

public slots:
    void showNavigationMenu(const QPoint&);
    void showTable(QTreeWidgetItem*, int); //展示数据表
    void createSchema(SchemaData); //创建模式

private:
    Ui::TinySQLManagementClass *ui; //pimpl

    struct MStorage;
    MStorage *R;
};
