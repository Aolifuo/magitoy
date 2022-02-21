/*
* QueryTab.h
* 新建查询
* 可以用SQL语句进行操作
*/

#pragma once
#include <QWidget>
#include <vector>
#include <QSqlQueryModel>

namespace Ui {
	class QueryTab;
}

class QueryTab : public QWidget
{
	Q_OBJECT
public:
	QueryTab(QWidget* parent = nullptr);
	~QueryTab();

	void setConnections(const std::vector<QString>&);
	void runSql(); //运行sql语句
private:
	Ui::QueryTab* ui;
	QSqlQueryModel* model;
	std::vector<QString> connections;
};