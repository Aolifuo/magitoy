/* ConnectionSession.cpp */

#include "ConnectionSession.h"
#include "ui_ConnectionSession.h"

#include <QDebug>
#include <QSqlDataBase>
#include <QMessageBox>
#include <QSqlError>

ConnectionSession::ConnectionSession(QWidget* parent)
	: QWidget(parent)
	, ui(new Ui::ConnectionSession)
{
	ui->setupUi(this);
	//setAttribute(Qt::WA_DeleteOnClose, true); //关闭自动销毁
	//信号槽
	//测试连接
	connect(ui->testConnection, &QPushButton::clicked, [this] { this->testConnection(); });
	//确定
	connect(ui->confirm, &QPushButton::clicked, [this] { this->confirm(); });
	//返回
	connect(ui->cancel, &QPushButton::clicked, [this] { this->cancel(); });
}

ConnectionSession::~ConnectionSession()
{
	qDebug() << "Destory ConnectionSession";
	delete ui;
}

//得到用户输入数据
void ConnectionSession::getData()
{
	data.connectionName = ui->connectionName->text();
	data.dataBaseName = ui->databaseName->text();
	data.hostName = ui->hostName->text();
	data.userName = ui->userName->text();
	data.passward = ui->password->text();
}

//测试连接，是否能连接数据库
void ConnectionSession::testConnection()
{
	getData();
	QSqlDatabase db = QSqlDatabase::addDatabase("QODBC", data.connectionName);
	db.setDatabaseName(data.dataBaseName);
	db.setHostName(data.hostName);
	db.setUserName(data.userName);
	db.setPassword(data.passward);
	//打开数据库尝试
	if (db.open())
	{                           
		QMessageBox::information(this, QStringLiteral("提示"), QStringLiteral("连接成功"), QMessageBox::Ok);
	}
	else
	{
		QMessageBox::critical(this, QStringLiteral("连接失败"), db.lastError().text(), QMessageBox::Ok);
	}
	
	db.close();
	QSqlDatabase::removeDatabase(data.connectionName); 
	
}

//确定按钮，并返回连接信息
void ConnectionSession::confirm()
{
	getData();
	emit sendConnectionData(data);
	close();
}

//取消并关闭会话
void ConnectionSession::cancel()
{
	close();
}