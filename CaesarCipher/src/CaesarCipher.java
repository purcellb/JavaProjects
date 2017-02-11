/**
 * Template by byan, assignment by Bobby Purcell
 */
public class CaesarCipher {
    private int a;
    private int n;
    private final char[] letters;

    public CaesarCipher() {
        this.letters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    }

    public int getA() {
        return a;
    }

    public int getN() {
        return n;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int encrypt(int x) {
        x = (x + a % n);
        return x;
    }

    public int decrypt(int y) {
        y = (y - a % n);
        return y;
    }

    public String encrypt(String plaintext) {
        char[] et = plaintext.toCharArray();
        for (int i = 0; i < et.length; i++) {
            char let = et[i];
            let = (char) ((int) let + a % n);
            // wrapping the alphabet around
            if (let > 'z') {
                let -= 26;
            }
            if (let < 'a') {
                let += 26;
            }
            et[i] = letters[let - 97];
        }
        return new String(et);
    }

    public String decrypt(String ciphertext) {
        char[] dt = ciphertext.toCharArray();
        for (int i = 0; i < dt.length; i++) {
            char let = dt[i];
            let = (char) ((int) let - a % n);
            if (let > 'z') {
                let -= 26;
            }
            if (let < 'a') {
                let += 26;
            }
            //had to throw in the -97 to keep in in the bounds of the alphabet rather than ascii
            dt[i] = letters[let - 97];
        }
        return new String(dt);
    }
}
