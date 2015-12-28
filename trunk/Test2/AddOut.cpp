// AddOut.cpp : implementation file
//

#include "stdafx.h"
#include "Test2.h"
#include "AddOut.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CAddOut

IMPLEMENT_DYNCREATE(CAddOut, CFormView)

CAddOut::CAddOut()
	: CFormView(CAddOut::IDD)
{
	//{{AFX_DATA_INIT(CAddOut)
	m_OutDate = 0;
	m_OutTime = 0;
	m_sPosition = _T("");
	m_sDetail = _T("");
	m_nCategory = -1;
	m_dAmount = 0.0;
	//}}AFX_DATA_INIT
}

CAddOut::~CAddOut()
{
}

void CAddOut::DoDataExchange(CDataExchange* pDX)
{
	CFormView::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CAddOut)
	DDX_MonthCalCtrl(pDX, IDC_MONTHCALENDAR, m_OutDate);
	DDX_DateTimeCtrl(pDX, IDC_TIMEPICKER, m_OutTime);
	DDX_Text(pDX, IDC_POSITION_EDIT, m_sPosition);
	DDX_Text(pDX, IDC_DETAIL_EDIT, m_sDetail);
	DDX_CBIndex(pDX, IDC_CATEGORY_COMBO, m_nCategory);
	DDX_Text(pDX, IDC_AMOUNT_EDIT, m_dAmount);
	//}}AFX_DATA_MAP
}


BEGIN_MESSAGE_MAP(CAddOut, CFormView)
	//{{AFX_MSG_MAP(CAddOut)
	ON_BN_CLICKED(IDC_ADD_BUTTON, OnAddButton)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CAddOut diagnostics

#ifdef _DEBUG
void CAddOut::AssertValid() const
{
	CFormView::AssertValid();
}

void CAddOut::Dump(CDumpContext& dc) const
{
	CFormView::Dump(dc);
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CAddOut message handlers

BOOL CAddOut::Create(LPCTSTR lpszClassName, LPCTSTR lpszWindowName, DWORD dwStyle, const RECT& rect, CWnd* pParentWnd, UINT nID, CCreateContext* pContext) 
{
	// TODO: Add your specialized code here and/or call the base class
	//CWnd* mc = GetDlgItem(IDC_MONTHCALENDAR);
	return CFormView::Create(lpszClassName, lpszWindowName, dwStyle, rect, pParentWnd, nID, pContext);
}

//DEL void CAddOut::OnSelectMonthcalendar(NMHDR* pNMHDR, LRESULT* pResult) 
//DEL {
//DEL 	// TODO: Add your control notification handler code here
//DEL 	CWnd* mc = GetDlgItem(IDC_MONTHCALENDAR);
//DEL 	LPSYSTEMTIME st = new SYSTEMTIME;
//DEL 	MonthCal_GetCurSel(mc->m_hWnd, st);
//DEL 	char a[256] = "111";
//DEL 	sprintf(a, "%d", st->wDay);
//DEL 	AfxMessageBox(a);
//DEL 	*pResult = 0;
//DEL }

void CAddOut::OnAddButton() 
{
	// TODO: Add your control notification handler code here]
	UpdateData(TRUE);
	char a[256] = "";
	sprintf(a, "%d", m_nCategory);
	AfxMessageBox(m_sPosition);
}
