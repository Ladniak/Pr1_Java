import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Bank {
    // Максимальна кількість доступних банкоматів (ресурсів)
    private static final int ATM_COUNT = 3;
    // Робочі години банку (у секундах)
    private static final int BANK_OPEN_DURATION = 10;

    // Семафор для керування кількістю одночасно доступних банкоматів
    private Semaphore atmSemaphores = new Semaphore(ATM_COUNT);

    // Симулюємо роботу банку
    public void startBankOperations() {
        System.out.println("Банк відкрито на " + BANK_OPEN_DURATION + " секунд.");

        // Запускаємо клієнтів (потоки)
        for (int i = 1; i <= 10; i++) {
            new Thread(new Client(i, atmSemaphores)).start();
        }

        // Закриваємо банк через певний час
        try {
            Thread.sleep(BANK_OPEN_DURATION * 1000);
        } catch (InterruptedException e) {
            System.err.println("Помилка під час очікування закриття банку: " + e.getMessage());
        }

        System.out.println("Банк закрито. Банкомати більше недоступні.");
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.startBankOperations();
    }
}

// Клас для симуляції клієнта
class Client implements Runnable {
    private int clientId;
    private Semaphore atmSemaphores;

    public Client(int clientId, Semaphore atmSemaphores) {
        this.clientId = clientId;
        this.atmSemaphores = atmSemaphores;
    }

    @Override
    public void run() {
        try {
            System.out.println("Клієнт " + clientId + " прийшов до банку.");

            // Клієнт намагається отримати доступ до банкомату
            if (atmSemaphores.tryAcquire(5, TimeUnit.SECONDS)) { // Чекає до 5 секунд
                System.out.println("Клієнт " + clientId + " користується банкоматом.");

                // Симуляція зняття грошей
                Thread.sleep((long) (Math.random() * 4000) + 1000);

                System.out.println("Клієнт " + clientId + " завершив операцію.");
                atmSemaphores.release(); // Вивільняє банкомат
            } else {
                System.out.println("Клієнт " + clientId + " не дочекався банкомату і пішов.");
            }
        } catch (InterruptedException e) {
            System.err.println("Клієнт " + clientId + " зіткнувся з помилкою: " + e.getMessage());
        }
    }
}
