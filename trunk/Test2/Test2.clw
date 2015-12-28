; CLW file contains information for the MFC ClassWizard

[General Info]
Version=1
LastClass=CScheduleView
LastTemplate=CFormView
NewFileInclude1=#include "stdafx.h"
NewFileInclude2=#include "test2.h"
LastPage=0

ClassCount=11
Class1=CAddOut
Class2=CFinanceDetail
Class3=CLoginDlg
Class4=CMainFrame
Class5=CRightFrm
Class6=CTest2App
Class7=CAboutDlg
Class8=CTest2Doc
Class9=CTest2View
Class10=CTree1

ResourceCount=7
Resource1=IDD_ADDOUT_VIEW
Resource2=IDD_FINDTL_VIEW
Resource3=IDD_ABOUTBOX
Resource4=IDR_MAINFRAME
Resource5=IDD_LOGIN_DIALOG
Resource6=IDR_SCHEDULEVIEW_TMPL (Chinese (P.R.C.))
Class11=CScheduleView
Resource7=IDD_SCHEDULEVIEW_FORM (Chinese (P.R.C.))

[CLS:CAddOut]
Type=0
BaseClass=CFormView
HeaderFile=AddOut.h
ImplementationFile=AddOut.cpp

[CLS:CFinanceDetail]
Type=0
BaseClass=CFormView
HeaderFile=FinanceDetail.h
ImplementationFile=FinanceDetail.cpp
Filter=D
VirtualFilter=VWC
LastObject=CFinanceDetail

[CLS:CLoginDlg]
Type=0
BaseClass=CDialog
HeaderFile=LoginDlg.h
ImplementationFile=LoginDlg.cpp

[CLS:CMainFrame]
Type=0
BaseClass=CFrameWnd
HeaderFile=MainFrm.h
ImplementationFile=MainFrm.cpp

[CLS:CRightFrm]
Type=0
BaseClass=CFrameWnd
HeaderFile=RightFrm.h
ImplementationFile=RightFrm.cpp
Filter=T
VirtualFilter=fWC

[CLS:CTest2App]
Type=0
BaseClass=CWinApp
HeaderFile=Test2.h
ImplementationFile=Test2.cpp
Filter=N
LastObject=CTest2App

[CLS:CAboutDlg]
Type=0
BaseClass=CDialog
HeaderFile=Test2.cpp
ImplementationFile=Test2.cpp
LastObject=CAboutDlg

[CLS:CTest2Doc]
Type=0
BaseClass=CDocument
HeaderFile=Test2Doc.h
ImplementationFile=Test2Doc.cpp

[CLS:CTest2View]
Type=0
BaseClass=CView
HeaderFile=Test2View.h
ImplementationFile=Test2View.cpp

[CLS:CTree1]
Type=0
BaseClass=CTreeView
HeaderFile=Tree1.h
ImplementationFile=Tree1.cpp

[DLG:IDD_ADDOUT_VIEW]
Type=1
Class=CAddOut
ControlCount=13
Control1=IDC_STATIC,static,1342308352
Control2=IDC_AMOUNT_EDIT,edit,1350631552
Control3=IDC_STATIC,static,1342308352
Control4=IDC_DETAIL_EDIT,edit,1350631552
Control5=IDC_ADD_BUTTON,button,1342246656
Control6=IDC_TIMEPICKER,SysDateTimePick32,1342242873
Control7=IDC_MONTHCALENDAR,SysMonthCal32,1342242832
Control8=IDC_STATIC,static,1342308352
Control9=IDC_STATIC,static,1342308352
Control10=IDC_STATIC,static,1342308352
Control11=IDC_STATIC,static,1342308352
Control12=IDC_POSITION_EDIT,edit,1350631552
Control13=IDC_CATEGORY_COMBO,combobox,1344339971

[DLG:IDD_FINDTL_VIEW]
Type=1
Class=CFinanceDetail
ControlCount=7
Control1=IDC_RESULT_LIST,SysListView32,1350631425
Control2=IDC_STATIC,static,1342308352
Control3=IDC_STATIC,static,1342308352
Control4=IDC_DATETIMEPICKER1,SysDateTimePick32,1342242848
Control5=IDC_DATETIMEPICKER2,SysDateTimePick32,1342242848
Control6=IDC_STATIC,static,1342308352
Control7=IDC_QUETY_CATEGOTY_COMBO,combobox,1344339970

[DLG:IDD_LOGIN_DIALOG]
Type=1
Class=CLoginDlg
ControlCount=2
Control1=IDOK,button,1342242817
Control2=IDCANCEL,button,1342242816

[DLG:IDD_ABOUTBOX]
Type=1
Class=CAboutDlg
ControlCount=4
Control1=IDC_STATIC,static,1342177283
Control2=IDC_STATIC,static,1342308480
Control3=IDC_STATIC,static,1342308352
Control4=IDOK,button,1342373889

[TB:IDR_MAINFRAME]
Type=1
Class=?
Command1=ID_FILE_NEW
Command2=ID_FILE_OPEN
Command3=ID_FILE_SAVE
Command4=ID_EDIT_CUT
Command5=ID_EDIT_COPY
Command6=ID_EDIT_PASTE
Command7=ID_FILE_PRINT
Command8=ID_APP_ABOUT
CommandCount=8

[MNU:IDR_MAINFRAME]
Type=1
Class=?
CommandCount=0

[ACL:IDR_MAINFRAME]
Type=1
Class=?
Command1=ID_FILE_NEW
Command2=ID_FILE_OPEN
Command3=ID_FILE_SAVE
Command4=ID_FILE_PRINT
Command5=ID_EDIT_UNDO
Command6=ID_EDIT_CUT
Command7=ID_EDIT_COPY
Command8=ID_EDIT_PASTE
Command9=ID_EDIT_UNDO
Command10=ID_EDIT_CUT
Command11=ID_EDIT_COPY
Command12=ID_EDIT_PASTE
Command13=ID_NEXT_PANE
Command14=ID_PREV_PANE
CommandCount=14

[DLG:IDD_SCHEDULEVIEW_FORM (Chinese (P.R.C.))]
Type=1
Class=CScheduleView
ControlCount=7
Control1=IDC_MONTHCALENDAR1,SysMonthCal32,1342242832
Control2=IDC_DATETIMEPICKER1,SysDateTimePick32,1342242848
Control3=IDC_BUTTON1,button,1342242816
Control4=IDC_EDIT1,edit,1350631552
Control5=IDC_COMBO1,combobox,1344340226
Control6=IDC_STATIC,static,1342308352
Control7=IDC_STATIC,static,1342308352

[CLS:CScheduleView]
Type=0
HeaderFile=ScheduleView.h
ImplementationFile=ScheduleView.cpp
BaseClass=CFormView
Filter=D

[MNU:IDR_SCHEDULEVIEW_TMPL (Chinese (P.R.C.))]
Type=1
Class=?
Command1=ID_FILE_NEW
Command2=ID_FILE_OPEN
Command3=ID_FILE_SAVE
Command4=ID_FILE_SAVE_AS
Command5=ID_FILE_PRINT
Command6=ID_FILE_PRINT_PREVIEW
Command7=ID_FILE_PRINT_SETUP
Command8=ID_FILE_MRU_FILE1
Command9=ID_APP_EXIT
Command10=ID_EDIT_UNDO
Command11=ID_EDIT_CUT
Command12=ID_EDIT_COPY
Command13=ID_EDIT_PASTE
Command14=ID_VIEW_TOOLBAR
Command15=ID_VIEW_STATUS_BAR
Command16=ID_APP_ABOUT
CommandCount=16

