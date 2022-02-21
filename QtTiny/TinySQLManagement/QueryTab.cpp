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
	//run��ť���źŲ�
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
	//��ѡ������ȡ��ǰ������Ϣ
	if (cur == "")
	{
		QMessageBox::warning(this, QStringLiteral("����"), QStringLiteral("��ǰû���κ�����"), QMessageBox::Ok);
		return;
	}
	//���ı���ȡ�û������SQL���
	QString queryText = ui->textEdit->toPlainText();
	QSqlDatabase db = QSqlDatabase::database(cur);
	//����SQL���
	model->setQuery(queryText, db);
	
}

void foo() {

}