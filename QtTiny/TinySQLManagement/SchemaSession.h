/*
* SchemaSession.h
* ģʽ�Ự����
* ��ȡģʽ��Ϣ
*/

#pragma once

#include "Data.h"
#include <QWidget>
#include <QTreeWidget>

namespace Ui {
	class SchemaSession;
}

class SchemaSession: public QWidget 
{
	Q_OBJECT
public:
	SchemaSession(QWidget* parent = nullptr);
	~SchemaSession();
	
	void setConnectionName(QString);
	void setDataBaseItem(QTreeWidgetItem*);

signals:
	void sendSchemaData(SchemaData);
private:
	Ui::SchemaSession* ui;
	SchemaData data;
};