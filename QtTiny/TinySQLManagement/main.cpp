#include "TinySQLManagement.h"
#include "TableTab.h"
#include <QtWidgets/QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    TinySQLManagement w;
    w.show();

    return a.exec();
}
