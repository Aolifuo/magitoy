/*QueryTab.cpp*/

#include "QueryTab.h"
#include "ui_QueryTab.h"

#include <QSqlDataBase>
#include <QSqlError>
#include <QMessageBox>

QueryTab::QueryTab(QWidget* p)
	: QWidget(p)
	, ui(new Ui::QueryTab)
	, model(new QSqlQueryModel(this))
{
	ui->setupUi(this);
	ui->tableView->setModel(model);
	ui->run->setIcon(QIcon("://image/trans.png"));
	//run按钮的信号槽
	connect(ui->run, &QPushButton::clicked, [this] { runSql(); });
}

QueryTab::~QueryTab()
{
	delete ui;
}

void QueryTab::setConnections(const std::vector<QString>& vec)
{
	for (const auto& text : vec)
	{
		ui->comboBox->addItem(text);
	}
	
}

void QueryTab::runSql()
{
	QString cur = ui->comboBox->currentText();
	//从选择栏获取当前连接信息
	if (cur == "")
	{
		QMessageBox::warning(this, QStringLiteral("警告"), QStringLiteral("当前没有任何连接"), QMessageBox::Ok);
		return;
	}
	//从文本获取用户输入的SQL语句
	QString queryText = ui->textEdit->toPlainText();
	QSqlDatabase db = QSqlDatabase::database(cur);
	//运行SQL语句
	model->setQuery(queryText, db);
	
}

void foo() {

}