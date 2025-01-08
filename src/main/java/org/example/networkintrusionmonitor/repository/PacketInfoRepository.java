package org.example.networkintrusionmonitor.repository;


import org.example.networkintrusionmonitor.model.PacketInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PacketInfoRepository {
    private static final String DB_URL = "jdbc:sqlite:network_monitor.db";

    public void initializeTable() {
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS packet_info (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "source_ip TEXT NOT NULL, " +
                "source_port INTEGER NOT NULL, " +
                "destination_ip TEXT NOT NULL, " +
                "destination_port INTEGER NOT NULL, " +
                "protocol TEXT, " +
                "packet_length LONG, " +
                "packet_type TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "raw_packet_data TEXT, " +
                "hex_stream TEXT, " + // New column for hex stream
                "decoded_content TEXT" + // New column for decoded string
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreateTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePacketInfo(PacketInfo packetInfo) {
        String sql = "INSERT INTO packet_info "
                + "(source_ip, source_port, destination_ip, destination_port, "
                + "protocol, packet_length, packet_type, timestamp, raw_packet_data, "
                + "hex_stream, decoded_content) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, packetInfo.getSourceIp());
            pstmt.setInt(2, packetInfo.getSourcePort());
            pstmt.setString(3, packetInfo.getDestinationIp());
            pstmt.setInt(4, packetInfo.getDestinationPort());
            pstmt.setString(5, packetInfo.getProtocol());
            pstmt.setLong(6, packetInfo.getPacketLength());
            pstmt.setString(7, packetInfo.getPacketType());
            pstmt.setTimestamp(8, Timestamp.valueOf(packetInfo.getTimestamp()));
            pstmt.setString(9, packetInfo.getRawPacketData());
            pstmt.setString(10, packetInfo.getHexStream());
            pstmt.setString(11, packetInfo.getDecodedContent());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PacketInfo> getAllPackets() {
        List<PacketInfo> packets = new ArrayList<>();
        String sql = "SELECT * FROM packet_info ORDER BY timestamp DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                packets.add(new PacketInfo(
                        rs.getString("source_ip"),
                        rs.getInt("source_port"),
                        rs.getString("destination_ip"),
                        rs.getInt("destination_port"),
                        rs.getString("protocol"),
                        rs.getLong("packet_length"),
                        rs.getString("packet_type"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getString("raw_packet_data"),
                        rs.getString("hex_stream"),
                        rs.getString("decoded_content")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return packets;
    }

    public void truncatePacketInfoTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // Delete all rows
            stmt.execute("DELETE FROM packet_info");

            // Reset the auto-increment counter
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='packet_info'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PacketInfo> getPacketsByProtocol(String protocol) {
        List<PacketInfo> packets = new ArrayList<>();
        String sql = "SELECT * FROM packet_info WHERE protocol = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, protocol);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    packets.add(new PacketInfo(
                            rs.getString("source_ip"),
                            rs.getInt("source_port"),
                            rs.getString("destination_ip"),
                            rs.getInt("destination_port"),
                            rs.getString("protocol"),
                            rs.getLong("packet_length"),
                            rs.getString("packet_type"),
                            rs.getTimestamp("timestamp").toLocalDateTime(),
                            rs.getString("raw_packet_data"),
                            rs.getString("hex_stream"),
                            rs.getString("decoded_content")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packets;
    }
}