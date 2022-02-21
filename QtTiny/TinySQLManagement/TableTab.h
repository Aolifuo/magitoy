/*
* TableTab.h
* 数据库表操作
* 增删改查排序筛选和事务功能
*/

#pragma once
#include <QWidget>
#include <QToolBar>
#include <QSqlTableModel>

namespace Ui {
	class TableTab;
}

class TableTab : public QWidget
{
	Q_OBJECT

public:
	TableTab(QWidget* parent = nullptr);
	~TableTab();

	void initToolBar();
	void initConnect();
	void setModel(QSqlTableModel*);
private:
	Ui::TableTab* ui;
	QToolBar* top;
	QToolBar* bottom;
	QSqlTableModel* model;
};
