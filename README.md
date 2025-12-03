# BE_Archi

### 1 Sensor Service
This service represents the physical sensors for each bay. Its goal is to collect raw observation (presence) and publish it so that other services can decide what to do. It owns the catalogue of sensors. It exposes a simple API to register sensors and to post readings that we will simulate. It does not decide whether a bay is free or occupied. It pushes notifications about a change in state observed. The **Parking spot service** is the primary consumer of these notifications. If you keep an event stream, the History service will subscribe as well.

### 2 Parking Spot Service
Its goal is to manage the state machine of a spot with the states free, reserved, and occupied, and to enforce valid transitions. It owns the list of spots, links each spot to a sensor, and stores timestamps and optional reservation holds. It exposes narrow operations such as reserve, occupy, and free. It never writes into other services. Instead, other services call it to request a transition. It consumes sensor notifications as hints and may choose to act on them. It is called by **Reservation** to place or cancel a hold, by **Entry and Exit service** to release a bay when the car enters/leaves. It emits spot status changed events for the History view.

### 3 Reservation Service
This service lets a user book a bay before arrival. Its goal is to allocate a spot for a time window and protect it from walk-ins IMPORTANT. It owns reservations and their life cycle with states such as pending, confirmed, and cancelled. When a reservation is created it calls Parking spot to reserve a specific bay with a time to live so a stale reservation expires automatically. When a reservation is cancelled or expires it requests the spot to be freed. Entry calls this service to validate a reservation at the gate. The service publishes reservation confirmed and reservation cancelled events for History. It never flips a bay to occupied on its own because only Entry can do that once the car is physically in. Reservation expires after XX hours if no vehicule is entering.

### 4 Payment Service
This service prices and settles a parking session. Its goal is to compute the amount to pay. It owns payments keyed by session. When Entry records an arrival it creates a payment that is due. We assumes the user always pay. When Exit is requested it is the Payment service that confirms whether the session is paid. It never changes bay state and never controls the gate. It emits payment settled events that the History service can show.

### 5 Entry/Exit Service
This is the gatekeeper. Its goal is to control the barrier and reconcile the real world with the logical state. On entry without a reservation it asks Parking spot for a free unreserved bay and then requests the occupy transition. If the transition succeeds it opens the gate and records a new parking session. On entry with a reservation it validates the booking window and then requests the occupy transition for that reserved bay. On exit it checks that payment has been settled then requests the free transition and opens the gate. It owns parking sessions with entry and exit times and a pointer to the bay and optional reservation. It calls Payment to open a bill at entry and to check status at exit. It emits gate opened, gate refused, entry recorded, and exit recorded events.


## FLOW PRINCIPLE:
Case - 1 Arrival with reservation:
**Reservation service** <--> **Parking spot service**: reserve puis reponse avec le num de place (etat reserved)  et un id de reservation
**Entry/Exit service** <--> **Reservation Service**: on presente lid de reservation et on check dans la base si cest valide (on part du principe que cest valide)
**Entry/Exit service** <--> **Parking spot service**: on set la place de l'etat reserved a occupied et on ouvre la gate
**Payment service** <--> **Entry/Exit service**: lorsque lon veut sortir, on donne en entree de la machine l'id de la place et on renvoie le montant à payer
**Payment service** <--> **Entry/Exit service**: on paye (on part du principe que ca marche toujours)
**Entry/Exit service** <--> **Parking spot service** on set la place de la personne qui vient de partir en 'free'
<img width="673" height="562" alt="image" src="https://github.com/user-attachments/assets/27bf47ce-686d-4f80-aa0a-df21840c3cf9" />

