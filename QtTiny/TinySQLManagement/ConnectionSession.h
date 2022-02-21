/*
* 连接会话窗口
* 获取数据源、主机、用户名和密码
*/

#pragma once
#include "Data.h"
#include <Qwidget>

namespace Ui {
	class ConnectionSession;
}

class ConnectionSession : public QWidget
{
	Q_OBJECT
public:
	ConnectionSession(QWidget* parent = nullptr);
	~ConnectionSession();

	
signals:
	void sendConnectionData(Data);

public slots:
	void testConnection();
	void confirm();
	void cancel();

private:
	void getData();

	Ui::ConnectionSession *ui;
	Data data;
};
