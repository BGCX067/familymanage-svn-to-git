#if !defined(AFX_ADDOUT_H__DE0B3BFA_12BE_4140_AE3C_6567736E117F__INCLUDED_)
#define AFX_ADDOUT_H__DE0B3BFA_12BE_4140_AE3C_6567736E117F__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// AddOut.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CAddOut form view

#ifndef __AFXEXT_H__
#include <afxext.h>
#endif

class CAddOut : public CFormView
{
protected:
	          // protected constructor used by dynamic creation
	DECLARE_DYNCREATE(CAddOut)

// Form Data
public:
	//{{AFX_DATA(CAddOut)
	enum { IDD = IDD_ADDOUT_VIEW };
	CTime	m_OutDate;
	CTime	m_OutTime;
	CString	m_sPosition;
	CString	m_sDetail;
	int		m_nCategory;
	double	m_dAmount;
	//}}AFX_DATA

// Attributes
public:

// Operations
public:
	CAddOut(); 
	virtual ~CAddOut();
// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CAddOut)
	public:
	virtual BOOL Create(LPCTSTR lpszClassName, LPCTSTR lpszWindowName, DWORD dwStyle, const RECT& rect, CWnd* pParentWnd, UINT nID, CCreateContext* pContext = NULL);
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
	//{{AFX_MSG(CAddOut)
	afx_msg void OnAddButton();
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_ADDOUT_H__DE0B3BFA_12BE_4140_AE3C_6567736E117F__INCLUDED_)
