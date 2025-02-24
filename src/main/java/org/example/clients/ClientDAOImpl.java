package org.example.clients;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientDAOImpl implements ClientDAO {

    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);

    public ClientDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addClient(){
        System.out.println("Enter customer's first name -> ");
        String firstName = scanner.nextLine();

        System.out.println("Enter customer's last name -> ");
        String lastName = scanner.nextLine();

        System.out.println("Enter customer's phone number -> ");
        String phoneNumber = scanner.nextLine();

        System.out.println("Enter customer's email -> ");
        String email = scanner.nextLine();

        try {
            try (PreparedStatement st = connection.prepareStatement("INSERT INTO Clients (firstName, lastName, phone, email) VALUES(?, ?, ?, ?)")) {
                st.setString(1, firstName);
                st.setString(2, lastName);
                st.setString(3, phoneNumber);
                st.setString(4, email);
                st.executeUpdate();
                System.out.println("Client added successfully");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Client> getClients() {
        List<Client> res = new ArrayList<>();
        try {
            try (Statement st = connection.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM Clients")) {
                    while (rs.next()) {
                        Client client = new Client();

                        client.setId(rs.getInt(1));
                        client.setFirstName(rs.getString(2));
                        client.setLastName(rs.getString(3));
                        client.setPhone(rs.getString(4));
                        client.setEmail(rs.getString(5));
                        res.add(client);
                    }
                }
            }
            return res;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
