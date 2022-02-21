/*SchemaSession.h*/

#include "SchemaSession.h"
#include "ui_SchemaSession.h"

SchemaSession::SchemaSession(QWidget* p)
	: QWidget(p)
	, ui(new Ui::SchemaSession)
{
	ui->setupUi(this);

	//返回模式数据
	connect(ui->confirm, &QPushButton::clicked, [this] { 
		data.name = ui->name->text();
		data.owner = ui->owner->text();
		emit sendSchemaData(data); 
		close();
		});
	connect(ui->cancel, &QPushButton::clicked, [this] { close(); });
}

SchemaSession::~SchemaSession()
{
	delete ui;
}

void SchemaSession::setConnectionName(QString name)
{
	data.connectionName = name;
}

void SchemaSession::setDataBaseItem(QTreeWidgetItem* item)
{
	data.item = item;
}