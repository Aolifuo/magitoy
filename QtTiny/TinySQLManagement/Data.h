#pragma once
#include <QString>
#include <QTreeWidget>

struct Data
{
    QString connectionName;
    QString hostName;
    QString dataBaseName;
    QString userName;
    QString passward;
};

struct SchemaData
{
    QTreeWidgetItem* item = nullptr;
    QString connectionName;
    QString name;
    QString owner;
};