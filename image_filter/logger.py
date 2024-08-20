import logging
import logging.handlers

def set_logger(name):
    #Set two handlers one for console and the other for logstash
    logger = logging.getLogger(name)
    logstash_handler = logging.handlers.SocketHandler("logstash", 5044)
    handler =  logging.FileHandler(f"{name}.log", 'w+')
    formatter = logging.Formatter("%(asctime)s : %(levelname)s : %(message)s")
    logstash_handler.setFormatter(formatter)
    handler.setFormatter(formatter)
    logger.addHandler(logstash_handler)
    logger.addHandler(handler)  
    logger.addHandler(logging.StreamHandler())
    logger.setLevel(logging.DEBUG)
    return logger
    


