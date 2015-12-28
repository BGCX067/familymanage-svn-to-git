#ifndef __MATRIX_H__
#define __MATRIX_H__

#include <GLES/gl.h>
#include <GLES/glext.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Matrix math utilities. These methods operate on OpenGL ES format
 * matrices and vectors stored in float arrays.
 *
 * Matrices are 4 x 4 column-vector matrices stored in column-major
 * order:
 * <pre>
 *  m[offset +  0] m[offset +  4] m[offset +  8] m[offset + 12]
 *  m[offset +  1] m[offset +  5] m[offset +  9] m[offset + 13]
 *  m[offset +  2] m[offset +  6] m[offset + 10] m[offset + 14]
 *  m[offset +  3] m[offset +  7] m[offset + 11] m[offset + 15]
 * </pre>
 *
 * Vectors are 4 row x 1 column column-vectors stored in order:
 * <pre>
 * v[offset + 0]
 * v[offset + 1]
 * v[offset + 2]
 * v[offset + 3]
 * </pre>
 *
 */
class Matrix {

    /**
     * Transposes a 4 x 4 matrix.
     *
     * @param mTrans the array that holds the output inverted matrix
     * @param mTransOffset an offset into mInv where the inverted matrix is
     *        stored.
     * @param m the input array
     * @param mOffset an offset into m where the matrix is stored.
     */
public:

	static void multiplyMM(GLfloat* result, GLint resultOffset,
			GLfloat* lhs, GLint lhsOffset, GLfloat* rhs, GLint rhsOffset);

	static void multiplyMV(GLfloat* resultVec,
			GLint resultVecOffset, GLfloat* lhsMat, GLint lhsMatOffset,
	            GLfloat* rhsVec, GLint rhsVecOffset);

    static void transposeM(GLfloat* mTrans, GLint mTransOffset, GLfloat* m,
    		GLint mOffset);

    /**
     * Inverts a 4 x 4 matrix.
     *
     * @param mInv the array that holds the output inverted matrix
     * @param mInvOffset an offset into mInv where the inverted matrix is
     *        stored.
     * @param m the input array
     * @param mOffset an offset into m where the matrix is stored.
     * @return true if the matrix could be inverted, false if it could not.
     */
    static GLint invertM(GLfloat* mInv, GLint mInvOffset, GLfloat* m,
            GLint mOffset);

    /**
     * Computes an orthographic projection matrix.
     *
     * @param m returns the result
     * @param mOffset
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     */
    static void orthoM(GLfloat* m, GLint mOffset,
        GLfloat left, GLfloat right, GLfloat bottom, GLfloat top,
        GLfloat near, GLfloat far);


    /**
     * Define a projection matrix in terms of six clip planes
     * @param m the float array that holds the perspective matrix
     * @param offset the offset into float array m where the perspective
     * matrix data is written
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     */

    static void frustumM(GLfloat* m, GLint offset,
            GLfloat left, GLfloat right, GLfloat bottom, GLfloat top,
            GLfloat near, GLfloat far);

    /**
     * Computes the length of a vector
     *
     * @param x x coordinate of a vector
     * @param y y coordinate of a vector
     * @param z z coordinate of a vector
     * @return the length of a vector
     */
    static GLfloat length(GLfloat x, GLfloat y, GLfloat z);

    /**
     * Sets matrix m to the identity matrix.
     * @param sm returns the result
     * @param smOffset index into sm where the result matrix starts
     */
    static void setIdentityM(GLfloat* sm, GLint smOffset);

    /**
     * Scales matrix  m by x, y, and z, putting the result in sm
     * @param sm returns the result
     * @param smOffset index into sm where the result matrix starts
     * @param m source matrix
     * @param mOffset index into m where the source matrix starts
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    static void scaleM(GLfloat* sm, GLint smOffset,
            GLfloat* m, GLint mOffset,
            GLfloat x, GLfloat y, GLfloat z);

    /**
     * Scales matrix m in place by sx, sy, and sz
     * @param m matrix to scale
     * @param mOffset index into m where the matrix starts
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    static void scaleM(GLfloat* m, GLint mOffset,
            GLfloat x, GLfloat y, GLfloat z);

    /**
     * Translates matrix m by x, y, and z, putting the result in tm
     * @param tm returns the result
     * @param tmOffset index into sm where the result matrix starts
     * @param m source matrix
     * @param mOffset index into m where the source matrix starts
     * @param x translation factor x
     * @param y translation factor y
     * @param z translation factor z
     */
    static void translateM(GLfloat* tm, GLint tmOffset,
            GLfloat* m, GLint mOffset,
            GLfloat x, GLfloat y, GLfloat z);

    /**
     * Translates matrix m by x, y, and z in place.
     * @param m matrix
     * @param mOffset index into m where the matrix starts
     * @param x translation factor x
     * @param y translation factor y
     * @param z translation factor z
     */
    static void translateM(
            GLfloat* m, GLint mOffset,
            GLfloat x, GLfloat y, GLfloat z);

    /**
     * Rotates matrix m by angle a (in degrees) around the axis (x, y, z)
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param m source matrix
     * @param mOffset index into m where the source matrix starts
     * @param a angle to rotate in degrees
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    static void rotateM(GLfloat* rm, GLint rmOffset,
            GLfloat* m, GLint mOffset,
            GLfloat a, GLfloat x, GLfloat y, GLfloat z);

    /**
     * Rotates matrix m in place by angle a (in degrees)
     * around the axis (x, y, z)
     * @param m source matrix
     * @param mOffset index into m where the matrix starts
     * @param a angle to rotate in degrees
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    static void rotateM(GLfloat* m, GLint mOffset,
            GLfloat a, GLfloat x, GLfloat y, GLfloat z);

    /**
     * Rotates matrix m by angle a (in degrees) around the axis (x, y, z)
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param a angle to rotate in degrees
     * @param x scale factor x
     * @param y scale factor y
     * @param z scale factor z
     */
    static void setRotateM(GLfloat* rm, GLint rmOffset,
            GLfloat a, GLfloat x, GLfloat y, GLfloat z);

    /**
     * Converts Euler angles to a rotation matrix
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param x angle of rotation, in degrees
     * @param y angle of rotation, in degrees
     * @param z angle of rotation, in degrees
     */
    static void setRotateEulerM(GLfloat* rm, GLint rmOffset,
    		GLfloat x, GLfloat y, GLfloat z);

    /**
     * Define a viewing transformation in terms of an eye point, a center of
     * view, and an up vector.
     *
     * @param rm returns the result
     * @param rmOffset index into rm where the result matrix starts
     * @param eyeX eye point X
     * @param eyeY eye point Y
     * @param eyeZ eye point Z
     * @param centerX center of view X
     * @param centerY center of view Y
     * @param centerZ center of view Z
     * @param upX up vector X
     * @param upY up vector Y
     * @param upZ up vector Z
     */
    static void setLookAtM(GLfloat* rm, GLint rmOffset,
    		GLfloat eyeX, GLfloat eyeY, GLfloat eyeZ,
    		GLfloat centerX, GLfloat centerY, GLfloat centerZ, GLfloat upX, GLfloat upY,
    		GLfloat upZ);
};

#ifdef __cplusplus
}
#endif

#endif
