# Intrusion Detection Application

## Introduction
This project focuses on building an application to monitor network traffic, detect suspicious activity, and alert administrators in real time. It provides a simple and effective way to improve network security against cyber threats.

## Features
- **Real-time Network Monitoring**: Continuous capture and analysis of network traffic.
- **Packet Inspection**: Extraction of key packet details (IP addresses, protocol types, payload analysis).
- **Traffic Statistics**: Aggregated metrics on network activity.
- **Intrusion Detection**: Identification of suspicious patterns such as DoS attacks and unauthorized access.
- **User-friendly Interface**: Intuitive dashboard for visualizing network statistics and alerts.
- **Error Handling & Notifications**: Ensures smooth application operation and informs users of critical issues.
- **AI-Powered Intrusion Detection (WIP)**: Our machine learning model, built with TensorFlow and Keras, is fully developed and ready. We are working on integrating it into the system to enhance detection capabilities. https://github.com/youssef-faik/network-monitor-intrusion-model

## Features  
- **Real-time Network Monitoring**: Continuous capture and analysis of network traffic.  
- **Packet Inspection**: Extraction of key packet details (IP addresses, protocol types, payload analysis).  
- **Traffic Statistics**: Aggregated metrics on network activity.  
- **Intrusion Detection**: Identification of suspicious patterns such as DoS attacks and unauthorized access.  
- **User-friendly Interface**: Intuitive dashboard for visualizing network statistics and alerts.  
- **Error Handling & Notifications**: Ensures smooth application operation and informs users of critical issues.  
- **AI-Powered Intrusion Detection (WIP)**: Integration of a machine learning model to enhance intrusion detection capabilities. [GitHub Repository](https://github.com/youssef-faik/network-monitor-intrusion-model)  

Let me know if you want any modifications! 🚀

## Technologies Used
- **Programming Language**: Java
- **Framework**: JavaFX (for UI development)
- **Packet Capture Library**: Pcap4J (for real-time network traffic analysis)
- **Database**: SQLite (for storing network activity logs)
- **Build Tool**: Apache Maven (for dependency management)
- **Version Control**: Git & GitHub (for collaboration and code management)
- **Network Analysis Tool**: Wireshark (for validating captured data)

## System Architecture
The application follows the Model-View-Controller (MVC) pattern:
- **Model**: Manages network traffic data and statistics.
- **View**: Built with JavaFX, displays captured data and alerts.
- **Controller**: Handles user interactions and updates the view.
- **Service Layer**: Responsible for packet capture and intrusion detection.
- **Repository Layer**: Stores and retrieves network traffic data.

## Installation & Setup
1. Clone the repository:
   ```sh
   git clone https://github.com/AbderrahimeEl/network-monitor.git
   ```
2. Install dependencies using Maven:
   ```sh
   mvn install
   ```
3. Run the application:
   ```sh
   mvn javafx:run
   ```

## Usage
1. Select the network interface to monitor.
2. Start packet capture.
3. View real-time traffic statistics and active connections.
4. Monitor alerts for suspicious activities.
5. Use filters to analyze specific IP addresses or protocols.

## Future Improvements
- **Machine Learning Integration**: Enhance detection accuracy using AI.
- **Multi-network Support**: Extend capabilities to monitor multiple networks simultaneously.
- **Additional Protocol Support**: Expand to detect a wider range of cyber threats.


## License
This project is licensed under the MIT License. See the LICENSE file for more details.

