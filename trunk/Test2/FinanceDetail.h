#if !defined(AFX_FinanceDetail_H__BDA4309F_DFF8_4EC1_963E_E8E0DA84763D__INCLUDED_)
#define AFX_FinanceDetail_H__BDA4309F_DFF8_4EC1_963E_E8E0DA84763D__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// FinanceDetail.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CFinanceDetail form view

#ifndef __AFXEXT_H__
#include <afxext.h>
#endif

class CFinanceDetail : public CFormView
{
public:
	CFinanceDetail(); 
	virtual ~CFinanceDetail();
	// protected constructor used by dynamic creation
protected:
	DECLARE_DYNCREATE(CFinanceDetail)

// Form Data
public:
	//{{AFX_DATA(CFinanceDetail)
	enum { IDD = IDD_FINDTL_VIEW };
		// NOTE: the ClassWizard will add data members here
	//}}AFX_DATA

// Attributes
public:

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CFinanceDetail)
	public:
	virtual BOOL Create(LPCTSTR lpszClassName, LPCTSTR lpszWindowName, DWORD dwStyle, const RECT& rect, CWnd* pParentWnd, UINT nID, CCreateContext* pContext = NULL);
	virtual void OnInitialUpdate();
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

	// Generated message map functions
	//{{AFX_MSG(CFinanceDetail)
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_FinanceDetail_H__BDA4309F_DFF8_4EC1_963E_E8E0DA84763D__INCLUDED_)
