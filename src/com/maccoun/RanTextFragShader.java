package com.maccoun;

/**
 * Created by IntelliJ IDEA.
 * User: theraccoun
 * Date: 3/5/12
 * Time: 2:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class RanTextFragShader {

     private static final String fShader =
             "precision mediump float;\n" +
             "uniform float random;\n" +


              "void main() {\n" +
                     "gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
//              "  if(random==1){ \"  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0)}\n"+
//              "  else { gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0)}\n"+

              "}\n";

    public String getFragmentShader(){
        return fShader;
    }
}
