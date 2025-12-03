import mysql.connector
from mysql.connector import Error
import datetime
import random

# --- CONFIGURATION DE LA CONNEXION ---
HOST = "srv-bdens.insa-toulouse.fr"
PORT = 3306
USER = "projet_gei_007"
PASSWORD = "tohPhie0"
DATABASE = "projet_gei_007"

# --- SCRIPT SQL D'INITIALISATION ---
INITIALISATION_SQL = """
DROP TABLE IF EXISTS TRANSACTION;
DROP TABLE IF EXISTS SESSION_PARKING;
DROP TABLE IF EXISTS RESERVATION;
DROP TABLE IF EXISTS SENSOR_LECTURE;
DROP TABLE IF EXISTS EMPLACEMENT;

CREATE TABLE EMPLACEMENT (
    id_spot INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(10) NOT NULL UNIQUE,
    type ENUM('Standard', 'PMR', 'Electrique') NOT NULL,
    status ENUM('Libre', 'Réservé', 'Occupé') NOT NULL
);

CREATE TABLE SENSOR_LECTURE (
    id_lecture BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_spot INT NOT NULL,
    occupe BOOLEAN NOT NULL,
    horodatage DATETIME NOT NULL,
    FOREIGN KEY (id_spot) REFERENCES EMPLACEMENT(id_spot) ON DELETE CASCADE
);

CREATE TABLE RESERVATION (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    id_spot INT NOT NULL,
    plaque_immat VARCHAR(20) NOT NULL,
    heure_debut_res DATETIME NOT NULL,
    heure_fin_res DATETIME NOT NULL,
    status ENUM('Confirmée', 'Annulée', 'Terminée') NOT NULL,
    FOREIGN KEY (id_spot) REFERENCES EMPLACEMENT(id_spot) ON DELETE CASCADE
);

CREATE TABLE SESSION_PARKING (
    id_session BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_spot INT,
    plaque_immat VARCHAR(20) NOT NULL,
    heure_entree DATETIME NOT NULL,
    heure_sortie DATETIME NULL,
    prix_total DECIMAL(6,2) NULL,
    FOREIGN KEY (id_spot) REFERENCES EMPLACEMENT(id_spot) ON DELETE SET NULL
);

CREATE TABLE TRANSACTION (
    id_transaction INT AUTO_INCREMENT PRIMARY KEY,
    id_session BIGINT NOT NULL,
    montant DECIMAL(6,2) NOT NULL,
    methode_paiement VARCHAR(50) NOT NULL,
    horodatage DATETIME NOT NULL,
    status ENUM('Payé', 'Échec') NOT NULL,
    FOREIGN KEY (id_session) REFERENCES SESSION_PARKING(id_session) ON DELETE CASCADE
);
"""

# --- DONNÉES DE SIMULATION ---
EMPLACEMENTS_DATA = [
    ("A1", 'Standard', 'Libre'), ("A2", 'Standard', 'Occupé'), ("A3", 'Standard', 'Réservé'),
    ("B1", 'PMR', 'Libre'), ("C1", 'Electrique', 'Occupé')
]
PLAQUES = ['AB-123-CD', 'EF-456-GH', 'IJ-789-KL', 'MN-012-OP', 'QR-345-ST']

# --- FONCTIONS D'INSERTION ---
def insert_emplacements(cursor, emplacements):
    sql = "INSERT INTO EMPLACEMENT (nom, type, status) VALUES (%s, %s, %s)"
    cursor.executemany(sql, emplacements)
    print(f"{cursor.rowcount} emplacements insérés.")
    connection.commit()

def insert_lectures_simulation(connection, cursor):
    cursor.execute("SELECT id_spot, status FROM EMPLACEMENT")
    spots = cursor.fetchall()
    lectures = []
    now = datetime.datetime.now()
    for spot_id, status in spots:
        occupe = 1 if status == 'Occupé' else 0
        horodatage = now - datetime.timedelta(seconds=random.randint(1, 30))
        lectures.append((spot_id, occupe, horodatage.strftime('%Y-%m-%d %H:%M:%S')))
    sql = "INSERT INTO SENSOR_LECTURE (id_spot, occupe, horodatage) VALUES (%s, %s, %s)"
    cursor.executemany(sql, lectures)
    print(f"{cursor.rowcount} lectures de capteurs simulées.")
    connection.commit()
    
def insert_reservations_simulation(connection, cursor):
    cursor.execute("SELECT id_spot, status FROM EMPLACEMENT WHERE status = 'Réservé'")
    reserved_spots = cursor.fetchall()
    reservations = []
    now = datetime.datetime.now()
    for i, (spot_id, _) in enumerate(reserved_spots):
        plaque = PLAQUES[i+2] if i+2 < len(PLAQUES) else "XX-000-XX"
        start_time = now + datetime.timedelta(hours=1)
        end_time = start_time + datetime.timedelta(hours=3)
        reservations.append((spot_id, plaque, start_time.strftime('%Y-%m-%d %H:%M:%S'), 
                             end_time.strftime('%Y-%m-%d %H:%M:%S'), 'Confirmée'))
    if reservations:
        sql = "INSERT INTO RESERVATION (id_spot, plaque_immat, heure_debut_res, heure_fin_res, status) VALUES (%s, %s, %s, %s, %s)"
        cursor.executemany(sql, reservations)
        print(f"{cursor.rowcount} réservations simulées insérées.")
    connection.commit()

def insert_sessions_et_transactions_simulation(connection, cursor):
    sessions = []
    heure_entree_en_cours = datetime.datetime.now() - datetime.timedelta(hours=random.randint(1, 5))
    sessions.append((2, PLAQUES[0], heure_entree_en_cours.strftime('%Y-%m-%d %H:%M:%S'), None, None))
    
    heure_entree_terminee = datetime.datetime.now() - datetime.timedelta(days=1, hours=3)
    heure_sortie_terminee = heure_entree_terminee + datetime.timedelta(hours=random.randint(2, 6))
    sessions.append((5, PLAQUES[1], heure_entree_terminee.strftime('%Y-%m-%d %H:%M:%S'), 
                     heure_sortie_terminee.strftime('%Y-%m-%d %H:%M:%S'), 12.50))
    
    sql_session = "INSERT INTO SESSION_PARKING (id_spot, plaque_immat, heure_entree, heure_sortie, prix_total) VALUES (%s, %s, %s, %s, %s)"
    cursor.executemany(sql_session, sessions)
    connection.commit()
    
    cursor.execute("SELECT id_session FROM SESSION_PARKING WHERE heure_sortie IS NOT NULL LIMIT 1")
    res = cursor.fetchone()
    if res:
        session_terminee_id = res[0]
        transactions = [
            (session_terminee_id, 12.50, 'Carte Bancaire', heure_sortie_terminee.strftime('%Y-%m-%d %H:%M:%S'), 'Payé')
        ]
        sql_transaction = "INSERT INTO TRANSACTION (id_session, montant, methode_paiement, horodatage, status) VALUES (%s, %s, %s, %s, %s)"
        cursor.executemany(sql_transaction, transactions)
        print("Sessions de parking et transactions simulées insérées.")
        connection.commit()

# --- LOGIQUE PRINCIPALE ---
connection = None
try:
    connection = mysql.connector.connect(
        host=HOST, port=PORT, user=USER, password=PASSWORD, database=DATABASE,
        use_pure=True
    )

    if connection.is_connected():
        print("Connexion réussie à la base de données.")
        cursor = connection.cursor()

        # --- CORRECTION ICI : DÉCOUPAGE MANUEL ---
        print("Création du schéma en cours...")
        # On divise le string par ';' et on exécute chaque commande séparément
        sql_commands = INITIALISATION_SQL.split(';')
        for command in sql_commands:
            if command.strip():
                try:
                    cursor.execute(command)
                except Error as err:
                    # On ignore les erreurs mineures comme DROP TABLE si la table n'existe pas
                    print(f"Note sur l'exécution SQL : {err}")

        connection.commit()
        print("Schéma de la DB 'Parking Connecté' créé avec succès.")

        # 2. Insertion des données
        print("\n--- Démarrage de la simulation de données ---")
        insert_emplacements(cursor, EMPLACEMENTS_DATA)
        insert_lectures_simulation(connection, cursor)
        insert_reservations_simulation(connection, cursor)
        insert_sessions_et_transactions_simulation(connection, cursor)
        print("\n--- Simulation de données terminée avec succès ---")

except Error as e:
    print(f"Erreur CRITIQUE: {e}")

finally:
    if connection and connection.is_connected():
        cursor.close()
        connection.close()
        print("Connexion fermée.")