#if !defined(AFX_RightFrm_H__E4E9642D_AF59_4CC2_B6BE_D3E064205A22__INCLUDED_)
#define AFX_RightFrm_H__E4E9642D_AF59_4CC2_B6BE_D3E064205A22__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// RightFrm.h : header file
//

#define WM_SWITCHVIEW_MESSAGE (WM_USER+100)

#include "AddOut.h"
#include "FinanceDetail.h"

/////////////////////////////////////////////////////////////////////////////
// CRightFrm frame

class CRightFrm : public CFrameWnd
{
	DECLARE_DYNCREATE(CRightFrm)
protected:
	CRightFrm();           // protected constructor used by dynamic creation

// Attributes
public:
private:
	CView* m_CurView;
	CView* m_OldView;
	CAddOut* m_AddOutView;
	CFinanceDetail* m_FinanceDetail;

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CRightFrm)
	protected:
	virtual BOOL OnCreateClient(LPCREATESTRUCT lpcs, CCreateContext* pContext);
	//}}AFX_VIRTUAL

// Implementation
protected:
	virtual ~CRightFrm();

	// Generated message map functions
	//{{AFX_MSG(CRightFrm)
		// NOTE - the ClassWizard will add and remove member functions here.
	afx_msg LRESULT OnSwitchViewMessage(WPARAM wParam, LPARAM lParam); 
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_RightFrm_H__E4E9642D_AF59_4CC2_B6BE_D3E064205A22__INCLUDED_)
