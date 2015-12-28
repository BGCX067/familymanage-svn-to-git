#include "Matrix.h"
#include <jni.h>
#include <android/log.h>
#include <math.h>
#include <stdio.h>
#include <string.h>

#define  LOG_TAG    "libgljni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define PI 3.141592653589793
#define I(_i, _j) ((_j)+ 4*(_i))

	void  Matrix::multiplyMM(GLfloat* result, GLint resultOffset,
			GLfloat* lhs, GLint lhsOffset, GLfloat* rhs, GLint rhsOffset)
	{
		GLfloat* result1 = result + resultOffset;
		GLfloat* lhs1 = lhs + lhsOffset;
		GLfloat* rhs1 = rhs + rhsOffset;
		for (GLint i=0 ; i<4 ; i++) {
			register const GLfloat rhs_i0 = rhs1[ I(i,0) ];
			register GLfloat ri0 = lhs1[ I(0,0) ] * rhs_i0;
			register GLfloat ri1 = lhs1[ I(0,1) ] * rhs_i0;
			register GLfloat ri2 = lhs1[ I(0,2) ] * rhs_i0;
			register GLfloat ri3 = lhs1[ I(0,3) ] * rhs_i0;
			for (GLint j=1 ; j<4 ; j++) {
				register const GLfloat rhs_ij = rhs1[ I(i,j) ];
				ri0 += lhs1[ I(j,0) ] * rhs_ij;
				ri1 += lhs1[ I(j,1) ] * rhs_ij;
				ri2 += lhs1[ I(j,2) ] * rhs_ij;
				ri3 += lhs1[ I(j,3) ] * rhs_ij;
			}
			result1[ I(i,0) ] = ri0;
			result1[ I(i,1) ] = ri1;
			result1[ I(i,2) ] = ri2;
			result1[ I(i,3) ] = ri3;
		}
	}

	void  Matrix::multiplyMV(GLfloat* resultVec,
			GLint resultVecOffset, GLfloat* lhsMat, GLint lhsMatOffset,
	            GLfloat* rhsVec, GLint rhsVecOffset)
	{
		GLfloat* rhsVec1 = rhsVec + rhsVecOffset;
		GLfloat* lhsMat1 = lhsMat + lhsMatOffset;
		GLfloat* resultVec1 = resultVec + resultVecOffset;
		GLfloat x = rhsVec1[0];
		GLfloat y = rhsVec1[1];
		GLfloat z = rhsVec1[2];
		GLfloat w = rhsVec1[3];
		resultVec1[0] = lhsMat1[0 + 4 * 0] * x + lhsMat1[0 + 4 * 1] * y + lhsMat1[0 + 4 * 2] * z + lhsMat1[0 + 4 * 3] * w;
		resultVec1[1] = lhsMat1[1 + 4 * 0] * x + lhsMat1[1 + 4 * 1] * y + lhsMat1[1 + 4 * 2] * z + lhsMat1[1 + 4 * 3] * w;
		resultVec1[2] = lhsMat1[2 + 4 * 0] * x + lhsMat1[2 + 4 * 1] * y + lhsMat1[2 + 4 * 2] * z + lhsMat1[2 + 4 * 3] * w;
		resultVec1[3] = lhsMat1[3 + 4 * 0] * x + lhsMat1[3 + 4 * 1] * y + lhsMat1[3 + 4 * 2] * z + lhsMat1[3 + 4 * 3] * w;
	}

    void  Matrix::transposeM(GLfloat* mTrans, GLint mTransOffset, GLfloat* m,
    		GLint mOffset)
    {
    	for (GLint i = 0; i < 4; i++)
    	{
    		GLint mBase = i * 4 + mOffset;
    	    mTrans[i + mTransOffset] = m[mBase];
    	    mTrans[i + 4 + mTransOffset] = m[mBase + 1];
    	    mTrans[i + 8 + mTransOffset] = m[mBase + 2];
    	    mTrans[i + 12 + mTransOffset] = m[mBase + 3];
    	}
    }

    GLint  Matrix::invertM(GLfloat* mInv, GLint mInvOffset, GLfloat* m,
            GLint mOffset)
    {
    	GLfloat src[16] = {0.0f};

    	// transpose matrix
    	transposeM(src, 0, m, mOffset);

    	// temp array for pairs
    	GLfloat tmp[12] = {0.0f};

    	// calculate pairs for first 8 elements (cofactors)
    	tmp[0] = src[10] * src[15];
    	tmp[1] = src[11] * src[14];
    	tmp[2] = src[9] * src[15];
    	tmp[3] = src[11] * src[13];
    	tmp[4] = src[9] * src[14];
    	tmp[5] = src[10] * src[13];
    	tmp[6] = src[8] * src[15];
    	tmp[7] = src[11] * src[12];
    	tmp[8] = src[8] * src[14];
    	tmp[9] = src[10] * src[12];
    	tmp[10] = src[8] * src[13];
    	tmp[11] = src[9] * src[12];

    	// Holds the destination matrix while we're building it up.
    	GLfloat dst[16] = {0.0f};

    	// calculate first 8 elements (cofactors)
    	dst[0] = tmp[0] * src[5] + tmp[3] * src[6] + tmp[4] * src[7];
    	dst[0] -= tmp[1] * src[5] + tmp[2] * src[6] + tmp[5] * src[7];
    	dst[1] = tmp[1] * src[4] + tmp[6] * src[6] + tmp[9] * src[7];
    	dst[1] -= tmp[0] * src[4] + tmp[7] * src[6] + tmp[8] * src[7];
    	dst[2] = tmp[2] * src[4] + tmp[7] * src[5] + tmp[10] * src[7];
    	dst[2] -= tmp[3] * src[4] + tmp[6] * src[5] + tmp[11] * src[7];
    	dst[3] = tmp[5] * src[4] + tmp[8] * src[5] + tmp[11] * src[6];
    	dst[3] -= tmp[4] * src[4] + tmp[9] * src[5] + tmp[10] * src[6];
    	dst[4] = tmp[1] * src[1] + tmp[2] * src[2] + tmp[5] * src[3];
    	dst[4] -= tmp[0] * src[1] + tmp[3] * src[2] + tmp[4] * src[3];
    	dst[5] = tmp[0] * src[0] + tmp[7] * src[2] + tmp[8] * src[3];
    	dst[5] -= tmp[1] * src[0] + tmp[6] * src[2] + tmp[9] * src[3];
    	dst[6] = tmp[3] * src[0] + tmp[6] * src[1] + tmp[11] * src[3];
    	dst[6] -= tmp[2] * src[0] + tmp[7] * src[1] + tmp[10] * src[3];
    	dst[7] = tmp[4] * src[0] + tmp[9] * src[1] + tmp[10] * src[2];
    	dst[7] -= tmp[5] * src[0] + tmp[8] * src[1] + tmp[11] * src[2];

    	// calculate pairs for second 8 elements (cofactors)
    	tmp[0] = src[2] * src[7];
    	tmp[1] = src[3] * src[6];
    	tmp[2] = src[1] * src[7];
    	tmp[3] = src[3] * src[5];
    	tmp[4] = src[1] * src[6];
    	tmp[5] = src[2] * src[5];
    	tmp[6] = src[0] * src[7];
    	tmp[7] = src[3] * src[4];
    	tmp[8] = src[0] * src[6];
    	tmp[9] = src[2] * src[4];
    	tmp[10] = src[0] * src[5];
    	tmp[11] = src[1] * src[4];

    	// calculate second 8 elements (cofactors)
    	dst[8] = tmp[0] * src[13] + tmp[3] * src[14] + tmp[4] * src[15];
    	dst[8] -= tmp[1] * src[13] + tmp[2] * src[14] + tmp[5] * src[15];
    	dst[9] = tmp[1] * src[12] + tmp[6] * src[14] + tmp[9] * src[15];
    	dst[9] -= tmp[0] * src[12] + tmp[7] * src[14] + tmp[8] * src[15];
    	dst[10] = tmp[2] * src[12] + tmp[7] * src[13] + tmp[10] * src[15];
    	dst[10] -= tmp[3] * src[12] + tmp[6] * src[13] + tmp[11] * src[15];
    	dst[11] = tmp[5] * src[12] + tmp[8] * src[13] + tmp[11] * src[14];
    	dst[11] -= tmp[4] * src[12] + tmp[9] * src[13] + tmp[10] * src[14];
    	dst[12] = tmp[2] * src[10] + tmp[5] * src[11] + tmp[1] * src[9];
    	dst[12] -= tmp[4] * src[11] + tmp[0] * src[9] + tmp[3] * src[10];
    	dst[13] = tmp[8] * src[11] + tmp[0] * src[8] + tmp[7] * src[10];
    	dst[13] -= tmp[6] * src[10] + tmp[9] * src[11] + tmp[1] * src[8];
    	dst[14] = tmp[6] * src[9] + tmp[11] * src[11] + tmp[3] * src[8];
    	dst[14] -= tmp[10] * src[11] + tmp[2] * src[8] + tmp[7] * src[9];
    	dst[15] = tmp[10] * src[10] + tmp[4] * src[8] + tmp[9] * src[9];
    	dst[15] -= tmp[8] * src[9] + tmp[11] * src[10] + tmp[5] * src[8];

    	// calculate determinant
    	GLfloat det = src[0] * dst[0] + src[1] * dst[1] + src[2] * dst[2] + src[3] * dst[3];

    	if (det == 0.0f) {

    	}

    	// calculate matrix inverse
    	det = 1 / det;
    	for (GLint j = 0; j < 16; j++)
    	    mInv[j + mInvOffset] = dst[j] * det;

    	return 1;
    }

    void  Matrix::orthoM(GLfloat* m, GLint mOffset,
        GLfloat left, GLfloat right, GLfloat bottom, GLfloat top,
        GLfloat near, GLfloat far)
    {
    	if (left == right) {
    	            //throw new IllegalArgumentException("left == right");
    		return;
    	}
    	if (bottom == top) {
    	            //throw new IllegalArgumentException("bottom == top");
    		return;
    	}
    	if (near == far) {
            //throw new IllegalArgumentException("bottom == top");
    		return;
    	}

    	const GLfloat r_width  = 1.0f / (right - left);
    	const GLfloat r_height = 1.0f / (top - bottom);
    	const GLfloat r_depth  = 1.0f / (far - near);
    	const GLfloat x =  2.0f * (r_width);
    	const GLfloat y =  2.0f * (r_height);
    	const GLfloat z = -2.0f * (r_depth);
    	const GLfloat tx = -(right + left) * r_width;
    	const GLfloat ty = -(top + bottom) * r_height;
    	const GLfloat tz = -(far + near) * r_depth;
    	m[mOffset + 0] = x;
    	m[mOffset + 5] = y;
    	m[mOffset +10] = z;
    	m[mOffset +12] = tx;
    	m[mOffset +13] = ty;
    	m[mOffset +14] = tz;
    	m[mOffset +15] = 1.0f;
    	m[mOffset + 1] = 0.0f;
    	m[mOffset + 2] = 0.0f;
    	m[mOffset + 3] = 0.0f;
    	m[mOffset + 4] = 0.0f;
    	m[mOffset + 6] = 0.0f;
    	m[mOffset + 7] = 0.0f;
    	m[mOffset + 8] = 0.0f;
    	m[mOffset + 9] = 0.0f;
    	m[mOffset + 11] = 0.0f;
    }

    void  Matrix::frustumM(GLfloat* m, GLint offset,
            GLfloat left, GLfloat right, GLfloat bottom, GLfloat top,
            GLfloat near, GLfloat far)
    {
    	if (left == right) {
    	            //throw new IllegalArgumentException("left == right");
    		return;
    	}
    	if (top == bottom) {
    	            //throw new IllegalArgumentException("top == bottom");
    		return;
    	}
    	if (near == far) {
    	            //throw new IllegalArgumentException("near == far");
    		return;
    	}
    	if (near <= 0.0f) {
    	            //throw new IllegalArgumentException("near <= 0.0f");
    		return;
    	}
    	if (far <= 0.0f) {
    	            //throw new IllegalArgumentException("far <= 0.0f");
    		return;
    	}
    	const GLfloat r_width  = 1.0f / (right - left);
    	const GLfloat r_height = 1.0f / (top - bottom);
    	const GLfloat r_depth  = 1.0f / (near - far);
    	const GLfloat x = 2.0f * (near * r_width);
    	const GLfloat y = 2.0f * (near * r_height);
    	const GLfloat A = 2.0f * ((right + left) * r_width);
    	const GLfloat B = (top + bottom) * r_height;
    	const GLfloat C = (far + near) * r_depth;
    	const GLfloat D = 2.0f * (far * near * r_depth);
    	m[offset + 0] = x;
    	m[offset + 5] = y;
    	m[offset + 8] = A;
    	m[offset +  9] = B;
    	m[offset + 10] = C;
    	m[offset + 14] = D;
    	m[offset + 11] = -1.0f;
    	m[offset +  1] = 0.0f;
    	m[offset +  2] = 0.0f;
    	m[offset +  3] = 0.0f;
    	m[offset +  4] = 0.0f;
    	m[offset +  6] = 0.0f;
    	m[offset +  7] = 0.0f;
    	m[offset + 12] = 0.0f;
    	m[offset + 13] = 0.0f;
    	m[offset + 15] = 0.0f;
    }

    GLfloat  Matrix::length(GLfloat x, GLfloat y, GLfloat z)
    {
    	return (GLfloat) sqrt(x * x + y * y + z * z);
    }

    void  Matrix::setIdentityM(GLfloat* sm, GLint smOffset)
    {
    	for (GLint i=0 ; i<16 ; i++) {
    	   sm[smOffset + i] = 0;
    	}
    	for(GLint i = 0; i < 16; i += 5) {
    	   sm[smOffset + i] = 1.0f;
    	}
    }

    void  Matrix::scaleM(GLfloat* sm, GLint smOffset,
            GLfloat* m, GLint mOffset,
            GLfloat x, GLfloat y, GLfloat z)
    {
    	for (GLint i=0 ; i<4 ; i++) {
    		GLint smi = smOffset + i;
    		GLint mi = mOffset + i;
			sm[     smi] = m[     mi] * x;
			sm[ 4 + smi] = m[ 4 + mi] * y;
			sm[ 8 + smi] = m[ 8 + mi] * z;
			sm[12 + smi] = m[12 + mi];
    	}
    }

    void  Matrix::scaleM(GLfloat* m, GLint mOffset,
            GLfloat x, GLfloat y, GLfloat z)
    {
    	for (GLint i=0 ; i<4 ; i++) {
    		GLint mi = mOffset + i;
    	    m[     mi] *= x;
    	    m[ 4 + mi] *= y;
    	    m[ 8 + mi] *= z;
    	}
    }

    void  Matrix::translateM(GLfloat* tm, GLint tmOffset,
            GLfloat* m, GLint mOffset,
            GLfloat x, GLfloat y, GLfloat z)
    {
    	for (GLint i=0 ; i<12 ; i++) {
    	    tm[tmOffset + i] = m[mOffset + i];
    	}
    	for (GLint i=0 ; i<4 ; i++) {
    		GLint tmi = tmOffset + i;
    		GLint mi = mOffset + i;
    	    tm[12 + tmi] = m[mi] * x + m[4 + mi] * y + m[8 + mi] * z +
    	    m[12 + mi];
    	}
    }

    void  Matrix::translateM(
            GLfloat* m, GLint mOffset,
            GLfloat x, GLfloat y, GLfloat z)
    {
    	for (GLint i=0 ; i<4 ; i++) {
    		GLint mi = mOffset + i;
    	    m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
    	}
    }

    void  Matrix::setRotateM(GLfloat* rm, GLint rmOffset,
            GLfloat a, GLfloat x, GLfloat y, GLfloat z)
    {
    	rm[rmOffset + 3] = 0;
    	rm[rmOffset + 7] = 0;
    	rm[rmOffset + 11]= 0;
    	rm[rmOffset + 12]= 0;
    	rm[rmOffset + 13]= 0;
    	rm[rmOffset + 14]= 0;
    	rm[rmOffset + 15]= 1;
    	a *= (GLfloat) (PI / 180.0f);
    	GLfloat s = (GLfloat) sin(a);
    	GLfloat c = (GLfloat) cos(a);
    	if (1.0f == x && 0.0f == y && 0.0f == z) {
    		rm[rmOffset + 5] = c;   rm[rmOffset + 10]= c;
    	    rm[rmOffset + 6] = s;   rm[rmOffset + 9] = -s;
    	    rm[rmOffset + 1] = 0;   rm[rmOffset + 2] = 0;
    	    rm[rmOffset + 4] = 0;   rm[rmOffset + 8] = 0;
    	    rm[rmOffset + 0] = 1;
    	} else if (0.0f == x && 1.0f == y && 0.0f == z) {
    	    rm[rmOffset + 0] = c;   rm[rmOffset + 10]= c;
    	    rm[rmOffset + 8] = s;   rm[rmOffset + 2] = -s;
    	    rm[rmOffset + 1] = 0;   rm[rmOffset + 4] = 0;
    	    rm[rmOffset + 6] = 0;   rm[rmOffset + 9] = 0;
    	    rm[rmOffset + 5] = 1;
    	} else if (0.0f == x && 0.0f == y && 1.0f == z) {
    	    rm[rmOffset + 0] = c;   rm[rmOffset + 5] = c;
    	    rm[rmOffset + 1] = s;   rm[rmOffset + 4] = -s;
    	    rm[rmOffset + 2] = 0;   rm[rmOffset + 6] = 0;
    	    rm[rmOffset + 8] = 0;   rm[rmOffset + 9] = 0;
    	    rm[rmOffset + 10]= 1;
    	} else {
    		GLfloat len = length(x, y, z);
    	    if (1.0f != len) {
    	    	GLfloat recipLen = 1.0f / len;
    	        x *= recipLen;
    	        y *= recipLen;
    	        z *= recipLen;
    	    }
    	    GLfloat nc = 1.0f - c;
    	    GLfloat xy = x * y;
    	    GLfloat yz = y * z;
    	    GLfloat zx = z * x;
    	    GLfloat xs = x * s;
    	    GLfloat ys = y * s;
    	    GLfloat zs = z * s;
    	    rm[rmOffset +  0] = x*x*nc +  c;
    	    rm[rmOffset +  4] =  xy*nc - zs;
    	    rm[rmOffset +  8] =  zx*nc + ys;
    	    rm[rmOffset +  1] =  xy*nc + zs;
    	    rm[rmOffset +  5] = y*y*nc +  c;
    	    rm[rmOffset +  9] =  yz*nc - xs;
    	    rm[rmOffset +  2] =  zx*nc - ys;
    	    rm[rmOffset +  6] =  yz*nc + xs;
    	    rm[rmOffset + 10] = z*z*nc +  c;
    	}
    }

    void  Matrix::rotateM(GLfloat* rm, GLint rmOffset,
                GLfloat* m, GLint mOffset,
                GLfloat a, GLfloat x, GLfloat y, GLfloat z)
        {
        	GLfloat r[16] = {0.0f};
        	setRotateM(r, 0, a, x, y, z);
        	multiplyMM(rm, rmOffset, m, mOffset, r, 0);
        }

        void  Matrix::rotateM(GLfloat* m, GLint mOffset,
                GLfloat a, GLfloat x, GLfloat y, GLfloat z)
        {
        	GLfloat temp[32] = {0.0f};
        	setRotateM(temp, 0, a, x, y, z);
        	multiplyMM(temp, 16, m, mOffset, temp, 0);
        	memcpy((m + mOffset), (temp + 16), 16 * sizeof(GLfloat));
        }

    void  Matrix::setRotateEulerM(GLfloat* rm, GLint rmOffset,
    		GLfloat x, GLfloat y, GLfloat z)
    {
    	x *= (GLfloat) (PI / 180.0f);
    	y *= (GLfloat) (PI / 180.0f);
    	z *= (GLfloat) (PI / 180.0f);
    	GLfloat cx = (GLfloat) cos(x);
    	GLfloat sx = (GLfloat) sin(x);
    	GLfloat cy = (GLfloat) cos(y);
    	GLfloat sy = (GLfloat) sin(y);
    	GLfloat cz = (GLfloat) cos(z);
    	GLfloat sz = (GLfloat) sin(z);
    	GLfloat cxsy = cx * sy;
    	GLfloat sxsy = sx * sy;

    	rm[rmOffset + 0]  =   cy * cz;
    	rm[rmOffset + 1]  =  -cy * sz;
    	rm[rmOffset + 2]  =   sy;
    	rm[rmOffset + 3]  =  0.0f;

    	rm[rmOffset + 4]  =  cxsy * cz + cx * sz;
    	rm[rmOffset + 5]  = -cxsy * sz + cx * cz;
    	rm[rmOffset + 6]  =  -sx * cy;
    	rm[rmOffset + 7]  =  0.0f;

    	rm[rmOffset + 8]  = -sxsy * cz + sx * sz;
    	rm[rmOffset + 9]  =  sxsy * sz + sx * cz;
    	rm[rmOffset + 10] =  cx * cy;
    	rm[rmOffset + 11] =  0.0f;

    	rm[rmOffset + 12] =  0.0f;
    	rm[rmOffset + 13] =  0.0f;
    	rm[rmOffset + 14] =  0.0f;
    	rm[rmOffset + 15] =  1.0f;
    }

    void  Matrix::setLookAtM(GLfloat* rm, GLint rmOffset,
    		GLfloat eyeX, GLfloat eyeY, GLfloat eyeZ,
    		GLfloat centerX, GLfloat centerY, GLfloat centerZ, GLfloat upX, GLfloat upY,
    		GLfloat upZ)
    {
    	// See the OpenGL GLUT documentation for gluLookAt for a description
    	        // of the algorithm. We implement it in a straightforward way:

    	GLfloat fx = centerX - eyeX;
    	GLfloat fy = centerY - eyeY;
    	GLfloat fz = centerZ - eyeZ;

    	// Normalize f
    	GLfloat rlf = 1.0f / length(fx, fy, fz);
    	fx *= rlf;
    	fy *= rlf;
    	fz *= rlf;

    	// compute s = f x up (x means "cross product")
    	GLfloat sx = fy * upZ - fz * upY;
    	GLfloat sy = fz * upX - fx * upZ;
    	GLfloat sz = fx * upY - fy * upX;

    	        // and normalize s
    	GLfloat rls = 1.0f / length(sx, sy, sz);
    	sx *= rls;
    	sy *= rls;
    	sz *= rls;

    	// compute u = s x f
    	GLfloat ux = sy * fz - sz * fy;
    	GLfloat uy = sz * fx - sx * fz;
    	GLfloat uz = sx * fy - sy * fx;

    	rm[rmOffset + 0] = sx;
    	rm[rmOffset + 1] = ux;
    	rm[rmOffset + 2] = -fx;
    	rm[rmOffset + 3] = 0.0f;

    	rm[rmOffset + 4] = sy;
    	rm[rmOffset + 5] = uy;
    	rm[rmOffset + 6] = -fy;
    	rm[rmOffset + 7] = 0.0f;

    	rm[rmOffset + 8] = sz;
    	rm[rmOffset + 9] = uz;
    	rm[rmOffset + 10] = -fz;
    	rm[rmOffset + 11] = 0.0f;

    	rm[rmOffset + 12] = 0.0f;
    	rm[rmOffset + 13] = 0.0f;
    	rm[rmOffset + 14] = 0.0f;
    	rm[rmOffset + 15] = 1.0f;

    	translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
    }
