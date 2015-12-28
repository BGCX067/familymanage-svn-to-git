#if !defined(AFX_TREE1_H__667A1A8B_1F4D_4004_89E3_8C79F1FF36DC__INCLUDED_)
#define AFX_TREE1_H__667A1A8B_1F4D_4004_89E3_8C79F1FF36DC__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// Tree1.h : header file
//
#include "afxcview.h"
/////////////////////////////////////////////////////////////////////////////
// CTree1 view

#include "RightFrm.h"

class CTree1 : public CTreeView
{
protected:
	CTree1();           // protected constructor used by dynamic creation
	DECLARE_DYNCREATE(CTree1)

// Attributes
public:

// Operations
public:

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CTree1)
	public:
	virtual void OnInitialUpdate();
	protected:
	virtual void OnDraw(CDC* pDC);      // overridden to draw this view
	//}}AFX_VIRTUAL

// Implementation
protected:
	virtual ~CTree1();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

	// Generated message map functions
protected:
	//{{AFX_MSG(CTree1)
	afx_msg void OnClick(NMHDR* pNMHDR, LRESULT* pResult);
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

/////////////////////////////////////////////////////////////////////////////

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_TREE1_H__667A1A8B_1F4D_4004_89E3_8C79F1FF36DC__INCLUDED_)
