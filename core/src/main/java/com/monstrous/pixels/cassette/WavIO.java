package com.monstrous.pixels.cassette;

import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;

/** Class to read or write WAV audio files */
public class WavIO {

    public byte[] read(FileHandle fileHandle)  {
        WavInputStream input;
        try {
            input = new WavInputStream(fileHandle);
        } catch (Exception e) {
            System.out.println("CANNOT READ FILE");
            return null;
        }

        byte[] buffer = new byte[500000];   // todo

        int offset = 0;
        try {
            offset = input.read(buffer);
            System.out.println("Offset: "+offset);
        } catch (IOException e) {
            System.out.println("CANNOT READ FILE");
            return null;
        }

        byte[] byteBuffer = new byte[offset];   // made to measure
        for(int i = 0; i < offset; i++){
            byteBuffer[i] = buffer[i];
        }
        return byteBuffer;
//        // unsigned bytes are interpreted in Java as signed bytes, convert to int
//        int[] intBuffer = new int[offset];
//        for(int i = 0; i < offset; i++){
//            intBuffer[i] = (buffer[i] & 0xFF) - 128;
//        }
//        return intBuffer;
    }

    public boolean write(FileHandle file, byte[] data){
        file.writeString("RIFF", false);
        byte[] size = new byte[4];
        convert4(size, data.length + 36);
        file.writeBytes(size, true);
        file.writeString("WAVE", true);

        file.writeString("fmt ", true);
        convert4(size, 16);
        file.writeBytes(size, true);
        byte[] buf2 = new byte[2];
        convert2(buf2, 1); // format = PCM
        file.writeBytes(buf2, true);
        convert2(buf2, 1); // channels
        file.writeBytes(buf2, true);
        convert4(size, 18000); // sample rate
        file.writeBytes(size, true);
        convert4(size, 18000); // byte rate
        file.writeBytes(size, true);
        convert2(buf2, 1); // align
        file.writeBytes(buf2, true);
        convert2(buf2, 8); // bits per sample
        file.writeBytes(buf2, true);

        file.writeString("data", true);
        convert4(size, data.length);
        file.writeBytes(size, true);
        file.writeBytes(data, true);

        return true;
    }

    private void convert4(byte[] bytes, int value){
        // note: LSB to MSB
        bytes[3] = (byte)((value>>24) & 0xFF);
        bytes[2] = (byte)((value>>16) & 0xFF);
        bytes[1] = (byte)((value>>8) & 0xFF);
        bytes[0] = (byte)(value & 0xFF);
    }
    private void convert2(byte[] bytes, int value){
        // note: LSB to MSB
        bytes[1] = (byte)((value>>8) & 0xFF);
        bytes[0] = (byte)(value & 0xFF);
    }
}
