# BE_Archi

### 1 Sensor Service
-  **Collects data from ALL parking sensors** (simulated via REST calls) so they can be sent to parking spot service

### 2 Parking Spot Service
-  Manages the **list of parking spots** with their states.
-  Keeps track of spot status (**free**, **reserved**, **occupied**) depending on **Sensor Service**.

### 3 Reservation Service
-  Allows users to **reserve a spot** in advance
-  Integrates with Parking Spot Service to **mark spots as reserved**.   

### 4 Payment Service
-  **Handles billing** (hourly/daily rates??).
-  Supports different **payment methods** (simulated)

### 5 Entry/Exit Service
-  Controls **barriers/gate**
-  **Opens gate** (or refuse) when payment/reservation is **validated**.
-  Logs **entry and exit times for billing**.





## FLOW PRINCIPLE:
**Sensor Service** detects car >> sends update to **Parking Spot Service**.
**Parking Spot Service** marks spot as occupied.
**Reservation Service** checks if spot was reserved.
**Payment Service** calculates fee when car exits.
**Entry/Exit Service** decides whether to open gate.
