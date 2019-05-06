package com.liu.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TriggerRender implements GLSurfaceView.Renderer {
//        private float triagnlecoords[] = {
//                0.5f, 0.5f, 0.0f, // top
//                -0.5f, -0.5f, 0.0f, // bottom left
//                0.5f, -0.5f, 0.0f  // bottom right
//    };
    private float triagnlecoords[] = {
            0.0f, 1.0f, 0.0f,
            0.86f, 0.5f, 0.0f, // top
            0.86f, -0.5f, 0.0f, // bottom left
            0.0f, -1.0f, 0.0f,// bottom right
            -0.86f,-0.5f, 0.0f,
            -0.86f, 0.5f, 0.0f
    };

    private float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    private FloatBuffer vertexBuffr;

    private int mProgram;
    private final int COORDS_PER_VERTEX = 3;

    //顶点个数
    private final int vertexCount = createPositions(10).length / COORDS_PER_VERTEX;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        vertexBuffr = BufferUtil.fBuffer(createPositions(10));

        String vertexShadercode = "attribute vec4 vPosition;" +
                "uniform mat4 vMatrix;" +
                "void main(){" +
                "gl_Position = vMatrix*vPosition;" +
                "}";
//         String vertexShadercode =
//                "attribute vec4 vPosition;" +
//                        "void main() {" +
//                        "  gl_Position = vPosition;" +
//                        "}";
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShadercode);

        String fragmentsShaderCode = "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main(){" +
                "gl_FragColor = vColor;" +
                "}";
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentsShaderCode);
        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        GLES20.glViewport(0, 0, width, height);
        //计算宽高比
        float ratio = (float) width / height;
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取变换矩阵vMatrix成员句柄
        int mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);

        //获取顶点着色器的vPosition成员句柄
        int mPostionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPostionHandle);
        //顶点之间的偏移量
        int vertexStride = COORDS_PER_VERTEX * 4;
        GLES20.glVertexAttribPointer(mPostionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffr);
        //获取片元着色器的vColor成员的句柄
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPostionHandle);
    }


    //    Face detection based on Face++
    private int loadShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    /**
     * 半径为1.0f
     * 或者说是中心点到各顶点的距离
     */
    float radius = 1.0f;

    /**
     * 根据自定义边数来绘制图像
     * @param n
     * @return
     */
    private float[]  createPositions(int n){
        ArrayList<Float> data=new ArrayList<>();
        data.add(0.0f);             //设置圆心坐标
        data.add(0.0f);
        data.add(0.0f);
        float angDegSpan=360f/n;
        for(float i=0;i<360+angDegSpan;i+=angDegSpan){
            data.add((float) (radius*Math.sin(i*Math.PI/180f)));
            data.add((float)(radius*Math.cos(i*Math.PI/180f)));
            data.add(0.0f);
            Log.e("---------", String.valueOf(i) );
        }
        float[] f=new float[data.size()];
        for (int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }
        return f;
    }
}
