// RightFrm.cpp : implementation file
//

#include "stdafx.h"
#include "Test2.h"
#include "RightFrm.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CRightFrm

IMPLEMENT_DYNCREATE(CRightFrm, CFrameWnd)

CRightFrm::CRightFrm()
{
	m_CurView = NULL;
	m_OldView = NULL;
	m_AddOutView = NULL;
	m_FinanceDetail = NULL;
}

CRightFrm::~CRightFrm()
{
}


BEGIN_MESSAGE_MAP(CRightFrm, CFrameWnd)
	//{{AFX_MSG_MAP(CRightFrm)
		// NOTE - the ClassWizard will add and remove mapping macros here.
	ON_MESSAGE(WM_SWITCHVIEW_MESSAGE, OnSwitchViewMessage)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CRightFrm message handlers

LRESULT CRightFrm::OnSwitchViewMessage(WPARAM wParam, LPARAM lParam)
{
	m_OldView = m_CurView;
	if(NULL != m_OldView)
	{
		m_OldView->ShowWindow(SW_HIDE);
	}
	int nDlgId = (int)lParam;
	CRect cr;
	this->GetWindowRect(&cr);
	switch(nDlgId)
	{
		case IDD_ADDOUT_VIEW:
		{
			if(NULL == m_AddOutView)
			{
				m_AddOutView = new CAddOut();
				m_AddOutView->Create(NULL, NULL,WS_CHILD,CRect(0,0,cr.Width(),cr.Height()),this,IDD_ADDOUT_VIEW);
			}
			m_AddOutView->ShowWindow(SW_SHOW);
			m_CurView = m_AddOutView;
		}
		break;
		case IDD_FINDTL_VIEW:
		{
			if(NULL == m_FinanceDetail)
			{
				m_FinanceDetail = new CFinanceDetail();
				m_FinanceDetail->Create(NULL, NULL,WS_CHILD,CRect(0,0,cr.Width(),cr.Height()),this,IDD_FINDTL_VIEW);
				m_FinanceDetail->OnInitialUpdate();
			}
			m_FinanceDetail->ShowWindow(SW_SHOW);
			m_CurView = m_FinanceDetail;
		}
		break;
	}
		
	return 0;
} 

BOOL CRightFrm::OnCreateClient(LPCREATESTRUCT lpcs, CCreateContext* pContext) 
{
	// TODO: Add your specialized code here and/or call the base class
	
	return TRUE;//CFrameWnd::OnCreateClient(lpcs, pContext);
}
