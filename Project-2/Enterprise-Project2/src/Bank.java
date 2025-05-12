/* Name: Bryan Aneyro Hernandez
Course: CNT 4714 Spring 2024
Assignment title: Project 2 – Synchronized, Cooperating Threads Under Locking
Due Date: February 11, 2024
*/

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

public class Bank {

    // Shared resources
    public static int accountBalance = 0;
    public static int transactionNumber = 1;
    protected static Lock lock = new ReentrantLock();
    protected static Condition condition = lock.newCondition();

    protected static void logTransaction(String transactionType, String agentName, int amount) {

        // Needed classes to format flagged transaction
        DecimalFormat twoDecimalAmount = new DecimalFormat("#.##");
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss z");
        String formattedTime = formatter.format(now);

        try (PrintWriter logFile = new PrintWriter(new FileWriter("transactions.csv", true))) {
            String formattedString = "";

            if(transactionType.equals("Withdrawal")) {
                formattedString = "\t Withdrawal Agent " + agentName + " issued withdrawal of $" +
                        twoDecimalAmount.format(amount) + " at: " + formattedTime + "  Transaction Number : " + transactionNumber;
            }
            else {
                formattedString = "Depositor Agent " + agentName + " issued deposit of $" +
                        twoDecimalAmount.format(amount) + " at: " + formattedTime + "  Transaction Number : " + transactionNumber;
            }

            logFile.println(formattedString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deposit(String name) {
        try {
            // Locking for exclusive access
            lock.lock();

            // Deposit logic
            int depositAmount = (int) (Math.random() * 500) + 1;
            accountBalance += depositAmount;

            if (depositAmount > 350) {
                // Log deposit transaction
                logTransaction("Deposit", name, depositAmount);
                System.out.println();
                System.out.println("* * * Flagged Transaction - Deposit Agent " + name +
                        " Made A Withdrawal In Excess of $75.00 USD - See Flagged Transaction Log.");
                System.out.println();
            } else {
                System.out.printf("%-35s%-30s%-20s%20s\n", "Agent " + name + " deposits $" + depositAmount, " ",
                        "(+) Balance is $" + accountBalance, transactionNumber);
            }

            // Increment transaction number
            transactionNumber++;

            // Signal waiting withdrawal agents
            condition.signalAll();
        }
        finally {
            // Releasing the lock
            lock.unlock();
        }
    }

    public void withdrawal(String name) {
        try {
            // Locking for exclusive access
            lock.lock();

            // Randomly generate Withdrawal
            int withdrawAmount = (int) (Math.random() * 99) + 1;
            String agentName = name;

            if (withdrawAmount > accountBalance) {
                System.out.printf("%-35s%-30s%-60s\n", " ", "Agent " + agentName + " withdraws $" + withdrawAmount,
                        "(******) WITHDRAWAL BLOCKED - INSUFFICIENT FUNDS!!!");
            } else if (withdrawAmount > 75) {
                // Log deposit transaction
                logTransaction("Withdrawal", agentName, withdrawAmount);
                System.out.println();
                System.out.println("* * * Flagged Transaction - Withdrawal Agent " + agentName +
                        " Made A Withdrawal In Excess of $75.00 USD - See Flagged Transaction Log.");
                System.out.println();

                transactionNumber++;
            } else {
                // Withdrawal logic
                accountBalance -= withdrawAmount;

                System.out.printf("%-35s%-30s%-20s%20s\n", " ", "Agent " + agentName + " withdraws $" + withdrawAmount,
                        "(-) Balance is $" + accountBalance, transactionNumber);

                transactionNumber++;

            }

            // Signal waiting withdrawal agents
            condition.signalAll();
        }
        finally {
            // Releasing the lock
            lock.unlock();
        }
    }

    public void printAuditReport() {
        try {
            // Locking for exclusive access
            lock.lock();

            System.out.println();
            System.out.println("*****************************************************************************************************************************");
            System.out.println();
            System.out.println("TREASURY DEPT AUDITOR FINDS CURRENT ACCOUNT BALANCE TO BE: $" + accountBalance + "\t " +
                    "Number of transactions since last Treasury audit is: " + (transactionNumber - 1));
            System.out.println();
            System.out.println("*****************************************************************************************************************************");
            System.out.println();

            // Signal waiting auditor agents
            condition.signalAll();
        }
        finally {
            // Releasing the lock
            lock.unlock();
        }
    }
}