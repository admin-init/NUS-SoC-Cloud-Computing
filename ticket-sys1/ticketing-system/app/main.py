from flask import Flask
from auth import auth_bp
from ticket import ticket_bp
from order import order_bp
from redis_queue import ticket_worker
import threading

app = Flask(__name__)
app.register_blueprint(auth_bp)
app.register_blueprint(ticket_bp)
app.register_blueprint(order_bp)

if __name__ == "__main__":
    threading.Thread(target=ticket_worker, daemon=True).start()
    app.run(host='0.0.0.0', port=5000, debug=True)
