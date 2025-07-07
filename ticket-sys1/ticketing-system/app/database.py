import mysql.connector

def get_db():
    conn = mysql.connector.connect(
        host='mysql',   # host='mysql'
        user='root',        # user='root'
        password='123456',
        database='ticketdb'
    )
    return conn
