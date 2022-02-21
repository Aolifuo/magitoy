/*
* QueryTab.h
* �½���ѯ
* ������SQL�����в���
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
	void runSql(); //����sql���
private:
	Ui::QueryTab* ui;
	QSqlQueryModel* model;
	std::vector<QString> connections;
};