package id.ac.polban.pbo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Unit Test BankAccount - Modul Praktikum 9")
class BankAccountTest {

    private BankAccount sender;
    private BankAccount receiver;

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("[LIFECYCLE] @BeforeAll - Mulai menjalankan suite BankAccountTest");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("[LIFECYCLE] @AfterAll - Selesai menjalankan suite BankAccountTest");
    }

    @BeforeEach
    void setUp() {
        sender = new BankAccount("001", "Andi", 100_000);
        receiver = new BankAccount("002", "Budi", 0);
    }

    @Test
    @DisplayName("TR01/TR02 - Deposit menambah saldo sesuai jumlah setoran")
    void deposit_menambahSaldo() {
        sender.deposit(50_000);

        assertEquals(150_000, sender.getBalance(), 0.001);

//         assertEquals(160_000, sender.getBalance(), 0.001);
    }

    @Test
    @DisplayName("TR09 - Constructor menyimpan data rekening dengan benar (assertAll)")
    void constructor_menyimpanDataDenganBenar() {
        BankAccount acc = new BankAccount("001", "Andi", 100_000);

        assertAll("data rekening",
                () -> assertEquals("001", acc.getAccountNumber()),
                () -> assertEquals("Andi", acc.getOwner()),
                () -> assertEquals(100_000, acc.getBalance(), 0.001)
        );
    }

    @Test
    @DisplayName("TR03 - Withdraw melebihi saldo ditolak dan saldo tetap utuh")
    void withdraw_melebihiSaldo_ditolak() {
        assertThrows(
                InsufficientBalanceException.class,
                () -> sender.withdraw(200_000)
        );
        assertEquals(100_000, sender.getBalance(), 0.001);
    }

    @Test
    @DisplayName("TR04 - Withdraw valid tidak melempar exception dan saldo berkurang")
    void withdraw_valid_tidakMelempar() {
        assertDoesNotThrow(() -> sender.withdraw(30_000));
        assertEquals(70_000, sender.getBalance(), 0.001);
    }

    @Test
    @DisplayName("TR10a - Constructor saldo awal negatif ditolak dengan pesan tepat")
    void constructor_saldoNegatif_ditolak() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new BankAccount("001", "Andi", -1)
        );
        assertEquals("Saldo awal tidak boleh negatif", ex.getMessage());
    }

    @Test
    @DisplayName("TR10b - Constructor nomor rekening kosong/null ditolak")
    void constructor_nomorRekeningKosong_ditolak() {
        assertAll("validasi nomor rekening",
                () -> {
                    IllegalArgumentException ex = assertThrows(
                            IllegalArgumentException.class,
                            () -> new BankAccount(null, "Andi", 10_000)
                    );
                    assertEquals("Nomor rekening wajib diisi", ex.getMessage());
                },
                () -> {
                    IllegalArgumentException ex = assertThrows(
                            IllegalArgumentException.class,
                            () -> new BankAccount("   ", "Andi", 10_000)
                    );
                    assertEquals("Nomor rekening wajib diisi", ex.getMessage());
                }
        );
    }

    @ParameterizedTest(name = "TR05 - setoran {0} ditolak")
    @ValueSource(doubles = {0, -0.01, -1, -50_000})
    @DisplayName("TR05 - Setoran 0, boundary negatif, dan negatif ditolak")
    void deposit_tidakValid(double amount) {
        assertThrows(
                IllegalArgumentException.class,
                () -> sender.deposit(amount)
        );
        assertEquals(100_000, sender.getBalance(), 0.001);
    }

    @ParameterizedTest(name = "TR11 - saldo {0} - tarik {1} = {2}")
    @CsvSource({
            "100000, 10000,   90000",
            "100000, 100000,  0",
            "50000,  25000.5, 24999.5"
    })
    @DisplayName("TR11 - Withdraw berbagai nilai saldo dan nominal penarikan")
    void withdraw_berbagaiNilai(double awal, double tarik, double sisa)
            throws InsufficientBalanceException {
        BankAccount acc = new BankAccount("009", "Dewi", awal);

        acc.withdraw(tarik);

        assertEquals(sisa, acc.getBalance(), 0.001);
    }

    @Nested
    @DisplayName("TR12 - Pengujian Transfer (@Nested) & Mini TDD")
    class TransferTests {

        @Test
        @DisplayName("Transfer valid memindahkan saldo dari pengirim ke penerima")
        void transfer_valid_memindahkanSaldo() throws InsufficientBalanceException {
            sender.transfer(receiver, 40_000);

            assertAll("saldo setelah transfer",
                    () -> assertEquals(60_000, sender.getBalance(), 0.001),
                    () -> assertEquals(40_000, receiver.getBalance(), 0.001)
            );
        }

        @Test
        @DisplayName("TR07/TR08 - Transfer dengan target null ditolak dan saldo pengirim tetap")
        void transfer_targetNull_ditolak() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> sender.transfer(null, 20_000)
            );
            assertEquals("Rekening tujuan tidak boleh null", ex.getMessage());
            assertEquals(100_000, sender.getBalance(), 0.001);
        }

        @Test
        @DisplayName("TR14 - Transfer ke rekening sendiri ditolak dan saldo tetap")
        void transfer_keRekeningSendiri_ditolak() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> sender.transfer(sender, 20_000)
            );
            assertEquals("Tidak dapat transfer ke rekening sendiri", ex.getMessage());
            assertEquals(100_000, sender.getBalance(), 0.001);
        }
    }
}
