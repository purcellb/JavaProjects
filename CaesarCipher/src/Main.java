import java.util.Scanner;

/**
 * Template by byan, assignment by Bobby Purcell
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to Caesar Cipher Demo:");
        Scanner scanner = new Scanner(System.in);
        CaesarCipher cipher = new CaesarCipher();
        int a, n, x, y;
        int i;
        String s = "test";
        s.toCharArray();

        while (true) {
            System.out.println("====== General Test ======");
            System.out.println("Please specify the following numbers:");
            System.out.printf("n - ");
            n = scanner.nextInt();
            scanner.nextLine();
            System.out.print("a - ");
            a = scanner.nextInt();
            scanner.nextLine();
            cipher.setN(n);
            cipher.setA(a);
            System.out.printf("plaintext value from [0 - %d] - ", (n - 1));
            x = scanner.nextInt();
            y = cipher.encrypt(x);
            System.out.printf("ciphertext value - %d%n", y);
            x = cipher.decrypt(y);
            System.out.printf("back to plaintext value - %d%n", x);
            scanner.nextLine();
            System.out.printf("Continue? (1 for yes, 0 for no) - ");
            i = scanner.nextInt();
            scanner.nextLine();
            if (i == 0) {
                break;
            }
        }

        while (true) {
            System.out.println("====== Text Message Test ======");
            cipher.setN(26);
            System.out.printf("key [0-25] - ");
            a = scanner.nextInt();
            scanner.nextLine();
            while (a < 0 || a > 25) {
                System.out.printf("key must be from [0-25] - ");
                a = scanner.nextInt();
                scanner.nextLine();
            }
            cipher.setA(a);
            System.out.printf("plaintext (use English alphabet)- ");
            String plaintext = scanner.nextLine();
            plaintext = plaintext.toLowerCase();
            String ciphertext = cipher.encrypt(plaintext);
            System.out.printf("ciphertext - %s%n", ciphertext);
            plaintext = cipher.decrypt(ciphertext);
            System.out.printf("plaintext - %s%n", plaintext);
            System.out.printf("Continue? (1 for yes, 0 for no) - ");
            i = scanner.nextInt();
            scanner.nextLine();
            if (i == 0) {
                break;
            }
        }

    }
}
