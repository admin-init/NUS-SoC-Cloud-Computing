import mysql.connector

def get_db():
    conn = mysql.connector.connect(
        host='127.0.0.1',   # host='mysql'
        user='ticket_user',        # user='root'
        password='123456',
        database='ticketdb'
    )
    return conn
