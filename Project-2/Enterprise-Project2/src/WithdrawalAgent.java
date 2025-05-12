/* Name: Bryan Aneyro Hernandez
Course: CNT 4714 Spring 2024
Assignment title: Project 2 – Synchronized, Cooperating Threads Under Locking
Due Date: February 11, 2024
*/

public class WithdrawalAgent implements Runnable{

    private Bank bank = null;
    public String name = null;

    public WithdrawalAgent(Bank bankAccount, String name) {
        this.bank = bankAccount;
        this.name = name;
    }

    @Override
    public void run() {
        while(true) {
            try {
                bank.withdrawal(name);

                // Generate a random sleep time
                Thread.sleep((long) (Math.random() * 500));
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
