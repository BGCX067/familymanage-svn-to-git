// Tree1.cpp : implementation file
//

#include "stdafx.h"
#include "Test2.h"
#include "Tree1.h"
#include "MainFrm.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CTree1

IMPLEMENT_DYNCREATE(CTree1, CTreeView)

CTree1::CTree1()
{
}

CTree1::~CTree1()
{
}


BEGIN_MESSAGE_MAP(CTree1, CTreeView)
	//{{AFX_MSG_MAP(CTree1)
	ON_NOTIFY_REFLECT(NM_CLICK, OnClick)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CTree1 drawing

void CTree1::OnDraw(CDC* pDC)
{
	CDocument* pDoc = GetDocument();
	// TODO: add draw code here
}

/////////////////////////////////////////////////////////////////////////////
// CTree1 diagnostics

#ifdef _DEBUG
void CTree1::AssertValid() const
{
	CTreeView::AssertValid();
}

void CTree1::Dump(CDumpContext& dc) const
{
	CTreeView::Dump(dc);
}
#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CTree1 message handlers

void CTree1::OnInitialUpdate() 
{
	CTreeView::OnInitialUpdate();
	
	// TODO: Add your specialized code here and/or call the base class
	::SetWindowLong(m_hWnd,GWL_STYLE,WS_VISIBLE|WS_TABSTOP
	|WS_CHILD);
 
	CTreeCtrl* pCtrl = &GetTreeCtrl();
	HTREEITEM h;
	for(int i = 0; i < 10; i++)
	{
		char buffer[5];
		sprintf(buffer, "%d%d%d", i, i, i);
		h=pCtrl->InsertItem(buffer,0,0,0,0);
		for(int j = 100; j < 110; j++)
		{
			sprintf(buffer, "%d", j);
			pCtrl->InsertItem(buffer,0,0,h,0);
		}
	}
	
}



void CTree1::OnClick(NMHDR* pNMHDR, LRESULT* pResult) 
{
	// TODO: Add your control notification handler code here
	UINT nflags;
	CPoint point;
	GetCursorPos(&point);
	this->GetTreeCtrl().ScreenToClient(&point);
	HTREEITEM htree=this->GetTreeCtrl().HitTest(point,&nflags);
	if (htree!=NULL)
	{
		if(this->GetTreeCtrl().ItemHasChildren(htree))
		{
			this->GetTreeCtrl().Expand(htree, TVE_TOGGLE);
		}
		else
		{
			int nViewId;
			if("100" == this->GetTreeCtrl().GetItemText(htree))
			{
				nViewId = IDD_ADDOUT_VIEW;
			}
			if("101" == this->GetTreeCtrl().GetItemText(htree))
			{
				nViewId = IDD_FINDTL_VIEW;
			}
			CMainFrame* pMainFrame = (CMainFrame*)(AfxGetApp()->m_pMainWnd); 
			CView* pView = (CView*)pMainFrame->m_wndSplitter.GetPane(0, 1);
			pView->PostMessage(WM_SWITCHVIEW_MESSAGE, 0, LPARAM(nViewId));
		}
	}
	*pResult = 0;
}
