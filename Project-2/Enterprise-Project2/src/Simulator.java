/* Name: Bryan Aneyro Hernandez
Course: CNT 4714 Spring 2024
Assignment title: Project 2 – Synchronized, Cooperating Threads Under Locking
Due Date: February 11, 2024
*/

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Simulator {

    // Define the number of withdrawal, depositor, and auditor agents
    private static final int NUM_WITHDRAWAL_AGENTS = 10;
    private static final int NUM_DEPOSITOR_AGENTS = 5;
    private static final int NUM_AUDITOR_AGENTS = 2;

    public static void main(String [] args) {

        System.out.println("* * *  SIMULATION BEGINS...");
        System.out.printf("%-35s%-30s%-20s%-20s%n", "Deposit Agents", "Withdrawal Agents", "Balance", "Transaction Number");
        System.out.printf("%-35s%-30s%-20s%-20s%n", "----------------", "------------------", "--------", "------------------");


        // Create a fixed-size thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUM_WITHDRAWAL_AGENTS + NUM_DEPOSITOR_AGENTS + NUM_AUDITOR_AGENTS);

        Bank bankAccount = new Bank();

        // Start withdrawal agents
        for(int i = 1; i <= NUM_WITHDRAWAL_AGENTS; i++) {
            WithdrawalAgent withdrawalAgent = new WithdrawalAgent(bankAccount, "WT" + i);
            executor.submit(withdrawalAgent);
        }

        // Start depositor agents
        for(int i = 1; i <= NUM_DEPOSITOR_AGENTS; i++) {
            DepositorAgent depositorAgent = new DepositorAgent(bankAccount, "DT" + i);
            executor.submit(depositorAgent);
        }

        // Start auditor agents
        for(int i = 1; i <= NUM_AUDITOR_AGENTS; i++) {
            AuditorAgent auditorAgent = new AuditorAgent(bankAccount, "AT" + i);
            executor.submit(auditorAgent);
        }

    }
}
