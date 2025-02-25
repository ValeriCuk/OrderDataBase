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

    @Override
    public int getClientByID() {
        int clientId;
        while (true) {
            try {
                System.out.println("Enter client id -> ");
                clientId = Integer.parseInt(scanner.nextLine());
                if (!clientExists(clientId)) {
                    System.out.println("Client with id " + clientId + " does not exist. Try again.");
                    continue;
                }
                return clientId;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid client ID.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean clientExists(int clientId) throws SQLException {
        String sql = "SELECT id, firstName, lastName, phone, email FROM Clients WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("phone"),
                            rs.getString("email")
                    );
                    System.out.println("Client: " + client);
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

}
