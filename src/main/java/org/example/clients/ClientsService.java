package org.example.clients;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class ClientsService {

    private final ClientDAO clientDAO;
    private final Scanner scanner = new Scanner(System.in);

    public ClientsService(Connection connection) {
        this.clientDAO = new ClientDAOImpl(connection);
    }

    public void start(){
        while (true) {
            System.out.println("Clients DB");
            System.out.println("\t1: add client");
            System.out.println("\t2: view clients");
            System.out.print("\t-> ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    clientDAO.addClient();
                    break;
                case "2":
                    List<Client> clients = clientDAO.getClients();
                    for (Client client : clients) {
                        System.out.println(client);
                    }
                    break;
                default:
                    return;
            }
        }
    }
}
