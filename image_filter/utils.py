import logging

def set_logger(logger):
    handler =  logging.FileHandler(f"{logger.name}.log", 'w+')
    formatter = logging.Formatter("%(asctime)s : %(levelname)s : %(message)s")
    handler.setFormatter(formatter)
    logger.addHandler(handler)
    logger.addHandler(logging.StreamHandler())
    logger.setLevel(logging.DEBUG)
    return logger
