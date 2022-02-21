/*
* TinySQLManagement.h
* ���ݿ����������
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
    void addNavigationNewConnection(QString); //������������
    void showDataBase(const QPoint&); //�ڵ�����չʾ���ݿ�ṹ
    void showNewQuery(); //�½���ѯ

public slots:
    void showNavigationMenu(const QPoint&);
    void showTable(QTreeWidgetItem*, int); //չʾ���ݱ�
    void createSchema(SchemaData); //����ģʽ

private:
    Ui::TinySQLManagementClass *ui; //pimpl

    struct MStorage;
    MStorage *R;
};
