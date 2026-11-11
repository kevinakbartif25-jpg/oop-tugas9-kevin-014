package id.ac.polban.pbo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Unit Test SavingsAccount - Inheritance & Polymorphism")
class SavingsAccountTest {

    @Test
    @DisplayName("TR06 - Aturan saldo minimum SavingsAccount berlaku lewat reference BankAccount")
    void aturanSubclass_berlakuLewatReferenceInduk() {
        BankAccount acc = new SavingsAccount("003", "Citra", 100_000, 0.05);

        InsufficientBalanceException ex = assertThrows(
                InsufficientBalanceException.class,
                () -> acc.withdraw(60_000)
        );
        assertEquals("Saldo minimum Rp50.000 harus dipertahankan", ex.getMessage());
        assertEquals(100_000, acc.getBalance(), 0.001);
    }

    @Test
    @DisplayName("TR13a - Penarikan tepat menyisakan batas minimum Rp50.000 diterima")
    void withdraw_tepatBatasMinimum_diterima() throws InsufficientBalanceException {
        SavingsAccount savings = new SavingsAccount("003", "Citra", 100_000, 0.05);

        savings.withdraw(50_000);

        assertEquals(SavingsAccount.MIN_BALANCE, savings.getBalance(), 0.001);
    }

    @Test
    @DisplayName("TR13b - Constructor SavingsAccount dengan bunga negatif ditolak")
    void constructor_bungaNegatif_ditolak() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new SavingsAccount("003", "Citra", 100_000, -0.05)
        );
        assertEquals("Bunga tidak boleh negatif", ex.getMessage());
    }

    @Test
    @DisplayName("TR13c - addInterest menambahkan bunga ke saldo rekening")
    void addInterest_menambahSaldoSesuaiBunga() {
        SavingsAccount savings = new SavingsAccount("003", "Citra", 100_000, 0.05);

        savings.addInterest();

        assertEquals(105_000, savings.getBalance(), 0.001);
    }
}
