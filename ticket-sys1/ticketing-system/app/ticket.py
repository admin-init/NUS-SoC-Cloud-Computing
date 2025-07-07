from flask import Blueprint, request, jsonify
from redis_queue import enqueue_ticket
from database import get_db

ticket_bp = Blueprint('ticket', __name__)

@ticket_bp.route('/tickets', methods=['GET'])
def list_tickets():
    db = get_db()
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM tickets WHERE available > 0")
    return jsonify(cursor.fetchall())

@ticket_bp.route('/book', methods=['POST'])
def book_ticket():
    data = request.json
    ticket_id = data['ticket_id']
    user_id = data['user_id']
    enqueue_ticket(ticket_id, user_id)
    return jsonify({"msg": "queued for booking"})
