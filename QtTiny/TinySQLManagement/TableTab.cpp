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
	//设置布局
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
	//顶部工具栏设置
	top->setToolButtonStyle(Qt::ToolButtonTextBesideIcon);
	top->setIconSize(QSize(20, 20));
	//开启事务操作
	top->addAction(QIcon("://image/trans.png"), QStringLiteral("开始事务"), [this] {
		model->setEditStrategy(QSqlTableModel::OnManualSubmit);
		top->actions().at(0)->setVisible(false);
		top->actions().at(1)->setVisible(true);
		top->actions().at(2)->setVisible(true);
		});
	top->addAction(QIcon("://image/commit.png"), QStringLiteral("提交"), [this] {
		model->setEditStrategy(QSqlTableModel::OnFieldChange);
		if (!model->submitAll())
		{
			QMessageBox::critical(this, QStringLiteral("提交出错"), model->lastError().text(), QMessageBox::Ok);
			return;
		}
		top->actions().at(0)->setVisible(true);
		top->actions().at(1)->setVisible(false);
		top->actions().at(2)->setVisible(false);
		});
	top->addAction(QIcon("://image/rollback.png"), QStringLiteral("回滚"), [this] {
		model->setEditStrategy(QSqlTableModel::OnFieldChange);
		model->revertAll();
		top->actions().at(0)->setVisible(true);
		top->actions().at(1)->setVisible(false);
		top->actions().at(2)->setVisible(false);
		});
	top->actions().at(1)->setVisible(false);
	top->actions().at(2)->setVisible(false);
	top->addSeparator();
	//显示文本
	top->addAction(QIcon("://image/text.png"), QStringLiteral("文本"), [this] {
		if (ui->text->height() == 0)
			ui->text->setFixedHeight(100);
		else
			ui->text->setFixedHeight(0);
		});
	//筛选操作
	top->addAction(QIcon("://image/select.png"), QStringLiteral("筛选"), [this] {
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
	//排序操作
	top->addAction(QIcon("://image/sort.png"), QStringLiteral("排序"), [this] {
		
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
	
	//底部工具栏设置
	bottom->setIconSize(QSize(15, 15));
	bottom->layout()->setSpacing(5);
	//添加数据
	bottom->addAction(QIcon("://image/add.png"), QStringLiteral(""), [this] {
		model->insertRow(model->rowCount());
		});
	//移除数据
	bottom->addAction(QIcon("://image/remove.png"), QStringLiteral(""), [this] {
		auto choose = QMessageBox::warning(this, QStringLiteral("删除当前行！"), QStringLiteral("确定删除吗"), QMessageBox::Yes, QMessageBox::No);
		if (choose == QMessageBox::Yes)
		{
			int curRow = ui->tableView->currentIndex().row();
			model->removeRow(curRow);
		}
		});
	//提交数据
	bottom->addAction(QIcon("://image/yes.png"), QStringLiteral(""), [this] {
		if (!model->submitAll())
		{
			QMessageBox::critical(this, QStringLiteral("提交出错"), model->lastError().text(), QMessageBox::Ok);
		}
		});
	//回滚数据
	bottom->addAction(QIcon("://image/no.png"), QStringLiteral(""), [this] {
		model->revertAll();
		});

	bottom->addAction(QIcon("://image/refresh.png"), QStringLiteral(""), [this] {
		model->select();
		});
	

}

void TableTab::initConnect()
{
	//显示文本
	connect(ui->tableView, &QTableView::clicked, [this](const QModelIndex& index) {
		ui->text->setText(index.data().toString());
		});

	//确定筛选
	connect(ui->selectBtn, &QPushButton::clicked, [this] {
		//从文本获取筛选对象
		QString text = ui->selectText->toPlainText();
		model->setFilter(text);
		model->select();
		});
	//确定排序
	connect(ui->sortBtn, &QPushButton::clicked, [this] {
		//从文本获取要选择的排序对象
		QString text = ui->sortText->toPlainText();
		model->setSort(text.toInt(), Qt::AscendingOrder);
		model->select();
		});
}

void TableTab::setModel(QSqlTableModel* m)
{
	model = m;
}