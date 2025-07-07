from flask import Blueprint, request, jsonify
from database import get_db

order_bp = Blueprint('order', __name__)

@order_bp.route('/orders/<int:user_id>', methods=['GET'])
def user_orders(user_id):
    db = get_db()
    cursor = db.cursor(dictionary=True)
    cursor.execute("SELECT * FROM orders WHERE user_id=%s", (user_id,))
    return jsonify(cursor.fetchall())
