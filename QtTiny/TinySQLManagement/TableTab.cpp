/* TableTab.cpp */

#include "TableTab.h"
#include "ui_TableTab.h"
#include <QSqlTableModel>
#include <QSqlError>
#include <QMessageBox>

TableTab::TableTab(QWidget* p)
	: QWidget(p)
	, ui(new Ui::TableTab)
{
	ui->setupUi(this);
	top = new QToolBar(this);
	bottom = new QToolBar(this);
	//���ò���
	ui->topToolBarLayout->addWidget(top);
	ui->bottomToolBarLayout->addWidget(bottom);
	ui->sortText->setFixedHeight(0);
	ui->selectText->setFixedHeight(0);
	ui->text->setFixedHeight(0);
	ui->selectBtn->setFixedHeight(0);
	ui->sortBtn->setFixedHeight(0);
	ui->selectBtn->setFixedWidth(50);
	ui->sortBtn->setFixedWidth(50);
	ui->selectBtn->setIcon(QIcon("://image/yes.png"));
	ui->sortBtn->setIcon(QIcon("://image/yes.png"));

	initToolBar();
	initConnect();
}

TableTab::~TableTab()
{
	qDebug() << "Destroy TableTab";
	delete ui;
}

void TableTab::initToolBar()
{
	//��������������
	top->setToolButtonStyle(Qt::ToolButtonTextBesideIcon);
	top->setIconSize(QSize(20, 20));
	//�����������
	top->addAction(QIcon("://image/trans.png"), QStringLiteral("��ʼ����"), [this] {
		model->setEditStrategy(QSqlTableModel::OnManualSubmit);
		top->actions().at(0)->setVisible(false);
		top->actions().at(1)->setVisible(true);
		top->actions().at(2)->setVisible(true);
		});
	top->addAction(QIcon("://image/commit.png"), QStringLiteral("�ύ"), [this] {
		model->setEditStrategy(QSqlTableModel::OnFieldChange);
		if (!model->submitAll())
		{
			QMessageBox::critical(this, QStringLiteral("�ύ����"), model->lastError().text(), QMessageBox::Ok);
			return;
		}
		top->actions().at(0)->setVisible(true);
		top->actions().at(1)->setVisible(false);
		top->actions().at(2)->setVisible(false);
		});
	top->addAction(QIcon("://image/rollback.png"), QStringLiteral("�ع�"), [this] {
		model->setEditStrategy(QSqlTableModel::OnFieldChange);
		model->revertAll();
		top->actions().at(0)->setVisible(true);
		top->actions().at(1)->setVisible(false);
		top->actions().at(2)->setVisible(false);
		});
	top->actions().at(1)->setVisible(false);
	top->actions().at(2)->setVisible(false);
	top->addSeparator();
	//��ʾ�ı�
	top->addAction(QIcon("://image/text.png"), QStringLiteral("�ı�"), [this] {
		if (ui->text->height() == 0)
			ui->text->setFixedHeight(100);
		else
			ui->text->setFixedHeight(0);
		});
	//ɸѡ����
	top->addAction(QIcon("://image/select.png"), QStringLiteral("ɸѡ"), [this] {
		if (ui->selectText->height() == 0)
		{
			ui->selectText->setFixedHeight(80);
			ui->selectBtn->setFixedHeight(20);
		}
		else
		{
			ui->selectText->setFixedHeight(0);
			ui->selectBtn->setFixedHeight(0);
		}
		});
	//�������
	top->addAction(QIcon("://image/sort.png"), QStringLiteral("����"), [this] {
		
		if (ui->sortText->height() == 0)
		{
			ui->sortText->setFixedHeight(80);
			ui->sortBtn->setFixedHeight(20);
		}
		else
		{
			ui->sortText->setFixedHeight(0);
			ui->sortBtn->setFixedHeight(0);
		}
		
		});
	
	//�ײ�����������
	bottom->setIconSize(QSize(15, 15));
	bottom->layout()->setSpacing(5);
	//�������
	bottom->addAction(QIcon("://image/add.png"), QStringLiteral(""), [this] {
		model->insertRow(model->rowCount());
		});
	//�Ƴ�����
	bottom->addAction(QIcon("://image/remove.png"), QStringLiteral(""), [this] {
		auto choose = QMessageBox::warning(this, QStringLiteral("ɾ����ǰ�У�"), QStringLiteral("ȷ��ɾ����"), QMessageBox::Yes, QMessageBox::No);
		if (choose == QMessageBox::Yes)
		{
			int curRow = ui->tableView->currentIndex().row();
			model->removeRow(curRow);
		}
		});
	//�ύ����
	bottom->addAction(QIcon("://image/yes.png"), QStringLiteral(""), [this] {
		if (!model->submitAll())
		{
			QMessageBox::critical(this, QStringLiteral("�ύ����"), model->lastError().text(), QMessageBox::Ok);
		}
		});
	//�ع�����
	bottom->addAction(QIcon("://image/no.png"), QStringLiteral(""), [this] {
		model->revertAll();
		});

	bottom->addAction(QIcon("://image/refresh.png"), QStringLiteral(""), [this] {
		model->select();
		});
	

}

void TableTab::initConnect()
{
	//��ʾ�ı�
	connect(ui->tableView, &QTableView::clicked, [this](const QModelIndex& index) {
		ui->text->setText(index.data().toString());
		});

	//ȷ��ɸѡ
	connect(ui->selectBtn, &QPushButton::clicked, [this] {
		//���ı���ȡɸѡ����
		QString text = ui->selectText->toPlainText();
		model->setFilter(text);
		model->select();
		});
	//ȷ������
	connect(ui->sortBtn, &QPushButton::clicked, [this] {
		//���ı���ȡҪѡ����������
		QString text = ui->sortText->toPlainText();
		model->setSort(text.toInt(), Qt::AscendingOrder);
		model->select();
		});
}

void TableTab::setModel(QSqlTableModel* m)
{
	model = m;
}