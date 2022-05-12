/*************************************************************************
 *  Compilation:  javac LZWmod.java
 *  Execution:    java LZWmod - < input.txt   (compress)
 *  Execution:    java LZWmod + < input.txt   (expand)
 *  Dependencies: BinaryStdIn.java BinaryStdOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

/***********************************************
 * README
 * I didn't comment on the same code as LZWmod
 * I just comment on the different part
 ***********************************************/

public class LZWextra {
    private static final int R = 256;
    private static int L = 512;
    private static int W = 9;
    private static final int maxW = 16;
    private static final int maxL = 65536;
    public static char reset;
    // these variables are used to check ratio and decide whether to reset
    // initialization
    private static final double threshhold = 1.1; //it is a ration which can not be reachable
    public static double oldRatio = -1;
    public static double newRatio = -1;
    public static int uncompressedSize = 0;
    public static int compressedSize = 0;


    public static void compress() {
        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;
        BinaryStdOut.write(reset);

        StringBuilder current = new StringBuilder();
        char c = BinaryStdIn.readChar();
        current.append(c);
        Integer codeword = st.get(current);
        while (!BinaryStdIn.isEmpty()) {
            codeword = st.get(current);
            char next = BinaryStdIn.readChar();
            current.append(next);
            if(!st.contains(current)) {
                BinaryStdOut.write(codeword, W);
                // update uncompressed size & compressed size
                uncompressedSize += 8*(current.length()-1);
                compressedSize += W;

                if(code >= L && W < maxW){
                    W++;
                    L *= 2;
                    st.put(current, code++);
                }
                // calculate the compression ratio and test whether it reaches threshhold
                else if(W >= maxW) {
                    if (oldRatio == -1) {
                        oldRatio = ((double)(uncompressedSize)) / (compressedSize);
                    }
                    newRatio = ((double)uncompressedSize)/ (compressedSize);
                    //in this way the dictionary should be reset
                    if ((oldRatio / newRatio) > threshhold) {
                        oldRatio = -1;
                        st = new TSTmod<Integer>();
                        for (int i = 0; i < R; i++)
                            st.put(new StringBuilder("" + (char) i), i);
                        L = 512;
                        W = 9;
                        code = R + 1;
                    }
                }
                else if(code < L) {
                    st.put(current, code++);
                }

                current = new StringBuilder();
                current.append(next);
            }
        }

        BinaryStdOut.write(st.get(current), W);
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }


    public static void expand() {
        String[] st = new String[maxL];
        int i;

        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            // update uncompressed size & compressed size
            uncompressedSize += 8*val.length();
            compressedSize += W;
            BinaryStdOut.write(val);

            if(i >= L && W < maxW) {
                W++;
                L *= 2;
            }
            // same idea as compression, we need to reset
            else if( W >= maxW){
                if (oldRatio == -1){
                    oldRatio = ((double)(uncompressedSize)) / (compressedSize);
                }
                newRatio = ((double)uncompressedSize)/ (compressedSize);

                if((oldRatio / newRatio) > threshhold) {
                    oldRatio = -1;
                    st = new String[maxL];
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";
                    W = 9;
                    L = 512;
                    i = R;
                }
            }

            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {
        //do not need read input argument r/n
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }
}