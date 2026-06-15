package com.monstrous.pixels.cassette;

import com.monstrous.pixels.filters.BorderFilter;

/** Class to convert audio samples to data */
public class Cassette {

    static final int BITS_PER_BYTE = 8;         // 1 start bit, 5 data bits, 2 stop bits, note bits are ternary
    static final int[] FREQUENCIES = { 1000, 2000, 3000 };   // in Hz

    final int sampleRate;
    final float sequenceDuration;
    final int samplesPerBit;


    public byte[] message;      // data
    private int readIndex;
    private int writeIndex;
    private float charTimer;
    private byte[] samples;     // audio samples
    private int sampleIndex;
    private final BorderFilter border;

    public Cassette(BorderFilter border){
        this(border, 18000, 0.004f);
    }

    public Cassette(BorderFilter border, int sampleRate, float sequenceDuration) {
        this.border = border;
        this.sampleRate = sampleRate;
        this.sequenceDuration = sequenceDuration;

        samplesPerBit = (int) (sampleRate * sequenceDuration);  // is 72 for 4ms at 18000 Hz

    }

    boolean conversionFinished;

    /** convert next byte if enough time has passed. Returns true while there is more data. */
    public boolean updateConversion(float deltaTime){
        if(conversionFinished)
            return false;

        // the conversion proceeds one byte at a time, not one bit at a time
        charTimer += deltaTime;

        // time to get the next byte yet?
        // (if delta time is very big, multiple bytes)
        while(charTimer > getSecondsPerByte()) { // 8 * 4ms
            charTimer -= getSecondsPerByte();

            int value = convertNextByte();
            if(value < 0){
                conversionFinished = true;
                border.setBorderColor(BorderFilter.BLACK);
                break;
            }
            byte k = (byte)value;
            message[writeIndex++] = k;
        }
        return !conversionFinished;
    }

    /** convert next byte from samples, returns -1 on error, otherwise a value in [0-255] */
    private int convertNextByte(){
        if(sampleIndex >= samples.length)   // reached end of audio
            return -1;

        int[] character = new int[8];
        int[] expectedZeroCrosses = new int[3];
        for(int f = 0; f < 3; f++)
            expectedZeroCrosses[f] = (int)(2*FREQUENCIES[f]*sequenceDuration);
        int threshold1 = (expectedZeroCrosses[0] + expectedZeroCrosses[1])/2;
        int threshold2 = (expectedZeroCrosses[1] + expectedZeroCrosses[2])/2;

        for(int charIndex = 0; charIndex < 8; charIndex++) {
            //while(sampleIndex < samples.length) {
            // 72 samples is a 4ms sequence at 18000 Hz, is one ternary bit

            int zeroPasses = 0;
            int prevSample = (samples[sampleIndex++] & 0xFF) - 128;
            for (int i = 1; i < samplesPerBit; i++) {
                int sample = (samples[sampleIndex++] & 0xFF) - 128;
                if (sample == 0 || (sample * prevSample) < 0)
                    zeroPasses++;
                prevSample = sample;
            }

            // ternary bit (1000Hz = 0, 2000Hz = 1, 3000Hz = 2)
            int digit;
            if (zeroPasses < threshold1)
                digit = 0;
            else if (zeroPasses < threshold2)
                digit = 1;
            else
                digit = 2;

            if(border != null)
                border.changeColor(digit == 0 ? BorderFilter.YELLOW : BorderFilter.CYAN);     // do a ZX Spectrum style border color change

            character[charIndex] = digit;
        }


        if(character[0] != 0) {
            System.out.println("header <> 0");
            return -1;
        }
        if(character[6] != 1 || character[7] != 2) {
            System.out.println("trailer <> 12");
            return -1;
        }

        int value = 0;
        for(int bit = 1; bit <=5; bit++){
            value = 3*value + character[bit];
        }
        return value;

    }

    // after calling this, keep calling updateConversion() until it returns false.
    //
    public void startConversion(byte[] samples) {
        this.samples = samples;
        message = new byte[samples.length/(samplesPerBit * BITS_PER_BYTE)];
        readIndex = 0;
        writeIndex = 0;
        sampleIndex = 0;
        conversionFinished = false;
        charTimer = 0;
        border.setBorderColor(BorderFilter.CYAN);
    }

    public boolean isDataReady(){
        return (readIndex < message.length && readIndex < writeIndex);
    }


    public byte getByte(){
        if(readIndex >= message.length || readIndex >= writeIndex)
            return 0;
        return message[readIndex++];
    }

    /** get transmission pattern for a byte. (int[8]) */
    public static void getTransmissionPattern(byte b, int[] buf){
        // convert byte of data into transmission bits
        buf[0] = 0;
        int value = b & 0xFF;
        for(int dataBit = 0; dataBit < 5; dataBit++){
            int bit = value % 3;
            value /= 3;
            buf[5-dataBit] = bit; // msb first
        }
        buf[6] = 1;
        buf[7] = 2;
    }

    public byte[] convertToSamples(byte[] data){
        byte[] samples = new byte[ samplesPerBit * BITS_PER_BYTE * data.length];
        int sampleIndex = 0;
        int[] character = new int[8];
        float phase = 0;

        for(int readIndex = 0; readIndex < data.length; readIndex++){ // byte per byte

            // convert byte of data into transmission bits
            getTransmissionPattern(data[readIndex], character);

            for(int charIndex = 0; charIndex < 8; charIndex++){
                int transmissionBit = character[charIndex];
                float f = FREQUENCIES[transmissionBit];
                float dt = sequenceDuration/ samplesPerBit; // delta time per sample
                float dAngle = 2.0f * (float)Math.PI * dt * f;

                for(int sample = 0; sample < samplesPerBit; sample++){
                    // make the wave a bit more square by adding odd harmonics, to give the right vibe
                    float s = (float)(Math.sin(phase) + Math.sin(3f*phase)/3.0 + Math.sin(5f*phase)/5.0);
                    int sampleValue = (int)(128 + 127 * s);
                    phase += dAngle;
                    samples[sampleIndex++] = (byte)(sampleValue & 0xFF);
                }

            }

        }
        return samples;
    }

    public int getBitsPerByte(){
        return BITS_PER_BYTE;
    }

    public float getSecondsPerByte(){
        return BITS_PER_BYTE * sequenceDuration;
    }


}
