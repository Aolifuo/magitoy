/* TinySQLManagement */

#include "TinySQLManagement.h"
#include "ui_TinySQLManagement.h"
#include "ConnectionSession.h"
#include "Data.h"
#include "SchemaSession.h"
#include "TableTab.h"
#include "QueryTab.h"

#include <QMessageBox>
#include <QSqlDataBase>
#include <QSqlError>
#include <QSqlQuery>
#include <QSqlTableModel>
#include <QTableView>
#include <map>
#include <vector>

struct TinySQLManagement::MStorage
{
    ConnectionSession* session = new ConnectionSession;
    SchemaSession* schemaSession = new SchemaSession;
    std::map<QString, Data> dataMap; //每个连接数据

    ~MStorage()
    {
        if (session)
            delete session;
        if (schemaSession)
            delete schemaSession;
    }
};


TinySQLManagement::TinySQLManagement(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::TinySQLManagementClass)
    , R(new TinySQLManagement::MStorage)
{
    ui->setupUi(this);

    //设置主窗口最小大小
    resize(1000, 600);
    setMinimumSize(800, 600);

    //设置中央控件大小
    ui->navigation->setFixedWidth(160);
    ui->information->setFixedWidth(160);

    //设置连接会话窗口
    R->session->setWindowTitle(QStringLiteral("连接")); //设置标题
    R->session->setWindowModality(Qt::ApplicationModal); //阻塞其他窗口

    //设置模式会话窗口
    R->schemaSession->setWindowTitle(QStringLiteral("新建模式"));
    R->schemaSession->setWindowModality(Qt::ApplicationModal);

    //设置导航栏
    ui->navigation->setFrameStyle(QFrame::NoFrame); //无边框
    ui->navigation->setHeaderLabel(QStringLiteral("连接")); //标题
    ui->navigation->setContextMenuPolicy(Qt::CustomContextMenu); //启动右键菜单功能

    //
    for (int i = 0; i < ui->tabWidget->count(); ++i)
        ui->tabWidget->removeTab(i);

    initToolBar();
    initConnect();
}

TinySQLManagement::~TinySQLManagement()
{
    delete ui;
    delete R;
}


//初始化工具栏
void TinySQLManagement::initToolBar()
{
    ui->mainToolBar->setToolButtonStyle(Qt::ToolButtonTextUnderIcon); //名字在图片下面
    ui->mainToolBar->setIconSize(QSize(30, 30)); //设置图片大小
    ui->mainToolBar->layout()->setSpacing(10);
    

    //打开数据库连接会话
    ui->mainToolBar->addAction(QIcon("://image/open_connection.png"), QStringLiteral("连接"), [this] {
        R->session->show();
        });
    //新建查询
    ui->mainToolBar->addAction(QIcon("://image/insert_table.png"), QStringLiteral("新建查询"), [this] { showNewQuery(); });
    ui->mainToolBar->addSeparator();

}

//初始化信号槽
void TinySQLManagement::initConnect()
{
    //接受连接数据
    connect(R->session, &ConnectionSession::sendConnectionData, [this](Data data) {
        if (R->dataMap.find(data.connectionName) == R->dataMap.end()) //没有存在的连接
        {
            QSqlDatabase db = QSqlDatabase::addDatabase("QODBC", data.connectionName);
            db.setDatabaseName(data.dataBaseName);
            db.setHostName(data.hostName);
            db.setUserName(data.userName);
            db.setPassword(data.passward);
            R->dataMap[data.connectionName] = data;
            addNavigationNewConnection(data.connectionName);
        }
        else //连接已存在
        {
            QMessageBox::warning(this, QStringLiteral("警告"), QStringLiteral("连接名不能重复"), QMessageBox::Ok);
        }
        });
    //接受返回新建模式数据
    connect(R->schemaSession, &SchemaSession::sendSchemaData, this, &TinySQLManagement::createSchema);
    //导航栏右键菜单
    connect(ui->navigation, &QTreeWidget::customContextMenuRequested, this, &TinySQLManagement::showNavigationMenu);
    //双击打开表
    connect(ui->navigation, &QTreeWidget::itemDoubleClicked, this, &TinySQLManagement::showTable);
    //删除Tab事件
    connect(ui->tabWidget, &QTabWidget::tabCloseRequested, [this](int i) {
         QWidget* w = ui->tabWidget->widget(i);
         ui->tabWidget->removeTab(i);
         delete w;
        });
}

//在导航栏添加新的连接
void TinySQLManagement::addNavigationNewConnection(QString connectionName)
{
    QTreeWidgetItem* item = new QTreeWidgetItem(ui->navigation, ItemLevel::CONNECTION);
    item->setText(0, connectionName);
    ui->navigation->addTopLevelItem(item);
    
}

//展示右键菜单
void TinySQLManagement::showNavigationMenu(const QPoint& pos)
{
    QTreeWidgetItem* item = ui->navigation->itemAt(pos);
    QMenu menu(this);
    //根据层级选择菜单
    
    if (item->type() == ItemLevel::CONNECTION) //连接层级
    {
        //打开连接
        menu.addAction(QStringLiteral("打开连接"), [this, item, &pos] {
            QSqlDatabase db = QSqlDatabase::database(item->text(0), true);
            if (db.isOpen())
            {
                item->setIcon(0, QIcon("://image/connection.png"));
                showDataBase(pos);
                //QMessageBox::information(this, QStringLiteral("提示"), QStringLiteral("连接成功"), QMessageBox::Ok);
            }
            else
            {
                QMessageBox::critical(this, QStringLiteral("连接失败"), db.lastError().text(), QMessageBox::Ok);
            
            }
            });

        //关闭连接
        menu.addAction(QStringLiteral("关闭连接"), [item] {
            item->setIcon(0, QIcon());
            delete item->child(0);
            QSqlDatabase::database(item->text(0), false).close();
            });

        menu.addAction(QStringLiteral("编辑连接"), [this] {
            
            });
    }
    else if (item->type() == ItemLevel::DATABASE)//数据库层级
    {
        menu.addAction(QStringLiteral("新建模式"), [this, item] {
            R->schemaSession->setConnectionName(item->parent()->text(0));
            R->schemaSession->setDataBaseItem(item);
            R->schemaSession->show();
            });

        
    }
    else if (item->type() == ItemLevel::SCHEMA) //模式层级
    {
        menu.addAction(QStringLiteral("删除模式"), [this, item] {
            auto choose = QMessageBox::information(this, 
                QStringLiteral("确认删除"), QStringLiteral("确定要删除此模式吗"),
                QMessageBox::Ok, QMessageBox::Cancel);
            if (choose == QMessageBox::Ok)
            {
                delete item;
            }
                
            });
        menu.addAction(QStringLiteral("新建表"), [this] {
            
            });
        
    }
    else //表层级
    {
        menu.addAction(QStringLiteral("删除表"), [this, item] {
            auto choose = QMessageBox::information(this,
                QStringLiteral("确认删除"), QStringLiteral("确定要删除此表吗"),
                QMessageBox::Ok, QMessageBox::Cancel);
            if (choose == QMessageBox::Ok)
            {
                delete item;
            }
            });
    }
    
    menu.exec(ui->navigation->mapToGlobal(pos));
}

//在导航栏显示数据库的名、模式和表树状结构
void TinySQLManagement::showDataBase(const QPoint& pos)
{
    QTreeWidgetItem* connectionItem = ui->navigation->itemAt(pos);
    Data& data = R->dataMap[connectionItem->text(0)];
    //插入数据库名
    QTreeWidgetItem* databaseItem = new QTreeWidgetItem(connectionItem, ItemLevel::DATABASE);
    databaseItem->setIcon(0, QIcon("://image/database.png"));
    databaseItem->setText(0, data.dataBaseName);
    
    QSqlDatabase db = QSqlDatabase::database(data.connectionName);
    QSqlQuery query(db);
    //搜索数据库中所有模式
    if (!query.exec("select schema_name from information_schema.schemata"))
    {
        QMessageBox::warning(this, QStringLiteral("查询模式失败"), query.lastError().text(), QMessageBox::Ok);
        return;
    }
    //插入数据库下所有模式名
    while (query.next())
    {
        QTreeWidgetItem* schemaItem = new QTreeWidgetItem(databaseItem, ItemLevel::SCHEMA);
        schemaItem->setIcon(0, QIcon("://image/schema.png"));
        schemaItem->setText(0, query.value(0).toString());

        //搜索模式下所有表
        QSqlQuery queryTable(db);
        if (!queryTable.exec("select tablename from pg_tables where schemaname='" + query.value(0).toString() + "'"))
        {
            QMessageBox::warning(this, QStringLiteral("查询表失败"), query.lastError().text(), QMessageBox::Ok);
            continue;
        }
        //插入模式下所有表名
        while (queryTable.next())
        {
            QTreeWidgetItem* tableItem = new QTreeWidgetItem(schemaItem, ItemLevel::TABLE);
            tableItem->setIcon(0, QIcon("://image/table.png"));
            tableItem->setText(0, queryTable.value(0).toString());
        }
    }

}

//展示表的数据，操作表
void TinySQLManagement::showTable(QTreeWidgetItem* tableItem, int col)
{
    //检查是否点击的是表
    if (tableItem->type() != ItemLevel::TABLE)
        return;
    
    //根据表寻找模式和连接名
    QTreeWidgetItem* schemaItem = tableItem->parent();
    QTreeWidgetItem* root = schemaItem->parent()->parent();
    QString schemaName = schemaItem->text(0);
    QString tableName = tableItem->text(0);
    QString connectionName = root->text(0);

    TableTab* tab = new TableTab();
    
    ui->tabWidget->addTab(tab, tableItem->text(col));
    ui->tabWidget->setCurrentIndex(ui->tabWidget->count() - 1);

    //显示表格
    QTableView* tableView = tab->findChild<QTableView*>("tableView");
    QSqlTableModel* model = new QSqlTableModel(tab, QSqlDatabase::database(connectionName));
    model->setTable(schemaName + "." + tableName);
    model->setEditStrategy(QSqlTableModel::OnFieldChange);
    tab->setModel(model);
    tableView->setModel(model);
    model->select();
    
    
}

//新建查询界面
void TinySQLManagement::showNewQuery()
{
    QueryTab* tab = new QueryTab;
    std::vector<QString> vec;
    //取出所有连接名
    for (auto d : R->dataMap)
    {
        vec.push_back(d.first);
    }
    tab->setConnections(vec);
    ui->tabWidget->addTab(tab, QStringLiteral("新建查询"));
    ui->tabWidget->setCurrentIndex(ui->tabWidget->count() - 1);
    
}

//创建模式
void TinySQLManagement::createSchema(SchemaData data)
{
    QSqlDatabase db = QSqlDatabase::database(data.connectionName);
    QSqlQuery query(db);

    if (!query.exec("CREATE SCHEMA " + data.name + " AUTHORIZATION " + data.owner))
    {
        QMessageBox::warning(this, QStringLiteral("新建模式失败"), query.lastError().text(), QMessageBox::Ok);
        return;
    }

    QTreeWidgetItem* schemaItem = new QTreeWidgetItem(data.item, ItemLevel::SCHEMA);
}

