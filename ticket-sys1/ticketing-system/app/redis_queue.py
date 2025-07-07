import redis
import threading
from database import get_db

r = redis.Redis(host='127.0.0.1', port=6379, decode_responses=True)
# host='redis'

def enqueue_ticket(ticket_id, user_id):
    r.lpush('ticket_queue', f"{ticket_id}:{user_id}")

def ticket_worker():
    while True:
        job = r.brpop('ticket_queue')[1]
        ticket_id, user_id = job.split(':')
        db = get_db()
        cursor = db.cursor()
        cursor.execute("SELECT available FROM tickets WHERE id=%s", (ticket_id,))
        available = cursor.fetchone()[0]
        if available > 0:
            cursor.execute("UPDATE tickets SET available = available - 1 WHERE id=%s", (ticket_id,))
            cursor.execute("INSERT INTO orders (user_id, ticket_id) VALUES (%s, %s)", (user_id, ticket_id))
            db.commit()

threading.Thread(target=ticket_worker, daemon=True).start()
