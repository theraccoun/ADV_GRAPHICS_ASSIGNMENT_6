package com.maccoun;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;

public class MyActivity extends Activity
{

    private HelloOpenglSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new HelloOpenglSurfaceView(this);


        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        Button bStructArray = new Button(this);
        bStructArray.setText("StructArray vs ArrayStruct");
        Button bRandom = new Button(this);
        bRandom.setText("Random vs NonRandom TexGen");

        bRandom.setOnClickListener(new View.OnClickListener() {

            private int counter = 0;

            public void onClick(View v) {
                counter = (counter+1)%2;

                if(counter==1){

                       mGLView.getRenderer().setIsTexShader(true);
                }
                else{
                    mGLView.getRenderer().setIsTexShader(false);
                }

            }
        });

        ll.addView(bRandom);
        ll.addView(bStructArray);
        ll.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        ll.addView(mGLView);

        setContentView(ll);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}

class HelloOpenglSurfaceView extends GLSurfaceView {

    private GLES20TriangleRenderer gtr;

    public HelloOpenglSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        // Set the Renderer for drawing on the GLSurfaceView
        gtr = new GLES20TriangleRenderer(context);
        setRenderer(gtr);
    }

    public GLES20TriangleRenderer getRenderer(){
        return gtr;
    }



}
