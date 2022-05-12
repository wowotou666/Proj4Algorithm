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

public class LZWmod {
    private static final int R = 256;  // number of input chars
    private static int L = 512;  // number of codewords = 2^W
    private static int W = 9;   // codeword width
    private static final int maxW = 16;  // max codeword width
    private static final int maxL = 65536;  // max number of codewords
    public static char reset;  // reset dictionary identifier

    public static void compress() {
        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF
        BinaryStdOut.write(reset);  // reset dictionary identifier to decide whether to take action

        StringBuilder current = new StringBuilder(); // initialize current string
        char c = BinaryStdIn.readChar(); // read first character
        current.append(c); // append first character
        Integer codeword = st.get(current);
        while (!BinaryStdIn.isEmpty()) {
            codeword = st.get(current);
            char next = BinaryStdIn.readChar();  // read next character to current one
            current.append(next);  // append next character to current one

            if(!st.contains(current)) {
                BinaryStdOut.write(codeword, W);

                // if the codebook does not reach max, increase width and capacity
                if(code >= L && W < maxW){
                    W++;
                    L *= 2;
                    st.put(current, code++);
                }
                // if the codebook reaches max, reset dictionary(back to original)
                else if(W >= maxW && reset == 'r') {
                        st = new TSTmod<Integer>();
                        for (int i = 0; i < R; i++)
                            st.put(new StringBuilder("" + (char) i), i);
                        L = 512;
                        W = 9;
                        code = R + 1;
                }
                // codewords do not reach codebook max
                else if(code < L) {
                    st.put(current, code++); // add current string to codebook
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
        int i;  // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                       // (unused) lookahead for EOF

        //read in reset state
        reset = BinaryStdIn.readChar();
        if(reset != 'n' && reset != 'r'){
            throw new IllegalArgumentException("Illegal reset argument");
        }

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);

            // if the codebook does not reach max, increase width and capacity
            if(i >= L && W < maxW) {
                W++;
                L *= 2;
            }
            // if the codebook reaches max, reset dictionary(back to original)
            else if( W >= maxW && reset == 'r'){
                st = new String[maxL];
                // initialize symbol table with all 1-character strings
                for (i = 0; i < R; i++)
                    st[i] = "" + (char) i;
                st[i++] = "";  // (unused) lookahead for EOF
                W = 9;
                L = 512;
                i = R;
            }

            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {
        //read input argument r/n
        if(args.length > 1) {
            reset = args[1].charAt(0);
        }
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }
}