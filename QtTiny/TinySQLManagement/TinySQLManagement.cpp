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
    std::map<QString, Data> dataMap; //ÿ����������

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

    //������������С��С
    resize(1000, 600);
    setMinimumSize(800, 600);

    //��������ؼ���С
    ui->navigation->setFixedWidth(160);
    ui->information->setFixedWidth(160);

    //�������ӻỰ����
    R->session->setWindowTitle(QStringLiteral("����")); //���ñ���
    R->session->setWindowModality(Qt::ApplicationModal); //������������

    //����ģʽ�Ự����
    R->schemaSession->setWindowTitle(QStringLiteral("�½�ģʽ"));
    R->schemaSession->setWindowModality(Qt::ApplicationModal);

    //���õ�����
    ui->navigation->setFrameStyle(QFrame::NoFrame); //�ޱ߿�
    ui->navigation->setHeaderLabel(QStringLiteral("����")); //����
    ui->navigation->setContextMenuPolicy(Qt::CustomContextMenu); //�����Ҽ��˵�����

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


//��ʼ��������
void TinySQLManagement::initToolBar()
{
    ui->mainToolBar->setToolButtonStyle(Qt::ToolButtonTextUnderIcon); //������ͼƬ����
    ui->mainToolBar->setIconSize(QSize(30, 30)); //����ͼƬ��С
    ui->mainToolBar->layout()->setSpacing(10);
    

    //�����ݿ����ӻỰ
    ui->mainToolBar->addAction(QIcon("://image/open_connection.png"), QStringLiteral("����"), [this] {
        R->session->show();
        });
    //�½���ѯ
    ui->mainToolBar->addAction(QIcon("://image/insert_table.png"), QStringLiteral("�½���ѯ"), [this] { showNewQuery(); });
    ui->mainToolBar->addSeparator();

}

//��ʼ���źŲ�
void TinySQLManagement::initConnect()
{
    //������������
    connect(R->session, &ConnectionSession::sendConnectionData, [this](Data data) {
        if (R->dataMap.find(data.connectionName) == R->dataMap.end()) //û�д��ڵ�����
        {
            QSqlDatabase db = QSqlDatabase::addDatabase("QODBC", data.connectionName);
            db.setDatabaseName(data.dataBaseName);
            db.setHostName(data.hostName);
            db.setUserName(data.userName);
            db.setPassword(data.passward);
            R->dataMap[data.connectionName] = data;
            addNavigationNewConnection(data.connectionName);
        }
        else //�����Ѵ���
        {
            QMessageBox::warning(this, QStringLiteral("����"), QStringLiteral("�����������ظ�"), QMessageBox::Ok);
        }
        });
    //���ܷ����½�ģʽ����
    connect(R->schemaSession, &SchemaSession::sendSchemaData, this, &TinySQLManagement::createSchema);
    //�������Ҽ��˵�
    connect(ui->navigation, &QTreeWidget::customContextMenuRequested, this, &TinySQLManagement::showNavigationMenu);
    //˫���򿪱�
    connect(ui->navigation, &QTreeWidget::itemDoubleClicked, this, &TinySQLManagement::showTable);
    //ɾ��Tab�¼�
    connect(ui->tabWidget, &QTabWidget::tabCloseRequested, [this](int i) {
         QWidget* w = ui->tabWidget->widget(i);
         ui->tabWidget->removeTab(i);
         delete w;
        });
}

//�ڵ���������µ�����
void TinySQLManagement::addNavigationNewConnection(QString connectionName)
{
    QTreeWidgetItem* item = new QTreeWidgetItem(ui->navigation, ItemLevel::CONNECTION);
    item->setText(0, connectionName);
    ui->navigation->addTopLevelItem(item);
    
}

//չʾ�Ҽ��˵�
void TinySQLManagement::showNavigationMenu(const QPoint& pos)
{
    QTreeWidgetItem* item = ui->navigation->itemAt(pos);
    QMenu menu(this);
    //���ݲ㼶ѡ��˵�
    
    if (item->type() == ItemLevel::CONNECTION) //���Ӳ㼶
    {
        //������
        menu.addAction(QStringLiteral("������"), [this, item, &pos] {
            QSqlDatabase db = QSqlDatabase::database(item->text(0), true);
            if (db.isOpen())
            {
                item->setIcon(0, QIcon("://image/connection.png"));
                showDataBase(pos);
                //QMessageBox::information(this, QStringLiteral("��ʾ"), QStringLiteral("���ӳɹ�"), QMessageBox::Ok);
            }
            else
            {
                QMessageBox::critical(this, QStringLiteral("����ʧ��"), db.lastError().text(), QMessageBox::Ok);
            
            }
            });

        //�ر�����
        menu.addAction(QStringLiteral("�ر�����"), [item] {
            item->setIcon(0, QIcon());
            delete item->child(0);
            QSqlDatabase::database(item->text(0), false).close();
            });

        menu.addAction(QStringLiteral("�༭����"), [this] {
            
            });
    }
    else if (item->type() == ItemLevel::DATABASE)//���ݿ�㼶
    {
        menu.addAction(QStringLiteral("�½�ģʽ"), [this, item] {
            R->schemaSession->setConnectionName(item->parent()->text(0));
            R->schemaSession->setDataBaseItem(item);
            R->schemaSession->show();
            });

        
    }
    else if (item->type() == ItemLevel::SCHEMA) //ģʽ�㼶
    {
        menu.addAction(QStringLiteral("ɾ��ģʽ"), [this, item] {
            auto choose = QMessageBox::information(this, 
                QStringLiteral("ȷ��ɾ��"), QStringLiteral("ȷ��Ҫɾ����ģʽ��"),
                QMessageBox::Ok, QMessageBox::Cancel);
            if (choose == QMessageBox::Ok)
            {
                delete item;
            }
                
            });
        menu.addAction(QStringLiteral("�½���"), [this] {
            
            });
        
    }
    else //��㼶
    {
        menu.addAction(QStringLiteral("ɾ����"), [this, item] {
            auto choose = QMessageBox::information(this,
                QStringLiteral("ȷ��ɾ��"), QStringLiteral("ȷ��Ҫɾ���˱���"),
                QMessageBox::Ok, QMessageBox::Cancel);
            if (choose == QMessageBox::Ok)
            {
                delete item;
            }
            });
    }
    
    menu.exec(ui->navigation->mapToGlobal(pos));
}

//�ڵ�������ʾ���ݿ������ģʽ�ͱ���״�ṹ
void TinySQLManagement::showDataBase(const QPoint& pos)
{
    QTreeWidgetItem* connectionItem = ui->navigation->itemAt(pos);
    Data& data = R->dataMap[connectionItem->text(0)];
    //�������ݿ���
    QTreeWidgetItem* databaseItem = new QTreeWidgetItem(connectionItem, ItemLevel::DATABASE);
    databaseItem->setIcon(0, QIcon("://image/database.png"));
    databaseItem->setText(0, data.dataBaseName);
    
    QSqlDatabase db = QSqlDatabase::database(data.connectionName);
    QSqlQuery query(db);
    //�������ݿ�������ģʽ
    if (!query.exec("select schema_name from information_schema.schemata"))
    {
        QMessageBox::warning(this, QStringLiteral("��ѯģʽʧ��"), query.lastError().text(), QMessageBox::Ok);
        return;
    }
    //�������ݿ�������ģʽ��
    while (query.next())
    {
        QTreeWidgetItem* schemaItem = new QTreeWidgetItem(databaseItem, ItemLevel::SCHEMA);
        schemaItem->setIcon(0, QIcon("://image/schema.png"));
        schemaItem->setText(0, query.value(0).toString());

        //����ģʽ�����б�
        QSqlQuery queryTable(db);
        if (!queryTable.exec("select tablename from pg_tables where schemaname='" + query.value(0).toString() + "'"))
        {
            QMessageBox::warning(this, QStringLiteral("��ѯ��ʧ��"), query.lastError().text(), QMessageBox::Ok);
            continue;
        }
        //����ģʽ�����б���
        while (queryTable.next())
        {
            QTreeWidgetItem* tableItem = new QTreeWidgetItem(schemaItem, ItemLevel::TABLE);
            tableItem->setIcon(0, QIcon("://image/table.png"));
            tableItem->setText(0, queryTable.value(0).toString());
        }
    }

}

//չʾ������ݣ�������
void TinySQLManagement::showTable(QTreeWidgetItem* tableItem, int col)
{
    //����Ƿ������Ǳ�
    if (tableItem->type() != ItemLevel::TABLE)
        return;
    
    //���ݱ�Ѱ��ģʽ��������
    QTreeWidgetItem* schemaItem = tableItem->parent();
    QTreeWidgetItem* root = schemaItem->parent()->parent();
    QString schemaName = schemaItem->text(0);
    QString tableName = tableItem->text(0);
    QString connectionName = root->text(0);

    TableTab* tab = new TableTab();
    
    ui->tabWidget->addTab(tab, tableItem->text(col));
    ui->tabWidget->setCurrentIndex(ui->tabWidget->count() - 1);

    //��ʾ���
    QTableView* tableView = tab->findChild<QTableView*>("tableView");
    QSqlTableModel* model = new QSqlTableModel(tab, QSqlDatabase::database(connectionName));
    model->setTable(schemaName + "." + tableName);
    model->setEditStrategy(QSqlTableModel::OnFieldChange);
    tab->setModel(model);
    tableView->setModel(model);
    model->select();
    
    
}

//�½���ѯ����
void TinySQLManagement::showNewQuery()
{
    QueryTab* tab = new QueryTab;
    std::vector<QString> vec;
    //ȡ������������
    for (auto d : R->dataMap)
    {
        vec.push_back(d.first);
    }
    tab->setConnections(vec);
    ui->tabWidget->addTab(tab, QStringLiteral("�½���ѯ"));
    ui->tabWidget->setCurrentIndex(ui->tabWidget->count() - 1);
    
}

//����ģʽ
void TinySQLManagement::createSchema(SchemaData data)
{
    QSqlDatabase db = QSqlDatabase::database(data.connectionName);
    QSqlQuery query(db);

    if (!query.exec("CREATE SCHEMA " + data.name + " AUTHORIZATION " + data.owner))
    {
        QMessageBox::warning(this, QStringLiteral("�½�ģʽʧ��"), query.lastError().text(), QMessageBox::Ok);
        return;
    }

    QTreeWidgetItem* schemaItem = new QTreeWidgetItem(data.item, ItemLevel::SCHEMA);
}

