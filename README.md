# BE_Archi

### 1 Sensor Service
-  **Collects data from parking sensors** (simulated via REST calls).
-  Tracks whether a **spot is occupied or free**.

### 2 Parking Spot Service
-  Manages the **list of parking spots**.
-  Keeps track of spot status (**free**, **reserved**, **occupied**).

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
