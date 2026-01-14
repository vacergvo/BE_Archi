# BE_Archi
Overleaf project: https://www.overleaf.com/4983453471tnhnqtyzdbdq#8d1400
<img width="1900" height="1079" alt="image" src="https://github.com/user-attachments/assets/b881ba1f-4ba7-4ed7-a1e1-416e90165af7" />

## FLOW PRINCIPLE:
Case - 1 Arrival with reservation:
-    **Reservation service** <--> **Parking spot service**: reserve puis reponse avec le num de place (qui passe en etat reserved)  et un id de reservation pour l'user qui va devoir presenter au portail
-    **Entry/Exit service** <--> **Reservation Service**: on presente lid de reservation et on check dans la base si cest valide (on part du principe que cest valide). On renvoie le ticket a luser avec le num de place. a ce stade lid de reservation ne sert plus a rien
-    **Entry/Exit service** <--> **Parking spot service**: on set la place de l'etat reserved a occupied et on ouvre la gate
-    **Payment service** <--> **Entry/Exit service**: lorsque lon veut sortir, on donne en entree de la machine l'id de la place et on renvoie le montant à payer
-    **Payment service** <--> **Entry/Exit service**: on paye (on part du principe que ca marche toujours)
-    **Entry/Exit service** <--> **Parking spot service** on set la place de la personne qui vient de partir en 'free'
-    
Case - 2 Arrival without reservation:
-    **Entry/Exit service** <--> **Parking spot service**: on ne donne pas dargument (de reserve id) donc on cherche directement dans parking spot si il y a une place dans letat (free)
-    **Entry/Exit service** <--> **Parking spot service**: on set la place de l'etat free a occupied et on ouvre la gate (attention si pas de place alors on refuse)
-    **Payment service** <--> **Entry/Exit service**: lorsque lon veut sortir, on donne en entree de la machine l'id de la place et on renvoie le montant à payer
-    **Payment service** <--> **Entry/Exit service**: on paye (on part du principe que ca marche toujours)
-    **Entry/Exit service** <--> **Parking spot service** on set la place de la personne qui vient de partir en 'free'
<img width="754" height="603" alt="image" src="https://github.com/user-attachments/assets/983e1b85-16dc-4fcb-987f-f627d4626cdd" />


#  Reservation
-    **1** Reservation Service --> Parking Spot Service: reserve(requestedSpotId)
-    Parking Spot Service --> Reservation Service: reserved(reservationId, assignedSpotId)

-    **2** Entry and Exit Service --> Reservation Service: validate(reservationId, TimerOn)
-    Reservation Service --> Entry and Exit Service: valid(reservationId, availableSpotId)   always validate

#  W/O Reservation
-    **1bis** Entry and Exit Service --> Parking Spot Service: findFree()   idea of function
-    Parking Spot Service --> Entry and Exit Service: freeSpot(availableSpotId) or none

#  Always (if a parking spot is available)
-    **3** Entry and Exit Service --> Parking Spot Service: occupy(spotId)
-    Parking Spot Service --> Entry and Exit Service: occupied(spotId)

-    **4** Entry and Exit Service --> Payment Service: quote(spotId)
-    Payment Service --> Entry and Exit Service: amountDue
-    **4** Entry and Exit Service --> Payment Service: pay(spotId, amount)
-    Payment Service --> Entry and Exit Service: accepted

-    **5** Entry and Exit Service --> Parking Spot Service: free(spotId)
-    Parking Spot Service --> Entry and Exit Service: freed(spotId)


