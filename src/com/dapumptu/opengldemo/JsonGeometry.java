package com.dapumptu.opengldemo;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class JsonGeometry {
    private static final String TAG = "JsonGeometry";

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    private float[] center = new float[3];
    private float vertexData[];
    private float tempNormalData[];
    private short indexData[];

    private int vertexCount = 0;
    private int indexCount = 0;
    private float scale = 1.0f;

    public float getScale() {
        return scale;
    }

    public float getCenterX() {
        return center[0];
    }

    public float getCenterY() {
        return center[1];
    }

    public float getCenterZ() {
        return center[2];
    }

    public JsonGeometry() {
    }

    public void draw(int positionHandle, int textureUvHandle, int normalHandle) {

        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, GLESConstants.COORDS_PER_POSITION,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        vertexBuffer.position(vertexCount * GLESConstants.COORDS_PER_NORMAL);
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(normalHandle, GLESConstants.COORDS_PER_NORMAL,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(textureUvHandle);
    }

    public void initFromJson(String jsonString) {

        JSONObject jObject = null;
        try {
            jObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jObject != null) {
            try {
                scale = (float) jObject.getDouble("scale");

                JSONArray jArray = jObject.getJSONArray("vertices");
                vertexCount = jArray.length() / 3;
                vertexData = new float[vertexCount * 3 * 2];
                for (int i = 0; i < jArray.length(); i++) {
                    double vertexValue = jArray.getDouble(i);
                    vertexData[i] = (float) vertexValue;
                    //Log.d(TAG, String.valueOf(vertexData));
                }
                Log.d(TAG, vertexCount + " vertices");

                jArray = jObject.getJSONArray("normals");
                tempNormalData = new float[vertexCount * 3];
                for (int i = 0; i < jArray.length(); i++) {
                    double normalData = jArray.getDouble(i);
                    tempNormalData[i] = (float) normalData;
                    //Log.d(TAG, String.valueOf(vertexData));
                }
                Log.d(TAG, jArray.length() + " normals");

                parseFaceData(jObject);

                //computeFaceNormals();
                //computeCenter();

            } catch (JSONException e) {
                // Oops
            }
        }

        // Init buffers
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                vertexData.length * GLESConstants.SIZE_FLOAT_BYTES);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexData);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);


        bb = ByteBuffer.allocateDirect(
                indexData.length * GLESConstants.SIZE_SHORT_BYTES);
        bb.order(ByteOrder.nativeOrder());

        indexBuffer = bb.asShortBuffer();
        indexBuffer.put(indexData);
        indexBuffer.position(0);

    }

    private void parseFaceData(JSONObject jObject) throws JSONException {
        JSONArray jArray = jObject.getJSONArray("faces");
        indexCount = jArray.length();
        indexData = new short[indexCount];

        Log.d(TAG, jArray.length() + " jarray");

        int indexCounter = 0;
        int normalCounter = 0;
        for (int offset = 0; offset < jArray.length();) {
            int faceData = jArray.getInt(offset++);
            //indexData[i] = (short) faceData;

            boolean isQuad = isBitSet(faceData, 0);
            boolean hasMaterial = isBitSet(faceData, 1);
            boolean hasFaceUv = isBitSet(faceData, 2);
            boolean hasFaceVertexUv = isBitSet(faceData, 3);
            boolean hasFaceNormal = isBitSet(faceData, 4);
            boolean hasFaceVertexNormal = isBitSet(faceData, 5);
            boolean hasFaceColor = isBitSet(faceData, 6);
            boolean hasFaceVertexColor = isBitSet(faceData, 7);

//                        String debugOutput = "";
//            debugOutput += "isQuad:" + ((isQuad) ? "yes" : "no") + ", ";
//            debugOutput += "hasMaterial:" + ((hasMaterial) ? "yes" : "no") + ", ";
//            debugOutput += "hasFaceVertexUv:" + ((hasFaceVertexUv) ? "yes" : "no") + ", ";
//            debugOutput += "hasFaceNormal:" + ((hasFaceNormal) ? "yes" : "no") + ", ";
//            debugOutput += "hasFaceVertexNormal:" + ((hasFaceVertexNormal) ? "yes" : "no") + ", ";
//            debugOutput += "hasFaceColor:" + ((hasFaceColor) ? "yes" : "no") + ", ";
//            debugOutput += "hasFaceVertexColor:" + ((hasFaceVertexColor) ? "yes" : "no") + ", ";
//            Log.d(TAG, debugOutput);

            int nVertices = 0;
            if (isQuad) {
                indexData[indexCounter++] = (short) jArray.getInt(offset);
                indexData[indexCounter++] = (short) jArray.getInt(offset+1);
                indexData[indexCounter++] = (short) jArray.getInt(offset+3);

                indexData[indexCounter++] = (short) jArray.getInt(offset+1);
                indexData[indexCounter++] = (short) jArray.getInt(offset+2);
                indexData[indexCounter++] = (short) jArray.getInt(offset+3);

                nVertices = 4;

            } else {
                indexData[indexCounter++] = (short) jArray.getInt(offset++);
                indexData[indexCounter++] = (short) jArray.getInt(offset++);
                indexData[indexCounter++] = (short) jArray.getInt(offset++);

                nVertices = 3;
            }

            if (hasMaterial) {
                offset++;
            }

//            if (hasFaceUv) {
//
//                for (i = 0; i < nUvLayers; i++) {
//
//                    uvLayer = json.uvs[i];
//
//                    uvIndex = faces[offset++];
//
//                    u = uvLayer[uvIndex * 2];
//                    v = uvLayer[uvIndex * 2 + 1];
//
//                    scope.faceUvs[i].push(new THREE.UV(u, v));
//
//                }
//
//            }

//            if (hasFaceVertexUv) {
//
//                for (i = 0; i < nUvLayers; i++) {
//
//                    uvLayer = json.uvs[i];
//
//                    uvs =[];
//
//                    for (j = 0; j < nVertices; j++) {
//
//                        uvIndex = faces[offset++];
//
//                        u = uvLayer[uvIndex * 2];
//                        v = uvLayer[uvIndex * 2 + 1];
//
//                        uvs[j] = new THREE.UV(u, v);
//
//                    }
//
//                    scope.faceVertexUvs[i].push(uvs);
//
//                }
//
//            }

            if (hasFaceNormal) {
                int normalIndex = jArray.getInt(offset++) * 3;

                float normal[] = new float[3];
                normal[0] = tempNormalData[normalIndex++];
                normal[1] = tempNormalData[normalIndex++];
                normal[2] = tempNormalData[normalIndex];

                vertexData[vertexCount * 3 + normalCounter++] = normal[0];
                vertexData[vertexCount * 3 + normalCounter++] = normal[1];
                vertexData[vertexCount * 3 + normalCounter++] = normal[2];
            }

            if (hasFaceVertexNormal) {

                for (int i = 0; i < nVertices; i++) {
                    int normalIndex = jArray.getInt(offset++) * 3;

                    float normal[] = new float[3];
                    normal[0] = tempNormalData[normalIndex++];
                    normal[1] = tempNormalData[normalIndex++];
                    normal[2] = tempNormalData[normalIndex];

                    int vertexDataOffset = vertexCount * 3 + indexData[indexCounter - 2 + i] * 3;
                    vertexData[vertexDataOffset] = normal[0];
                    vertexData[vertexDataOffset + 1] = normal[1];
                    vertexData[vertexDataOffset + 2] = normal[2];
                }

            }

            if (hasFaceColor) {
                offset++;
            }

            if (hasFaceVertexColor) {
                for (int i = 0; i < nVertices; i++) {
                    offset++;
                }
            }

        }
        this.indexCount = indexCounter;

        Log.d(TAG, indexCounter / 3 + " faces");
        Log.d(TAG, normalCounter / 3 + " normals");
    }

    private boolean isBitSet(int value, int position) {
        return (value & ( 1 << position )) == 0 ? false : true;
    }

    // TODO
    // http://www.opengl.org/wiki/Calculating_a_Surface_Normal
    private void computeFaceNormals() {

        float cb[] = new float[3];
        float ab[] = new float[3];
        float crossResult[] = new float[3];
        for (int f = 0, fl = indexCount; f < fl; f+=3) {

            float vA[] = new float[3];
            float vB[] = new float[3];
            float vC[] = new float[3];

            int vertexIndexOffset1 = indexData[f];
            int vertexIndexOffset2 = indexData[f+1];
            int vertexIndexOffset3 = indexData[f+2];

            vA[0] = vertexData[vertexIndexOffset1];
            vA[1] = vertexData[vertexIndexOffset1 + 1];
            vA[2] = vertexData[vertexIndexOffset1 + 2];

            vB[0] = vertexData[vertexIndexOffset2];
            vB[1] = vertexData[vertexIndexOffset2 + 1];
            vB[2] = vertexData[vertexIndexOffset2 + 2];

            vC[0] = vertexData[vertexIndexOffset3];
            vC[1] = vertexData[vertexIndexOffset3 + 1];
            vC[2] = vertexData[vertexIndexOffset3 + 2];

            cb[0] = vC[0] - vB[0];
            cb[1] = vC[1] - vB[1];
            cb[2] = vC[2] - vB[2];

            ab[0] = vA[0] - vB[0];
            ab[1] = vA[1] - vB[1];
            ab[2] = vA[2] - vB[2];


            crossResult[0] = cb[1] * ab[2] - cb[2] * ab[1];
            crossResult[1] = cb[2] * ab[0] - cb[0] * ab[2];
            crossResult[2] = cb[0] * ab[1] - cb[1] * ab[0];

            //float vLength = (float) Math.sqrt(crossResult[0] * crossResult[0] + crossResult[1] * crossResult[1] + crossResult[2] * crossResult[2]);
            cb[0] = crossResult[0];// / vLength;
            cb[1] = crossResult[1];// / vLength;
            cb[2] = crossResult[2];// / vLength;

            vertexData[vertexCount * 3 + vertexIndexOffset1] = cb[0];
            vertexData[vertexCount * 3 + vertexIndexOffset1+1] = cb[1];
            vertexData[vertexCount * 3 + vertexIndexOffset1+2] = cb[2];

            vertexData[vertexCount * 3 + vertexIndexOffset2] = cb[0];
            vertexData[vertexCount * 3 + vertexIndexOffset2+1] = cb[1];
            vertexData[vertexCount * 3 + vertexIndexOffset2+2] = cb[2];

            vertexData[vertexCount * 3 + vertexIndexOffset3] = cb[0];
            vertexData[vertexCount * 3 + vertexIndexOffset3+1] = cb[1];
            vertexData[vertexCount * 3 + vertexIndexOffset3+2] = cb[2];

        }

    }

    private void computeCenter() {
        int offset = 0;
        float minX, maxX, minY, maxY, minZ, maxZ;

        // Inialise values to first vertex
        minX = maxX = vertexData[offset];
        minY = maxY = vertexData[offset + 1];
        minZ = maxZ = vertexData[offset + 2];

        // Loop through all vertices to find extremas
        for (int i = 1; i < vertexCount; i++) {
            offset = i * 3;

            // Minimum and Maximum X
            if (vertexData[offset] < minX) minX = vertexData[offset];
            if (vertexData[offset] > maxX) maxX = vertexData[offset];

            // Minimum and Maximum Y
            if (vertexData[offset + 1] < minY) minY = vertexData[offset + 1];
            if (vertexData[offset + 1] > maxY) maxY = vertexData[offset + 1];

            // Minimum and Maximum Z
            if (vertexData[offset + 2] < minZ) minZ = vertexData[offset + 2];
            if (vertexData[offset + 2] > maxZ) maxZ = vertexData[offset + 2];
        }

        center[0] = (maxX + minX) / 2.0f;
        center[1] = (maxY + minY) / 2.0f;
        center[2] = (maxZ + minZ) / 2.0f;

        //center[1] *= -1.0f;
    }
}
