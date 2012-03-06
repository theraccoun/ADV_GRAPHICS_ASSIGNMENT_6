package com.maccoun;

/**
 * Created by IntelliJ IDEA.
 * User: theraccoun
 * Date: 3/6/12
 * Time: 2:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class AlgVertTexShader {

    private static final String vShader =

             "uniform mat4 uMVPMatrix;\n" +
             "attribute vec4 aPosition;\n" +


             "void main() {\n" +
             "  gl_Position = uMVPMatrix * aPosition;\n" +
             "}\n";


    public String getVShader(){
        return vShader;
    }


}
