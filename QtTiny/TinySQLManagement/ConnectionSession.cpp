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
	//setAttribute(Qt::WA_DeleteOnClose, true); //�ر��Զ�����
	//�źŲ�
	//��������
	connect(ui->testConnection, &QPushButton::clicked, [this] { this->testConnection(); });
	//ȷ��
	connect(ui->confirm, &QPushButton::clicked, [this] { this->confirm(); });
	//����
	connect(ui->cancel, &QPushButton::clicked, [this] { this->cancel(); });
}

ConnectionSession::~ConnectionSession()
{
	qDebug() << "Destory ConnectionSession";
	delete ui;
}

//�õ��û���������
void ConnectionSession::getData()
{
	data.connectionName = ui->connectionName->text();
	data.dataBaseName = ui->databaseName->text();
	data.hostName = ui->hostName->text();
	data.userName = ui->userName->text();
	data.passward = ui->password->text();
}

//�������ӣ��Ƿ����������ݿ�
void ConnectionSession::testConnection()
{
	getData();
	QSqlDatabase db = QSqlDatabase::addDatabase("QODBC", data.connectionName);
	db.setDatabaseName(data.dataBaseName);
	db.setHostName(data.hostName);
	db.setUserName(data.userName);
	db.setPassword(data.passward);
	//�����ݿⳢ��
	if (db.open())
	{                           
		QMessageBox::information(this, QStringLiteral("��ʾ"), QStringLiteral("���ӳɹ�"), QMessageBox::Ok);
	}
	else
	{
		QMessageBox::critical(this, QStringLiteral("����ʧ��"), db.lastError().text(), QMessageBox::Ok);
	}
	
	db.close();
	QSqlDatabase::removeDatabase(data.connectionName); 
	
}

//ȷ����ť��������������Ϣ
void ConnectionSession::confirm()
{
	getData();
	emit sendConnectionData(data);
	close();
}

//ȡ�����رջỰ
void ConnectionSession::cancel()
{
	close();
}