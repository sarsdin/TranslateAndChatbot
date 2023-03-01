# from fastapi.middleware.cors import CORSMiddleware
from fastapi import FastAPI
from route import router as api_router
# from ReqApi import *
# import chat
# from routers.supply_change import predict
# from starlette.middleware import BaseHTTPMiddleware
# from .middleware import add_process_time_header

import logging
logging.basicConfig()
logging.getLogger('sqlalchemy.engine').setLevel(logging.DEBUG)

app = FastAPI()

# app.add_middleware(BaseHTTPMiddleware, dispatch=add_process_time_header)
# app.include_router(predict.router)
app.include_router(api_router)




# gpt = chat.MyGpt()
# chat.gptStart()

if __name__ == '__main__':
    print('PyCharm')




# @app.get("/")
# def read_root():
#     return {
#         "Hello": "World"
#     }
