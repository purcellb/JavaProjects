/**
 * Template By B. Yan
 * Created by Bobby on 3/21/2016.
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome the small-scale RSA Testing Tool!");
        System.out.println("========================================= \n");
        System.out.println("Please type two relatively small prime numbers");
        Scanner input = new Scanner(System.in);
        System.out.print("p: ");
        int p = input.nextInt();

        System.out.print("\nq: ");
        int q = input.nextInt();

        System.out.println("\nn = p * q = " + p * q);

        int t = (p - 1) * (q - 1);
        System.out.println("t = (p-1) * (q-1) = " + t);

        System.out.println("Please type an e that is relatively prime with t=" + t + " : ");

        int e = input.nextInt();

        int keys[] = rsaKeyGen(p, q, e);

        System.out.println("Public Keys: n = " + keys[0] + " and e = " + keys[1] + "(published)");
        System.out.println("Private Key: d = " + keys[2] + " (You should keep this secret!)");

        System.out.println("Testing the encryption/decryption using the above generated keys: ");
        System.out.print("Please type a number to encrypt (0 to exit): ");
        int x = input.nextInt();
        BigDecimal y = null;
        BigDecimal z = null;

        while (x != 0) {
            if (x >= p * q) System.err.println("The number should be within (1 - " + (p * q - 1) + ").");
            else {
                y = rsaEncrypt(new BigDecimal(x), new BigDecimal(keys[1]), new BigDecimal(keys[0]));
                System.out.println("Result: " + x + " encrypted to " + y);
                System.out.println("");
                z = rsaDecrypt(y, new BigDecimal(keys[2]), new BigDecimal(keys[0]));
                System.out.println("Result: " + y + " decrypted to " + z);

                if (x == z.intValue()) System.out.println("It works!");
                else System.out.println("It did not work, please check your implementation!");
            }
            System.out.print("Please type a number to encrypt (0 to exit): ");
            x = input.nextInt();
        }

        System.out.println("Testing the encryption/decryption on text using the above generated keys: ");
        System.out.print("Please type plain text to encrypt (Type \"EXIT\" to exit): ");
        input = new Scanner(System.in);
        String plainTextInput = input.nextLine().toLowerCase();
        char[] encryptedChrArray = (plainTextInput.toCharArray());
        System.out.println();
        while (plainTextInput != "EXIT") {
            //encryption
            for (int i = 0; i < encryptedChrArray.length; i++) {
                x = (encryptedChrArray[i]) - 96;//TODO play with this a bit for correct i/o

                y = rsaEncrypt(new BigDecimal(x), new BigDecimal(keys[1]), new BigDecimal(keys[0]));
                char let = (char) (y.intValue());

                while (let > 'z') {
                    let -= 26;
                }
                while (let < 'a') {
                    let += 26;
                }
                encryptedChrArray[i] = let;
                // }
            }
            String encTxt = new String(encryptedChrArray);
            System.out.println("\nResult: " + plainTextInput + "\nEncrypted: " + encTxt + "\n");

            //decryption
            char[] decryptedChrArray = new char[encryptedChrArray.length];
            for (int i = 0; i < encryptedChrArray.length; i++) {
                y = new BigDecimal(((int) (encryptedChrArray[i])));//TODO play with this a bit for correct i/o
                z = rsaDecrypt(y, new BigDecimal(keys[2]), new BigDecimal(keys[0]));
                char let = (char) (z.intValue());
                while (let > 'z') {
                    let -= 26;
                }
                while (let < 'a') {
                    let += 26;
                }
                decryptedChrArray[i] = let;

            }
            System.out.println("\n\n RESULTS\nOriginal Plain Text: " + plainTextInput);
            String decTxt = new String(decryptedChrArray);
            System.out.println("Encrypted Text: " + encTxt + "\nDecrypted Text: " + decTxt + "\n");

            System.out.print("Please type additional plain text to encrypt or type \"EXIT\" to exit: ");
            plainTextInput = input.nextLine();
        }
        System.out.println("Done!");
    }

    //assuming k > j, the first returned value is gcd of k and j
    //the second is the inverse of j in Z(k)
    public static int[] egcd(int k, int j) {
        List<Integer> quotients = new ArrayList<>();
        List<Integer> remainders = new ArrayList<>();
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();

        int oldK = k;

        int quotient = k / j;
        int remainder = k % j;
        int gcd = remainder == 0 ? j : 1;

        //System.out.println("The first quotient is " + quotient);
        //System.out.println("The first remainder is " + remainder);
        while (remainder != 0) {
            quotients.add(quotient);
            //System.out.println("Adding " + quotient + " to quotients.");
            remainders.add(remainder);
            //System.out.println("Adding " + remainder + " to remainders.");

            k = j;
            j = remainder;
            quotient = k / j;
            remainder = k % j;
            gcd = j;
        }

        int result[] = new int[2];
        result[0] = gcd;

        if (gcd != 1) {
            System.out.println("These two numbers " + j + " and " + k + " are not relatively prime.");
            System.exit(0);
//                    result[1] = 0;
//                    return result;
        }
        xs.add(1);
        ys.add(0);

        int y = 1;
        int x = 0 - quotients.remove(quotients.size() - 1);
        int oldY = y;
        int oldX = x;

        while (quotients.size() > 0) {
            y = x;
            x = oldY - quotients.remove(quotients.size() - 1) * oldX;
            oldY = y;
            oldX = x;
        }


        result[1] = mod(new BigDecimal(x), new BigDecimal(oldK)).intValue();

        //System.out.println("x is " + x);
        //System.out.println("y is " + y);
        //System.out.println("oldK is " + oldK);
        return result;
    }

    public static int[] rsaKeyGen(int p, int q, int e) {
        int n, d, t;
        int keys[] = new int[3];
        keys[0] = n = p * q;
        keys[1] = e;
        t = (p - 1) * (q - 1);
        int gcdx[] = egcd(t, e);
        keys[2] = gcdx[1];

        return keys;
    }

    public static BigDecimal rsaEncrypt(BigDecimal x, BigDecimal e, BigDecimal n) {
        System.out.println("Encrypting " + x + " with e = " + e + " and n = " + n);
        //System.out.println(x.pow(e.intValue()));
        return mod(x.pow(e.intValue()), n);
    }

    public static BigDecimal rsaDecrypt(BigDecimal y, BigDecimal d, BigDecimal n) {
        System.out.println("Decrypting " + y + " with d = " + d + " and n = " + n);
        //System.out.println(y.pow(d.intValue()));
        return mod(y.pow(d.intValue()), n);
    }

    public static BigDecimal mod(BigDecimal a, BigDecimal n) {
        //System.out.println("------------------------------------");
        //System.out.println("In mod method: a is " + a + " n is " + n);
        if (a.compareTo(new BigDecimal(0)) == -1)
            return n.subtract(a.negate().remainder(n));
        else
            return a.remainder(n);
    }
}