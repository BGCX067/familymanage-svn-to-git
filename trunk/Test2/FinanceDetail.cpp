// FinanceDetail.cpp : implementation file
//

#include "stdafx.h"
#include "Test2.h"
#include "FinanceDetail.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CFinanceDetail

IMPLEMENT_DYNCREATE(CFinanceDetail, CFormView)

CFinanceDetail::CFinanceDetail()
	: CFormView(CFinanceDetail::IDD)
{
	//{{AFX_DATA_INIT(CFinanceDetail)
		// NOTE: the ClassWizard will add member initialization here
	//}}AFX_DATA_INIT
}

CFinanceDetail::~CFinanceDetail()
{
}

void CFinanceDetail::DoDataExchange(CDataExchange* pDX)
{
	CFormView::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CFinanceDetail)
		// NOTE: the ClassWizard will add DDX and DDV calls here
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CFinanceDetail, CFormView)
	//{{AFX_MSG_MAP(CFinanceDetail)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CFinanceDetail diagnostics

#ifdef _DEBUG
void CFinanceDetail::AssertValid() const
{
	CFormView::AssertValid();
}

void CFinanceDetail::Dump(CDumpContext& dc) const
{
	CFormView::Dump(dc);
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CFinanceDetail message handlers


BOOL CFinanceDetail::Create(LPCTSTR lpszClassName, LPCTSTR lpszWindowName, DWORD dwStyle, const RECT& rect, CWnd* pParentWnd, UINT nID, CCreateContext* pContext) 
{
	// TODO: Add your specialized code here and/or call the base class
	
	return CFormView::Create(lpszClassName, lpszWindowName, dwStyle, rect, pParentWnd, nID, pContext);
}

void CFinanceDetail::OnInitialUpdate() 
{
	CFormView::OnInitialUpdate();
	
	// TODO: Add your specialized code here and/or call the base class
	CListCtrl* list = (CListCtrl*)GetDlgItem(IDC_RESULT_LIST);
	list->InsertColumn(0, "Date", LVCFMT_LEFT, 100, 0);
	list->InsertColumn(1, "Time", LVCFMT_LEFT, 100, 1);
	list->InsertColumn(2, "Category", LVCFMT_LEFT, 100, 2);
	list->InsertColumn(3, "Amount", LVCFMT_LEFT, 100, 3);
	list->InsertColumn(4, "Position", LVCFMT_LEFT, 100, 4);
	list->InsertColumn(5, "Detail", LVCFMT_LEFT, 100, 1);
	CString strText;
	list->InsertItem(0, "10");
	list->SetItemText(0, 1, "11");
	list->InsertItem(1, "20");
/*
	// Insert 10 items in the list view control.
	for (int i=0;i < 10;i++)
	{
	   strText.Format(TEXT("item %d"), i);

	   // Insert the item, select every other item.
	   list->InsertItem(
		  LVIF_TEXT|LVIF_STATE, i, strText, 
		  (i%2)==0 ? LVIS_SELECTED : 0, LVIS_SELECTED,
		  0, 0);

	   // Initialize the text of the subitems.
	   for (int j=1;j < 6;j++)
	   {
		  strText.Format(TEXT("sub-item %d %d"), i, j);
		  list->SetItemText(i, j, strText);
	   }
	}
*/
}
