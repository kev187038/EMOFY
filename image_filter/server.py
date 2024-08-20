#Flask server for an API that applies filters to images
import io
import os
import cv2
import base64
import logging
import numpy as np
from Image import Image
from logger import set_logger
from flask import Flask, request, jsonify
from flask_cors import CORS 


logger = set_logger('image_filter_server')
logger.info('Server started')

app = Flask(__name__)
CORS(app, resources={r"/apply_filter": {"origins": "http://localhost:8081"}})

@app.route('/apply_filter', methods=['POST'])
def apply_filter():
    logger.info(f'[EMOFY] : Filter requested by {request.remote_addr}')
    try:
        data = request.get_json()
        image = Image(data['image'])
        filter = data['filter']
        filtered_image = image.apply_filter(filter)
        logger.info(f'[EMOFY] : Sending filtered image to {request.remote_addr}')
        return jsonify({'filtered_image': filtered_image})
    except Exception as e:
        logger.error(f'Error: {str(e)}')
        return jsonify({'error': str(e)}), 400

if __name__ == '__main__':
    from waitress import serve
    serve(app, host='0.0.0.0', port=5000)
